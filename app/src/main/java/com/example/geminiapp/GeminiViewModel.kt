package com.example.geminiapp

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GeminiViewModel(model: GenerativeModel, chatId: String, userId: String): ViewModel() {
    val model = model
    val chatId = chatId
    val userId = userId
    private val firebaseManager = FirebaseManager()
    var state by mutableStateOf(GeminiState())
    var history = mutableListOf<Content>()
    lateinit var chat : Chat
    private val _topic = MutableStateFlow<String>("")
    val topic: StateFlow<String> get() = _topic
    init {
        firebaseManager.retrieveMessagesAtOnce(chatId = chatId, userId = userId) { retrievedMessages ->
            retrievedMessages.map {
                history.add(content(role = it.role) { text(it.messageText) })
            }
            chat = model.startChat(history = history)
        }
        firebaseManager.retrieveTopic(chatId = chatId, userId = userId){
            if(it == "fail"){
                updateTopic("Fail")
                Log.d("check", "topic retrieval failed")
            } else {
                updateTopic(it)
                Log.d("check", "topic retrieval ${topic.value}")
            }

        }
    }
    private fun updateTopic(topic: String){
        _topic.value = topic
    }
    fun onClick(prompt: String){
        if(state.userInput ==null || state.isLoading){
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)
            try {
                if(history.isEmpty()){
                    val topic = model.generateContent("Generate one short sentence topic for this chat: $prompt").text.toString()
                    firebaseManager.updateTopic(chatId, userId, topic) { res ->
                        if (res) {
                            Log.d("check", "Topic update successful")
                        } else {
                            Log.d("check", "Topic update failed")
                        }
                    }
                }
                firebaseManager.sendMessage("user", prompt, chatId, userId)
                val response = chat.sendMessage(prompt).text.toString()
                Log.d("check", "response: $response")
                firebaseManager.sendMessage("model", response, chatId, userId)
            } catch (e: Exception) {
                Log.e("GeminiViewModel", "Error: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }
}