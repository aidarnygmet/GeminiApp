package com.example.geminiapp

sealed class Screen(val route: String){
    object Chat: Screen("chat")
    object Chats: Screen("chats")
    fun withArgs(vararg args: String): String{
        return buildString{
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}
