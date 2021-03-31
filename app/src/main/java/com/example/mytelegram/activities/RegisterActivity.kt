package com.example.mytelegram.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.mytelegram.R
import com.example.mytelegram.databinding.ActivityRegisterBinding
import com.example.mytelegram.ui.fragments.EnterPhoneNumberFragment
import com.example.mytelegram.utilits.initFirebase
import com.example.mytelegram.utilits.replaceFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        mToolbar = mBinding.registerToolbar
        setSupportActionBar(mToolbar)
        title = getString(R.string.register_title_your_phonr)
        replaceFragment(EnterPhoneNumberFragment(), false)
    }
}