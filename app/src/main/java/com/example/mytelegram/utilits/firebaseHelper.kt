package com.example.mytelegram.utilits

import com.example.mytelegram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

lateinit var AUTH:FirebaseAuth
lateinit var UID:String
lateinit var REF_DATA_ROOT:DatabaseReference
lateinit var USER:User

const val NODE_USERS = "users"
const val CHILD_ID = "id"
const val CHIlD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"


fun initFirebase(){
    AUTH = FirebaseAuth.getInstance()
    REF_DATA_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
}