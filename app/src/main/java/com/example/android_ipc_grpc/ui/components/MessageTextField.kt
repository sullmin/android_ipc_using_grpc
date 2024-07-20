package com.example.android_ipc_grpc.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageTextField(
    currentInputMessage: String,
    onValueChangeListener: (String) -> Unit,
    onSendMessageListener: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(text = "Message")
            },
            shape = RoundedCornerShape(45),
            value = currentInputMessage,
            onValueChange = onValueChangeListener,
            trailingIcon = {
                Button(
                    onClick = onSendMessageListener,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(10.dp),
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Rounded.Send,
                        contentDescription = "Button send"
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun MessageTextFieldPreview() {
    MessageTextField(
        currentInputMessage = "current msg",
        onValueChangeListener = {},
        onSendMessageListener = {}
    )
}