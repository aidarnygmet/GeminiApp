package com.example.geminiapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val _currentUser = mutableStateOf<String?>(null)
    val currentUser: State<String?> = _currentUser
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> get() = _chats
    fun setCurrentUser(user: String?) {
        _currentUser.value = user
    }
    fun setChats(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val firebaseManager = FirebaseManager()
            currentUser.value?.let {
                firebaseManager.retrieveChats(it){ retrievedChats->
                    if (retrievedChats != null) {
                        _chats.value = retrievedChats.reversed()
                        Log.d("check", "chats are retrieved: ${currentUser.value}, ${_chats.value.size}")
                        callback(true)
                    } else {
                        callback(false)
                        Log.d("check", "Error Chats")
                    }
                }
            }
        }
    }
}