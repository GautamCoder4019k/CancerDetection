package com.project.cancerdetect.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.project.cancerdetect.ui.theme.styleTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    topBarTitle: Int,
    backPress: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = topBarTitle),
                style = styleTopBar
            )
        },
        navigationIcon = {
            if (backPress != null) {
                IconButton(onClick = { backPress.invoke() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White)
    )
}