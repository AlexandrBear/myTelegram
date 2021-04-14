package com.example.mytelegram.ui.skreens.groups

import androidx.recyclerview.widget.RecyclerView
import com.example.mytelegram.R
import com.example.mytelegram.models.CommonModel
import com.example.mytelegram.ui.skreens.base.BaseFragment
import com.example.mytelegram.utilits.APP_ACTIVITY
import com.example.mytelegram.utilits.hideKeyboard
import com.example.mytelegram.utilits.showToast
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var listContacts: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
        create_group_btn_complete.setOnClickListener { showToast("click") }
        create_group_input_name.requestFocus()
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_recycle_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach{ mAdapter.updateListItems(it) }
    }
}