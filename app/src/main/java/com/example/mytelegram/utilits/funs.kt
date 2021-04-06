package com.example.mytelegram.utilits

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mytelegram.R
import com.squareup.picasso.Picasso


fun showToast(message:String){
    Toast.makeText(APP_ACTIVITY,message,Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity){
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addStack:Boolean = true){
    if (addStack){
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_conteiner,
                fragment
            ).commit()
    } else {
        supportFragmentManager.beginTransaction()
            .replace(R.id.data_conteiner,
                fragment
            ).commit()
    }

}
fun Fragment.replaceFragment(fragment: Fragment){
    //Фунция расширения для Fragment, позволяет устанавливать фрагмент
    this.fragmentManager?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(R.id.data_conteiner,
            fragment
        )?.commit()
}
fun hideKeyboard(){
    //Функция скрывает клавиатуру
    val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken,0)
}
fun ImageView.downloadAndSetImage(url:String){
    //Функция расширения ImageView,скачивает и устанавливает картинку
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.default_photo)
        .into(this)
}
