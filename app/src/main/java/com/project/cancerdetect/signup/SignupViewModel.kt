package com.project.cancerdetect.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignupViewModel : ViewModel() {

    var signupStatus = mutableStateOf<Boolean?>(null)
        private set

    fun signupWithEmailAndPassword(email: String = "", password: String = "") {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                signupStatus.value = true
            }
            .addOnFailureListener {
                signupStatus.value = false
                Log.d("TAG", "signupWithEmailAndPassword failure:${it.localizedMessage} ")
            }
    }
}