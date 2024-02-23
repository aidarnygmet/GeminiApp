package com.example.geminiapp.chatComposables

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.geminiapp.Message
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull

@Composable
fun ChatItem(message: Message) {
    val alignment = if (message.role == "user") {
        Arrangement.End
    } else {
        Arrangement.Start
    }
    val bubbleColor = if (message.role == "user") {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (message.role == "user") {
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
            if (message.role == "model"){
                var processedText by remember { mutableStateOf<AnnotatedString?>(null) }

                if (processedText == null) {
                    processedText = buildAnnotatedString {
                        val boldIndices = mutableListOf<Int>()
                        var previousCharacterIndex = -1
                        var nextCharacterIndex = 1
                        var indexCount = 0
                        val input = message.messageText
                        input.forEachIndexed { index, char ->
                            when (char) {
                                '*' -> {
                                    if(previousCharacterIndex>=0 && nextCharacterIndex<input.length){
                                        if(input[previousCharacterIndex] == '*' ||
                                            input[nextCharacterIndex] == '*'){
                                            if(boldIndices.isEmpty()){
                                                boldIndices.add(indexCount)
                                            }
                                            if(boldIndices.last() != indexCount){
                                                boldIndices.add(indexCount)
                                            }
                                            previousCharacterIndex++
                                            nextCharacterIndex++
                                        } else {

                                            append("\u2022")
                                            indexCount++
                                            previousCharacterIndex++
                                            nextCharacterIndex++
                                        }
                                    } else if(nextCharacterIndex<input.length){
                                        if(input[nextCharacterIndex] == '*'){
                                            if(boldIndices.isEmpty()){
                                                boldIndices.add(indexCount)
                                            }
                                            if(boldIndices.last() != indexCount){
                                                boldIndices.add(indexCount)
                                            }
                                            previousCharacterIndex++
                                            nextCharacterIndex++
                                        } else {
                                            append("\u2022")
                                            indexCount++
                                            previousCharacterIndex++
                                            nextCharacterIndex++
                                        }
                                    } else if (previousCharacterIndex>0){
                                        if(input[previousCharacterIndex] == '*'){
                                            if(boldIndices.isEmpty()){
                                                boldIndices.add(indexCount)
                                            }
                                            if(boldIndices.last() != indexCount){
                                                boldIndices.add(indexCount)
                                            }
                                            previousCharacterIndex++
                                            nextCharacterIndex++
                                        } else {
                                            append("\u2022")
                                            indexCount++
                                            previousCharacterIndex++
                                            nextCharacterIndex++
                                        }
                                    }
                                }
                                else -> {
                                    append(char.toString())
                                    indexCount++
                                    previousCharacterIndex++
                                    nextCharacterIndex++
                                }
                            }
                        }

                        for (i in boldIndices.indices step 2) {
                            addStyle(style = SpanStyle(fontWeight = FontWeight.Bold), start = boldIndices[i], end = boldIndices[i + 1])
                        }
                    }
                }
                Column(modifier = Modifier.background(bubbleColor)) {
                    Text(
                        text = processedText!!,
                        color = textColor
                    )
                }
            } else {
                Column(modifier = Modifier.background(bubbleColor)) {
                    Text(
                        text = message.messageText,
                        color = textColor
                    )
                }
            }

        }
    }

}