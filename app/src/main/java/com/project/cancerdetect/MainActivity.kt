package com.project.cancerdetect

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.project.cancerdetect.api.RetrofitClient
import com.project.cancerdetect.ui.theme.CancerDetectTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CancerDetectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityScreen()
                }
            }
        }
    }

}


@Composable
fun MainActivityScreen() {
    val context = LocalContext.current
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var serverResponse by rememberSaveable { mutableStateOf<String?>(null) }  // State to hold the server response

    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = uri
            uploadImage(context, it, snackbarHostState, scope) { response ->
                serverResponse = response // Update the state with the response
            }
            Log.d("RESPONSE",serverResponse.toString())
        }
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                galleryLauncher.launch("image/*")
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Storage Permission Not Given")
                }
            }
        }
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ) {
                    galleryLauncher.launch("image/*")
                } else {
                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            },
            colors = ButtonDefaults.buttonColors(Color.White),
            shape = RoundedCornerShape(0)
        ) {
            Icon(
                modifier = Modifier.size(120.dp),
                tint = Color.Black,
                painter = painterResource(id = R.drawable.baseline_image_24),
                contentDescription = null
            )
            Text(
                text = "Click here to Upload Image",
                style = TextStyle(fontSize = 20.sp, color = Color.Black)
            )
        }
    }
}

fun uploadImage(
    context: Context,
    imageUri: Uri,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onResponse: (String) -> Unit
) {
    val file = File(context.cacheDir, "upload.jpg").apply {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            outputStream().use { inputStream.copyTo(it) }
        }
    }

    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("pic", file.name, requestFile)

    RetrofitClient.apiService.uploadImage(body).enqueue(object : retrofit2.Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            scope.launch {
                val responseBody = response.body()?.string() ?: "No response from server"
                onResponse(responseBody)  // Pass the response to the composable
                snackbarHostState.showSnackbar("Upload successful")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            scope.launch {
                snackbarHostState.showSnackbar("Upload failed: ${t.message}")
                onResponse("Failed to upload image")  // Notify the composable of failure
            }
        }
    })
}


@Preview(showBackground = true)
@Composable
private fun MainActivityScreenPreview() {
    MainActivityScreen()
}