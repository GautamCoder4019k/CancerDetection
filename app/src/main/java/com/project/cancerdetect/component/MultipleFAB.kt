package com.project.cancerdetect.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

enum class FabType {
    CAMERA, GALLERY
}

sealed class FabButtonState {
    data object Collapsed : FabButtonState()
    data object Expand : FabButtonState()

    fun isExpanded() = this == Expand

    fun toggleValue() = if (isExpanded()) {
        Collapsed
    } else {
        Expand
    }
}

data class FabButton(
    val fabType: FabType,
    val iconRes: Int,
)

@Composable
fun FabItem(
    fabButton: FabButton,
    onFabClick: (FabType) -> Unit
) {
    FloatingActionButton(
        onClick = {
            onFabClick(fabButton.fabType)
        },
        modifier = Modifier
            .size(48.dp),
        containerColor = Color.Black
    ) {
        Icon(
            painter = painterResource(id = fabButton.iconRes),
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
fun MultipleFAB(
    fabButtons: List<FabButton>,
    fabButtonState: FabButtonState,
    onFabButtonStateChange: (FabButtonState) -> Unit,
    onFabClick: (FabType) -> Unit
) {
    val rotation by animateFloatAsState(
        if (fabButtonState.isExpanded())
            135f
        else
            0f, label = "mainFabRotation"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = fabButtonState.isExpanded(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                fabButtons.forEach {
                    FabItem(
                        fabButton = it,
                        onFabClick = onFabClick
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                onFabButtonStateChange(fabButtonState.toggleValue())
            },
            modifier = Modifier
                .rotate(rotation),
            containerColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                tint = Color.White,
                contentDescription = null
            )
        }
    }
}