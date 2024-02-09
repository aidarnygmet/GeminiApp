package com.example.geminiapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiScreen() {
    val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.API_KEY
    )
    val GeminiViewModel : GeminiViewModel= viewModel()
    var userInput by remember { mutableStateOf("") }
    var response  = GeminiViewModel.state.response

    Column {
        Text("Enter your request:")
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Request") }
        )
        Button(onClick = {
            // Make your API call here using userInput
            GeminiViewModel.onClick(generativeModel, userInput)
        }) {
            Text("Send")
        }
        if(GeminiViewModel.state.isLoading){
            Text("Loading...")
        } else {
            Text("Response:")
            Text(response)
        }
    }
}