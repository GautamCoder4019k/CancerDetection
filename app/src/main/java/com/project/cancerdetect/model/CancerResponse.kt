package com.project.cancerdetect.model

import android.graphics.Color
import android.net.Uri

data class CancerResponse(
    val res: String,
    val uri: Uri? = null,
    val info: String,
    val cancerType: String
) {
    val color: Int
        get() = if (cancerType.equals("Cancerous")) Color.RED else Color.GREEN
}