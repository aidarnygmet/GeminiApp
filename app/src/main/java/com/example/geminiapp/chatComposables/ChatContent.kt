package com.example.geminiapp.chatComposables

import ResponseComposable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.geminiapp.FirebaseManager
import com.example.geminiapp.GeminiViewModel
import com.example.geminiapp.Message
import com.google.ai.client.generativeai.type.Content

@Composable
fun ChatContent(modifier: Modifier, viewModel: GeminiViewModel){
    var messages by remember { mutableStateOf(emptyList<Message>()) }
    val firebaseManager = FirebaseManager()
    LaunchedEffect(Unit) {
        firebaseManager.retrieveMessages(viewModel.chatId, viewModel.userId) { retrievedMessages ->
            messages = retrievedMessages
        }
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
    ) {

        items(messages) { it ->
            ChatItem(message = it)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
    //ResponseComposable(response = viewModel.state.response)
}