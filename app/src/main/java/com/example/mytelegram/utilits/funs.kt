package com.example.mytelegram.utilits

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mytelegram.R
import com.example.mytelegram.models.CommonModel
import com.squareup.picasso.Picasso

//Файл утилитных функций доступных во вмем приложении

fun showToast(message:String){
    //Функция показывает сообщение
    Toast.makeText(APP_ACTIVITY,message,Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity){
    //Функция расширения для AppCompactActivity, позволяет запускать активити
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addStack:Boolean = true){
    //Функция расширения для AppCompactActivity, позволяет устанавливать фрашменты
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

fun initContacts() {
    //Функция считывает контакты с телефонной книги, заполняет массив arrayContacts
    if (checkPermission(READ_CONTACTS)){
        var arrayContackts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let{
            while (cursor.moveToNext()){
                //Читаем телефонную книгу пока есть следуюшие элементы
                val fullName = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"),"")
                arrayContackts.add(newModel)

            }
        }
        cursor?.close()
        updatePhonesToDatabase(arrayContackts)
    }
}
