package com.project.cancerdetect.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun DashboardListElement(text: String, uri: Uri?, infoText: String, color: Color) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val  url = "https://www.google.com/maps/search/oncologist+near+me"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        colors = CardDefaults.cardColors(color),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(fontSize = 20.sp, color = Color.Black)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = if (expanded) "Hide Details" else "Show Details",
                    style = TextStyle(fontSize = 16.sp, color = Color.Blue),
                    modifier = Modifier.padding(end = 5.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    tint = Color.Black,
                    contentDescription = if (expanded) "Hide details" else "Show details"
                )
            }
            if (expanded) {
                Text(
                    text = infoText,
                    style = TextStyle(fontSize = 20.sp, color = Color.Black)
                )
                TextButton(onClick = { openUrlInBrowser(context, url) }) {
                    Text(text = "Click to know more", color = Color.White)
                }
            }
            uri?.let {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Uploaded Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(200.dp)
                )
            }
        }
    }
}

fun openUrlInBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Preview
@Composable
private fun DashboardListElementPreview() {
    DashboardListElement(
        "Example Title",
        null,
        "Detailed information that can be expanded or collapsed.",
        Color.White
    )
}
