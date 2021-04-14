package com.example.mytelegram.ui.skreens.base


import androidx.fragment.app.Fragment
import com.example.mytelegram.utilits.APP_ACTIVITY

open class BaseFragment(layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }
}