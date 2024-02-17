package com.example.geminiapp.chatComposables

import ResponseComposable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.geminiapp.GeminiViewModel
import com.google.ai.client.generativeai.type.Content

@Composable
fun ChatContent(modifier: Modifier, viewModel: GeminiViewModel){
    val history = viewModel.history
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
    ) {

        items(history) { chatItem ->
            ChatItem(content = chatItem)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
    //ResponseComposable(response = viewModel.state.response)
}