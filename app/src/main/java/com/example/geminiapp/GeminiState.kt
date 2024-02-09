package com.example.geminiapp

class GeminiState {
    fun copy(isLoading: Boolean = false, userInput: String = "", response: String = ""): GeminiState {
        val newState = GeminiState()
        newState.isLoading = isLoading
        newState.userInput = userInput
        newState.response = response
        return newState
    }

    var userInput: String = ""
    var isLoading: Boolean = false
    var response: String = ""
}
