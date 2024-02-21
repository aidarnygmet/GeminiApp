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
    fun setCurrentUser(user: String) {
        _currentUser.value = user
    }
    fun getCurrentUser(): String{
        return _currentUser.value!!
    }
    fun setChats() {
        viewModelScope.launch {
            val firebaseManager = FirebaseManager()
            currentUser.value?.let {
                firebaseManager.retrieveChats(it){ retrievedChats->
                    if (retrievedChats != null) {
                        _chats.value = retrievedChats
                        Log.d("check", "User View Model ${retrievedChats.size}")
                    } else {
                        Log.d("check", "Error Chats")
                    }
                }
            }
        }
    }
}