package com.project.cancerdetect.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.project.cancerdetect.login.LoginActivity
import com.project.cancerdetect.ui.theme.CancerDetectTheme

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CancerDetectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignupScreen(
                        onSignupSuccess = {
                            startActivity(
                                Intent(
                                    this,
                                    LoginActivity::class.java
                                )
                            )
                        },
                        onSigninClicked = {
                            startActivity(
                                Intent(
                                    this, LoginActivity::class.java
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}