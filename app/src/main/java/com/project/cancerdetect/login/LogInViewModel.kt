package com.project.cancerdetect.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LogInViewModel : ViewModel() {

    var loginStatus = mutableStateOf<Boolean?>(null)
        private set

    fun loginWithEmailAndPassword(email: String = "", password: String = "") {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                loginStatus.value = true
            }
            .addOnFailureListener {
                loginStatus.value = false
            }
    }
}