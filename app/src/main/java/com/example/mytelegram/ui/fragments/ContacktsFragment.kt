package com.example.mytelegram.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mytelegram.R
import com.example.mytelegram.utilits.APP_ACTIVITY


class ContacktsFragment : BaseFragment(R.layout.fragment_contackts) {
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
    }
}