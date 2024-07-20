package com.example.android_ipc_grpc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IconBlinker(
    label: String,
    iconVector: ImageVector,
    blink: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(30.dp)
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = iconVector,
            contentDescription = "Icon eMail"
        )
        AnimatedVisibility(
            visible = blink
        ) {
            CircleBlinker(label = label)
        }
        AnimatedVisibility(
            visible = !blink
        ) {
            BoxCircle(
                modifier = Modifier.background(Color.Green)
            )
        }
    }
}

@Composable
private fun CircleBlinker(label: String) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "${label}Transition"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "${label}Animate"
    )

    BoxCircle(
        modifier = Modifier.background(Color.Red.copy(alpha = scale))
    )
}

@Composable
private fun BoxCircle(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(AlertDialogDefaults.shape)
    )
}

@Preview
@Composable
fun IconBlinkerPreview() {
    IconBlinker(
        label = "",
        iconVector = Icons.Outlined.Email,
        blink = true
    )
}