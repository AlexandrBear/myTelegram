package com.example.mytelegram.ui.message_recycler_view.view_holder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mytelegram.database.CURRENT_UID
import com.example.mytelegram.ui.message_recycler_view.views.MessageView
import com.example.mytelegram.utilits.asTime
import kotlinx.android.synthetic.main.message_item_text.view.*

class HolderTextMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blocUserMessage: ConstraintLayout = view.bloc_user_message
    private val chatUserMessage: TextView = view.chat_user_message
    private val chatUserMessageTime: TextView = view.chat_user_message_time
    private val blocReceiveMessage: ConstraintLayout = view.bloc_received_message
    private val chatReceivedMessage: TextView = view.chat_received_message
    private val chatReceivedMessageTime: TextView = view.chat_received_message_time


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocUserMessage.visibility = View.VISIBLE
            blocReceiveMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blocUserMessage.visibility = View.GONE
            blocReceiveMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }
    override fun onAttach(view: MessageView) {

    }

    override fun onDettach() {

    }
}