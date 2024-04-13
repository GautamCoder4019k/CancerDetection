package com.project.cancerdetect

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.project.cancerdetect.api.RetrofitClient
import com.project.cancerdetect.ui.theme.CancerDetectTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects


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
    var result by rememberSaveable { mutableStateOf<CancerResponse?>(null) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = uri
            uploadImage(context, it, snackbarHostState, scope) { response ->
                result = response
                showDialog = true
            }
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

    val file = createImageFile(context)
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )


    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        imageUri = uri
        uploadImage(context, uri, snackbarHostState, scope) { response ->
            result = response
            showDialog = true
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Permission Granted")
            }
            cameraLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permission Denied")
            }
        }
    }

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

        Button(
            onClick = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            colors = ButtonDefaults.buttonColors(Color.White),
            shape = RoundedCornerShape(0)
        ) {
            Icon(
                modifier = Modifier.size(120.dp),
                tint = Color.Black,
                painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                contentDescription = null
            )
            Text(
                text = "Click here to Capture Image",
                style = TextStyle(fontSize = 20.sp, color = Color.Black)
            )
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = !showDialog }) {
                Card(
                    modifier = Modifier.padding(20.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    result?.let {
                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = it.res,
                            style = TextStyle(fontSize = 20.sp, color = Color.Black)
                        )
                    }
                }
            }
        }
    }
}

fun uploadImage(
    context: Context,
    imageUri: Uri,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onResponse: (CancerResponse) -> Unit
) {
    val file = File(context.cacheDir, "upload.jpg").apply {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            outputStream().use { inputStream.copyTo(it) }
        }
    }

    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("pic", file.name, requestFile)

    RetrofitClient.apiService.uploadImage(body)
        .enqueue(object : retrofit2.Callback<CancerResponse> {
            override fun onResponse(
                call: Call<CancerResponse>,
                response: Response<CancerResponse>
            ) {
                scope.launch {
                    val responseBody = response.body()
                    Log.d("@@@@@@@", response.message().toString())
                    if (responseBody != null) {
                        onResponse(responseBody)
                    }
                    snackbarHostState.showSnackbar("Upload successful")
                }
            }

            override fun onFailure(call: Call<CancerResponse>, t: Throwable) {
                Log.d("@@@@@@@", t.message.toString())
                scope.launch {
                    snackbarHostState.showSnackbar("Upload failed: ${t.message}")
                    onResponse(CancerResponse("ERROR"))
                }
            }
        })
}

fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

@Preview(showBackground = true)
@Composable
private fun MainActivityScreenPreview() {
    MainActivityScreen()
}