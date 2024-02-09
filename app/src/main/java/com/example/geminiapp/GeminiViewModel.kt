package com.example.geminiapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeminiViewModel(): ViewModel() {
    var state by mutableStateOf(GeminiState())

    fun onClick(model: GenerativeModel, prompt: String){
        if(state.userInput ==null || state.isLoading){
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)

            val response = model.generateContent(prompt)
            Log.d("GeminiViewModel", "onClick: response: ${response.text}")
            state = state.copy(isLoading = false, response = response.text?:"No response")
            Log.d("GeminiViewModel", "onClick: state.response: ${state.response}")
        }
    }
}