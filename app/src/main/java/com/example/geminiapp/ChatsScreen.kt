package com.example.geminiapp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.geminiapp.presentation.GoogleAuthUIClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChatsScreen(navController: NavController, userViewModel: UserViewModel,
                onSignOut: ()->Unit){
    val userId = Firebase.auth.currentUser?.uid
    var chatsLoaded by remember {
        mutableStateOf(false)
    }
    Log.d("check", "chatsLoaded $chatsLoaded")
    LaunchedEffect(Unit){
        userViewModel.setChats(){
            chatsLoaded = it
        }
    }
    val chats by userViewModel.chats.collectAsState()
    if(chatsLoaded){
        Log.d("check", "user viewmodel ${userViewModel.chats.value.size}")
        Column {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(.8f)){
                    ChatEntry(navController = navController, chat = null, userId = userId)
                }

                Spacer(modifier = Modifier.size(4.dp))
                Button(onClick = {
                    onSignOut()
                }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                }
            }
            if(chats.isEmpty()){
                Text(text = "No chats yet\nStart chatting by pressing New Chat", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                items(chats) { chat ->
                    ChatEntry(navController = navController, chat = chat, userId = userId)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }
    } else {
        Text("Loading")
    }
}
@Composable
fun ChatEntry(navController: NavController, chat: Chat?, userId: String?){
    val context = LocalContext.current
    val firebaseManager = FirebaseManager()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
            .clickable {
                if (chat != null) {
                    Log.d("check", "chat is not null ${chat.chatId}")
                    navController.navigate(Screen.Chat.withArgs(chat.chatId!!))
                } else {
                    Log.d("check", "chat is null")
                    createChatAndNavigate(navController, userId)
                }
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = chat?.topic?:"Start a New Chat", modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
        if(chat != null){
            Button(onClick = { firebaseManager.deleteChat(chatId = chat.chatId!!, userId = userId!!) {
                if (it) {
                    Log.d("check", "Chat delete succesfull")
                } else {
                    Log.d("check", "Chat delete failed")
                }
            }
            }) {
                Icon(imageVector = Icons.Default.Delete,contentDescription = null)
            }
        }

    }
}
fun createChatAndNavigate(navController: NavController, userId: String?){
    val firebaseManager = FirebaseManager()
    if (userId != null) {
        firebaseManager.createChat(userId, {chatId->
            navController.navigate(Screen.Chat.withArgs(chatId))
        }, { e -> Log.d("check", "createChatAndNavigate: $e")
        })
    } else {
        Log.d("check", "current user is null")
    }
}