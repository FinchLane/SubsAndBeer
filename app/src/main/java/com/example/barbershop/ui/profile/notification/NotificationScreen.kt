package com.example.barbershop.ui.profile.notification

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.viewmodel.profile.ProfileViewModel

@Composable
fun NotificationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val messages by profileViewModel.messages.collectAsState(initial = emptyList())
    val newMessage = messages.filter { !it.isChecked }
    val oldMessage = messages.filter { it.isChecked }

    val invitations = emptyList<String>()

    Scaffold(
        topBar = {
            AppBarBack(
                nav = {
                    navController.popBackStack()
                    profileViewModel.checkAllMessages()
                      },
                title = "Уведомления",
                content = {
                    if (messages.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                profileViewModel.deleteAllMessage()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Delete All"
                            )
                        }
                    }
                }
            )
                 },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (invitations.isNotEmpty()) {

            }
            if (messages.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "У вас нет уведомлений"
                    )
                }
            }
            else {
                if (newMessage.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Новые уведомления",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(newMessage) { index, item ->
                                val isFirst = index == 0
                                val isLast = index == messages.lastIndex
                                MessageItem(
                                    title = item.title,
                                    body = item.body,
                                    onClick = { },
                                    onDelete = { profileViewModel.deleteMessage(item.id) },
                                    isFirst = isFirst,
                                    isLast = isLast
                                )
                            }
                        }
                        if (oldMessage.isNotEmpty()) {
                            Text(
                                text = "Прочитанные уведомления",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = modifier.padding(start = 16.dp)
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(oldMessage) { index, item ->
                                    val isFirst = index == 0
                                    val isLast = index == messages.lastIndex
                                    MessageItem(
                                        title = item.title,
                                        body = item.body,
                                        onClick = { },
                                        onDelete = { profileViewModel.deleteMessage(item.id) },
                                        isFirst = isFirst,
                                        isLast = isLast
                                    )
                                }
                            }
                        }
                    }
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(messages) { index, item ->
                            val isFirst = index == 0
                            val isLast = index == messages.lastIndex
                            MessageItem(
                                title = item.title,
                                body = item.body,
                                onClick = { },
                                onDelete = { profileViewModel.deleteMessage(item.id) },
                                isFirst = isFirst,
                                isLast = isLast
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    title: String,
    body: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 12.dp
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(cornerRadius)
        isFirst -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        isLast -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        else -> RectangleShape
    }

    Surface(
        shape = shape,
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            IconButton(
                onClick = { onDelete() }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun InviteFamilyMessageItem(
    user: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 12.dp
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(cornerRadius)
        isFirst -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        isLast -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        else -> RectangleShape
    }

    Surface(
        shape = shape,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Пользователь $user приглашает вас в семейную группу",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                OutlinedButton (
                    onClick = { onCancel() },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Отклонить"
                    )
                }

                Button(
                    onClick = { onConfirm() },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Принять"
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MessageItemPreview() {
    BarbershopTheme {
        MessageItem(
            title = "Заголовок сообщения",
            body = "Тело сообщения",
            onClick = { },
            onDelete = { },
            isFirst = true,
            isLast = true
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InviteFamilyMessageItemPreview() {
    BarbershopTheme {
        InviteFamilyMessageItem(
            user = "+79519977990",
            onConfirm = { },
            onCancel = { },
            isFirst = true,
            isLast = true
        )
    }
}