package com.project.cancerdetect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancerResponse(
    val res: String
): Parcelable