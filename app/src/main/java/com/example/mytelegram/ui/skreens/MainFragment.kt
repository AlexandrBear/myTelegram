package com.example.mytelegram.ui.skreens

import androidx.fragment.app.Fragment
import com.example.mytelegram.R
import com.example.mytelegram.utilits.APP_ACTIVITY
import com.example.mytelegram.utilits.hideKeyboard


class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "MyTelegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()

    }
}