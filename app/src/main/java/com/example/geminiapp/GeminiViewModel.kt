package com.example.geminiapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GeminiViewModel(model: GenerativeModel): ViewModel() {
    var state by mutableStateOf(GeminiState())
    var history = mutableListOf<Content>()
    private val chat = model.startChat()
    fun onClick(prompt: String){
        if(state.userInput ==null || state.isLoading){
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)
            try {
                var response = ""
                history.add(content(role = "user"){text(prompt)})

                chat.sendMessageStream(prompt).collect{chunk->
                    response+=chunk.text
                    state = state.copy(response = response)
                    Log.d("GeminiViewModel", "Response: $response")
                }
                history.add(content(role = "model"){text(response)})


                history.forEach {
                    it.parts.forEach {part->
                        Log.d("GeminiViewModel", "History: ${part.asTextOrNull()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiViewModel", "Error: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }
}