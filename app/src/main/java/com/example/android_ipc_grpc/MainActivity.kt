package com.example.android_ipc_grpc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.android_ipc_grpc.ui.theme.Android_ipc_grpcTheme
import kotlinx.coroutines.launch

class MainActivity : AbstractServiceActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onServiceBound() {
        viewModel.subscribe()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_ipc_grpcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        PackageWidget(
                            pkg = application.packageName
                        )
                        Row {
                            SendMessageWidget {
                                lifecycleScope.launch {
                                    viewModel.sendMessage()
                                }
                            }
                        }
                        MessagesWidget()
                    }
                }
            }
        }
    }

    @Composable
    private fun MessagesWidget() {
        val messages by viewModel.messageQueue.collectAsState(initial = listOf())

        Text(text = "messages - ${messages.size}")
        LazyColumn {
            items(messages) { message ->
                Text(message)
            }
        }
    }

    @Composable
    private fun SendMessageWidget(sendMessageRequested: () -> Unit) {
        Column {
            OutlinedButton(
                onClick = sendMessageRequested
            ) {
                Text(
                    text = "Send Message"
                )
            }
        }
    }

    @Composable
    private fun PackageWidget(pkg: String) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = pkg
        )
    }
}