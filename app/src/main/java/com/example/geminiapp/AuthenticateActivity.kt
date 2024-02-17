package com.example.geminiapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class AuthenticateActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { MakeItSoApp() }
    }

    private fun MakeItSoApp() {
        TODO("Not yet implemented")
    }
}