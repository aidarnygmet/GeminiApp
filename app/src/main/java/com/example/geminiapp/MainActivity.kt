package com.example.geminiapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getString
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.geminiapp.chatComposables.ChatScreen
import com.example.geminiapp.presentation.EmailSignInClient
import com.example.geminiapp.presentation.GoogleAuthUIClient
import com.example.geminiapp.presentation.SignInScreen
import com.example.geminiapp.presentation.SignInViewModel
import com.example.geminiapp.presentation.SignUpScreen
import com.example.geminiapp.ui.theme.GeminiAppTheme
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class MainActivity : ComponentActivity() {
    private val googleAuthClient by lazy{
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val emailSignInClient = EmailSignInClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val generativeModel = GenerativeModel(
            apiKey = BuildConfig.API_KEY,
            modelName = "gemini-pro"
        )
        val auth = Firebase.auth
        var startDestination = "sign_in"
        if(auth.currentUser != null){
            startDestination = Screen.Chats.route
        }

        setContent {
            GeminiAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //GeminiScreen(viewModel = viewModel)
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = startDestination){
                        composable("sign_in"){
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = {result->
                                    if(result.resultCode == RESULT_OK){
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                            if(viewModel.state.value.isSignInSuccessful){
                                                navController.navigate(Screen.Chats.route)
                                            }
                                        }
                                    }
                                }
                            )
                            LaunchedEffect(key1 = state.isSignInSuccessful){
                                if(state.isSignInSuccessful){
                                    Toast.makeText(applicationContext, "SignIn Success", Toast.LENGTH_LONG).show()
                                }
                            }
                            SignInScreen(
                                state = state,
                                onGoogleSignInClicked = {
                                    lifecycleScope.launch {
                                        val signInIntent = googleAuthClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntent ?: return@launch
                                            ).build()
                                        )
                                    }
                                },
                                onSignInClicked = {email, password ->
                                    emailSignInClient.signIn(email, password, onSuccess = {result->
                                        viewModel.onSignInResult(result)
                                        if(viewModel.state.value.isSignInSuccessful){
                                            if(auth.currentUser != null){
                                                Log.d("check", "signInResult ${auth.currentUser?.uid}")
                                                navController.navigate(Screen.Chats.route)
                                            }
                                        }
                                    },
                                        onFailure = {
                                            val e = it?.message
                                            Toast.makeText(applicationContext, "SignIn Fail: $e", Toast.LENGTH_LONG).show()
                                        })
                                },
                                onSignUpClicked = {
                                    Log.d("check", "wtf")
                                    navController.navigate(Screen.SignUp.route)
                                }
                                )
                        }
                        composable("sign_up"){
                            SignUpScreen(navController = navController, onButtonClicked = { email, username, password->
                                emailSignInClient.signUp(email, username, password, onSuccess = {result->
                                    navController.navigate(Screen.Chats.route)
                                }, onFailure = {
                                    val e = it?.message
                                    Toast.makeText(applicationContext, "SignIn Fail: $e", Toast.LENGTH_LONG).show()
                                })
                            })
                        }
                        composable(
                            Screen.Chats.route
                        ){
                            var userViewModel = UserViewModel()
                            if(auth.currentUser != null){
                                Log.d("check", "launching chats screen ${auth.currentUser!!.uid}")
                                userViewModel.setCurrentUser(auth.currentUser!!.uid)
                            }
                            ChatsScreen(navController = navController, userViewModel = userViewModel, onSignOut = {
                                Log.d("check", "Sign Out")
                                navController.navigate("sign_in")
                                try {
                                    auth.signOut()
                                }catch (e: Exception){
                                    e.printStackTrace()
                                    if (e is CancellationException) throw e
                                }
                            })
                        }
                        composable(
                            Screen.Chat.route+"/{chatId}",
                            arguments = listOf(
                                navArgument("chatId") {
                                    type = NavType.StringType
                                }
                            )
                        ){
                            val chatId = it.arguments?.getString("chatId")
                            val userId = auth.currentUser?.uid
                            ChatScreen(navController = navController, model = generativeModel, chatId = chatId!!, userId = userId!!)
                        }
                    }
                }
            }
        }
    }
}


