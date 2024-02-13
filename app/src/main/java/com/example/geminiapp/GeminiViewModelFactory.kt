package com.example.geminiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.ai.client.generativeai.GenerativeModel

class GeminiViewModelFactory(private val model: GenerativeModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeminiViewModel::class.java)) {
            return GeminiViewModel(model) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}