package com.example.geminiapp

data class Message(
    val role: String,
    val messageText: String
){
    constructor() : this("", "")
}
