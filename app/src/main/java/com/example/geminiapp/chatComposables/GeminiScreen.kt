package com.example.geminiapp.chatComposables

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geminiapp.BuildConfig
import com.example.geminiapp.GeminiViewModel
import com.example.geminiapp.GeminiViewModelFactory
import com.google.ai.client.generativeai.GenerativeModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiScreen(viewModel: GeminiViewModel) {
    var userInput by remember { mutableStateOf("") }
    var response  = viewModel.state.response
    var isInputFocused by remember { mutableStateOf(false) }
    Scaffold(bottomBar = {ChatBottomBar(
        onInputFocused = { isInputFocused = it },
        onSendClicked = { viewModel.onClick(it) }
    )},
        topBar = {
            Text("Gemini")
        },
        content = {
            ChatContent(modifier = Modifier.fillMaxSize().padding(bottom = 68.dp), viewModel = viewModel)
        }
    )
//    Column {
//        Text("Enter your request:")
//        TextField(
//            value = userInput,
//            onValueChange = { userInput = it },
//            label = { Text("Request") }
//        )
//        Button(onClick = {
//            // Make your API call here using userInput
//            viewModel.onClick(userInput)
//        }) {
//            Text("Send")
//        }
//        if(viewModel.state.isLoading){
//            Text("Loading...")
//        } else {
//            Text("Response:")
//            Text(response)
//        }
//    }
}

@Composable
fun ChatBottomBar(onSendClicked: (String) -> Unit,
                  onInputFocused: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
        , horizontalArrangement = Arrangement.Start
    ) {
        ChatMessageInput(
            onSendClicked = onSendClicked,
            onInputFocused = onInputFocused
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMessageInput(onSendClicked: (String) -> Unit,
                     onInputFocused: (Boolean) -> Unit) {
    var messageText by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background)
                .onFocusChanged { onInputFocused(it.isFocused) },
            placeholder = { Text("Type a message...", modifier = Modifier.fillMaxSize(), style = MaterialTheme.typography.bodyLarge) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onSendClicked(messageText)
                    messageText = ""
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable { onSendClicked(messageText)
                    messageText = ""}
                .padding(4.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
