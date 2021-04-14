package com.example.mytelegram.ui.skreens.groups

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mytelegram.R
import com.example.mytelegram.database.*
import com.example.mytelegram.models.CommonModel
import com.example.mytelegram.utilits.APP_ACTIVITY
import com.example.mytelegram.utilits.AppValueEventListener
import com.example.mytelegram.utilits.hideKeyboard
import com.example.mytelegram.utilits.replaceFragment
import kotlinx.android.synthetic.main.fragment_add_contacts.*



class AddContactsFragment : Fragment(R.layout.fragment_add_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Добавить участника"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            listContacts.forEach {
                replaceFragment(CreateGroupFragment(listContacts))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = add_contacts_recycle_view
        mAdapter = AddContactsAdapter()

        // 1-ый запрос
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener {dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->

                //2-ой запрос
                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener {dataSnapshot1 ->
                    val newModel = dataSnapshot1.getCommonModel()

                    //3-ий запрос
                    mRefMessages.child(model.id).limitToLast(1)
                        .addListenerForSingleValueEvent(AppValueEventListener {dataSnapshot2 ->
                            val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                            if(tempList.isEmpty()){
                                newModel.lastMessage = "Чат очищен"
                            }else{
                                newModel.lastMessage = tempList[0].text
                            }

                            if (newModel.fullname.isEmpty()){
                                newModel.fullname = newModel.phone
                            }
                            mAdapter.updateListItems(newModel)
                        })
                })
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    companion object{
        val listContacts = mutableListOf<CommonModel>()
    }
}