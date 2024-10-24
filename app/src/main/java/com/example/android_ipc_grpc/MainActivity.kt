package com.example.android_ipc_grpc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.android_ipc_grpc.ipc.AbstractServiceActivity
import com.example.android_ipc_grpc.ui.components.IconBlinker
import com.example.android_ipc_grpc.ui.components.MessageBox
import com.example.android_ipc_grpc.ui.components.MessageTextField
import com.example.android_ipc_grpc.ui.models.UiMessage
import com.example.android_ipc_grpc.ui.theme.Android_ipc_grpcTheme
import com.example.android_ipc_grpc.ui.theme.systemGrey
import kotlinx.coroutines.launch

class MainActivity : AbstractServiceActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onServiceBound() {
        lifecycleScope.launch {
            viewModel.authenticate()
            viewModel.subscribe()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_ipc_grpcTheme(
                dynamicColor = false,
                darkTheme = true
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBarWidget() },
                    bottomBar = { BottomBarWidget() }
                ) {
                    Column(
                        modifier = Modifier.padding(it)
                    ) {
                        MessagesWidget()
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomBarWidget() {
        val currentInputMessage by viewModel.message.collectAsState(initial = "")
        MessageTextField(
            currentInputMessage = currentInputMessage,
            onValueChangeListener = { viewModel.message.value = it },
            onSendMessageListener = {
                if (serviceBound().value) {
                    lifecycleScope.launch {
                        viewModel.sendMessage()
                    }
                } else {
                    Toast.makeText(applicationContext, "Service not bound", Toast.LENGTH_LONG)
                        .show()
                }
            }
        )
    }

    @Composable
    private fun TopBarWidget() {
        val serviceStatus by serviceBound().collectAsState(initial = false)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = { boundService() }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Rounded.KeyboardArrowLeft,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Button back"
                )
            }
            IdentityWidget(
                identity = application.packageName.substringAfterLast(".")
            )
            IconBlinker(
                label = "ServiceStatus",
                iconVector = Icons.Outlined.Email,
                iconTint = MaterialTheme.colorScheme.primary,
                blink = !serviceStatus
            )
        }
    }

    @Composable
    private fun IdentityWidget(identity: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape)
                    .background(systemGrey)
                    .padding(4.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Person,
                    tint = Color.White,
                    contentDescription = "Icon eMail"
                )
            }
            Text(
                text = identity,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    @Composable
    private fun MessagesWidget() {
        val scrollState = rememberLazyListState()
        val messages by viewModel.messageQueue.collectAsState(initial = listOf())

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                scrollState.animateScrollToItem(messages.lastIndex)
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(messages) { uiMessage ->
                MessageBox(
                    message = uiMessage.message,
                    date = uiMessage.sendAt,
                    isLeft = uiMessage.isOwner == UiMessage.OwnerType.ME,
                    isMessageGroup = uiMessage.isMessageGroup
                )
            }
        }
    }
}