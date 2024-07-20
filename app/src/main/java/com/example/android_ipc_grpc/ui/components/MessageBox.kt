package com.example.android_ipc_grpc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android_ipc_grpc.utils.toStringFormat
import java.time.LocalDateTime

@Composable
fun MessageBox(
    message: String,
    date: LocalDateTime,
    isMessageGroup: Boolean,
    isLeft: Boolean
) {
    val gravity = if (isLeft) {
        Alignment.Start
    } else {
        Alignment.End
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = gravity
    ) {
        AnimatedVisibility(
            visible = !isMessageGroup
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = date.toStringFormat(),
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            horizontalAlignment = gravity
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isLeft) {
                            Color.Gray
                        } else {
                            Color.Blue
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(text = message)
            }
        }
    }
}

@Preview
@Composable
fun MessageBoxPreview() {
    Column {
        MessageBox(
            message = "hello mr test, for long message test test test test test test",
            date = LocalDateTime.now(),
            isLeft = true,
            isMessageGroup = false
        )
        MessageBox(
            message = "hi!!",
            date = LocalDateTime.now(),
            isLeft = false,
            isMessageGroup = true
        )
    }
}