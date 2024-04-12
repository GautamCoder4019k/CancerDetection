package com.project.cancerdetect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.project.cancerdetect.ui.theme.CancerDetectTheme

class ResponseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = intent.getSerializableExtra("RESPONSE") as CancerResponse?
        setContent {
            CancerDetectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (result != null) {
                        ResponseScreen(result = result)
                    }
                }
            }
        }
    }
}