package com.example.geminiapp.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.geminiapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUIClient(private val context: Context,
    private val oneTapClient: SignInClient) {
    private val auth = Firebase.auth
    suspend fun signIn(): IntentSender?{
        val result = try {
            oneTapClient.beginSignIn(
                beginSignInRequest()
            ).await()
        } catch (e: Exception){
            e.printStackTrace()
            Log.d("check", "signIn")
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }
    suspend fun signInWithIntent(intent: Intent): SignInResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try{
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(data = user?.run {
                UserData(
                    userId = uid,
                    username = displayName,
                    profilePictureId = photoUrl?.toString()
                )
            }, errorMessage = null)
        }catch (e: Exception){
            e.printStackTrace()
            Log.d("check", "signInWithIntent")
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }
    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
    fun getSignedInUser(): UserData? = auth.currentUser?.run{
    UserData(
        userId = uid,
        username = displayName,
        profilePictureId = photoUrl?.toString())
    }
    private fun beginSignInRequest(): BeginSignInRequest{
        Log.d("check", "beginSignInRequest: ${context.getString(R.string.oauth)}")
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.oauth))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}