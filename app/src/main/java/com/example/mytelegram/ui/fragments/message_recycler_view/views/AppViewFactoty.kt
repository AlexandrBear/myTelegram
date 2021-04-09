package com.example.mytelegram.ui.fragments.message_recycler_view.views

import com.example.mytelegram.models.CommonModel
import com.example.mytelegram.utilits.TYPE_MESSAGE_IMAGE
import com.example.mytelegram.utilits.TYPE_MESSAGE_VOICE

class AppViewFactoty {
    companion object{
        fun getView(message:CommonModel):MessageView{
            return when(message.type){
                TYPE_MESSAGE_IMAGE -> ViewImageMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                else -> ViewTextMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.text
                )
            }
        }
    }
}