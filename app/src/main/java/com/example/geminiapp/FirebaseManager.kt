package com.example.geminiapp

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseManager {
    private val database = FirebaseDatabase.getInstance()
    private val chatsRef = database.getReference("chats")

    fun createChat(userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit){
        Log.d("check", "FirebaseManager.createChat: userId $userId and chatsRef ${chatsRef}")
        chatsRef.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    chatsRef.child(userId).setValue(true)
                }
                val userChats = chatsRef.child(userId)
                val chatId = userChats.push().key
                val chatMap = mapOf(
                    "topic" to "New Chat",
                )
                userChats.child(chatId!!).setValue(chatMap).addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        Log.d("check", "FirebaseManager.createChat: chatId $chatId")
                        onSuccess(chatId)
                    } else {
                        onError(Exception("Chat creation failed"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(Exception("Error creating chat: ${error.message}"))
            }

        })

    }
    fun retrieveChats(userId: String, callback: (List<Chat>?) -> Unit){
        val userChats = chatsRef.child(userId)
        userChats.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = mutableListOf<Chat>()
                if (!snapshot.exists()) {
                    userChats.child(userId).setValue(true)
                }
                for (chatSnapshot in snapshot.children) {
                    val chatId = chatSnapshot.key.orEmpty()
                    val topic = chatSnapshot.child("topic").getValue(String::class.java).orEmpty()
                    chatList.add(Chat(topic = topic, chatId = chatId))
                }
                callback(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }

        })
    }
    fun sendMessage(sender: String, message: String, chatId: String, userId: String){
        val chatRef = chatsRef.child(userId).child(chatId)
        chatRef.child("messages").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("messages").setValue(true)
                }
                val messagesRef = chatRef.child("messages")
                val messageId = messagesRef.push().key
                val messageMap = mapOf(
                    "role" to sender,
                    "messageText" to message,
                )

                messagesRef.child(messageId!!).setValue(messageMap)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
    fun retrieveMessages(chatId: String, userId: String,callback: (List<Message>) -> Unit) {
        val messagesRef = chatsRef.child(userId).child(chatId).child("messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    Log.d("check", "retrieveMessages: "+messageSnapshot.key+" "+message?.role+" "+message?.messageText)
                    messages.add(message!!)
                }
                callback(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}