package com.example.mytelegram.ui.skreens.groups

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mytelegram.R
import com.example.mytelegram.database.*
import com.example.mytelegram.models.CommonModel
import com.example.mytelegram.models.UserModel
import com.example.mytelegram.ui.skreens.base.BaseFragment
import com.example.mytelegram.ui.message_recycler_view.views.AppViewFactoty
import com.example.mytelegram.ui.skreens.main_list.MainListFragment
import com.example.mytelegram.utilits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.choice_upload.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChatFragment(private val group: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: GroupChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 10
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_choice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chat_input_massage.addTextChangedListener(AppTextWatcher {
            val string = chat_input_massage.text.toString()
            if (string.isEmpty() || string == "Запись") {
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })

        chat_btn_attach.setOnClickListener { attach() }


        CoroutineScope(Dispatchers.IO).launch {
            chat_btn_voice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //TODO record
                        chat_input_massage.setText("Запись")
                        chat_btn_voice.setColorFilter(
                            ContextCompat.getColor(APP_ACTIVITY, R.color.primary)
                        )
                        val messageKey = getMessageKey(group.id)
                        mAppVoiceRecorder.startRecord(messageKey)

                    } else if (event.action == MotionEvent.ACTION_UP) {
                        // TODO stop record
                        chat_input_massage.setText("")
                        chat_btn_voice.colorFilter = null
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                group.id,
                                TYPE_MESSAGE_VOICE
                            )
                            mSmoothScrollToPosition = true
                        }
                    }
                }
                true
            }

        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        btn_attach_file.setOnClickListener { attachFile() }
        btn_attach_image.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecycleView() {
        mRecyclerView = chat_recycle_view
        mAdapter = GroupChatAdapter()
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_GROUPS)
            .child(group.id)
            .child(NODE_MESSAGES)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(AppViewFactoty.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(AppViewFactoty.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)


        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                println(mRecyclerView.recycledViewPool.getRecycledViewCount(0))
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            // Слушает идет скролинг или нет
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT
            .child(NODE_USERS)
            .child(group.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        chat_btn_send_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = chat_input_massage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessageToGroup(
                message,
                group.id,
                TYPE_TEXT
            ) {
                chat_input_massage.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_chat_fullname.text = group.fullname
        } else mToolbarInfo.toolbar_chat_fullname.text = mReceivingUser.fullname

        mToolbarInfo.toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.toolbar_chat_status.text = mReceivingUser.state
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Активномть которая запускается для получения картинки для фото пользователя
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri: Uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(group.id)
                    uploadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(group.id)
                    val filename = getFilenameFromUri(uri!!)
                    uploadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_FILE, filename)
                    mSmoothScrollToPosition = true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Создание выпадающего меню
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Слушатель выбора выпадающего меню
        when (item.itemId) {
            R.id.menu_clear_chat -> clearCgat(group.id) {
                showToast("Чат очищен")
                replaceFragment(MainListFragment())
            }
            R.id.menu_delete_chat -> deleteChat(group.id) {
                showToast("Чат удален")
                replaceFragment(MainListFragment())
            }
        }
        return true
    }
}