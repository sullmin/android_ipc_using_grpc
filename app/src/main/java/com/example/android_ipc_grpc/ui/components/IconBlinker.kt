package com.example.android_ipc_grpc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android_ipc_grpc.ui.theme.systemSuccess

@Composable
fun IconBlinker(
    label: String,
    iconVector: ImageVector,
    iconTint: Color,
    blink: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(30.dp)
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = iconVector,
            tint = iconTint,
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
                color = systemSuccess
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
        color = MaterialTheme.colorScheme.error.copy(alpha = scale)
    )
}

@Composable
private fun BoxCircle(
    color: Color
) {
    Canvas(
        modifier = Modifier.size(10.dp),
        onDraw = {
            drawCircle(
                color = color
            )
        }
    )
}

@Preview
@Composable
fun IconBlinkerPreview() {
    IconBlinker(
        label = "",
        iconTint = Color.Black,
        iconVector = Icons.Outlined.Email,
        blink = true
    )
}