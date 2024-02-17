package com.example.geminiapp.chatComposables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull

@Composable
fun ChatItem(content: Content) {
    val alignment = if (content.role == "user") {
        Arrangement.End
    } else {
        Arrangement.Start
    }
    val bubbleColor = if (content.role == "user") {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (content.role == "user") {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = alignment
    ){
        Card(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(bubbleColor)
                .padding(8.dp)

        ){
            Column(modifier = Modifier.background(bubbleColor)) {
                Text(
                    text = content.parts[0].asTextOrNull() ?: "",
                    color = textColor
                )
            }
        }
    }

}