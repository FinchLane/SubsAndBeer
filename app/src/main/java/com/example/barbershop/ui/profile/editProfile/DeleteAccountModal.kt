package com.example.barbershop.ui.profile.editProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun DeleteAccountModal(onDeleteClick: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Column {
            Button(
                onClick = {
                    onDeleteClick()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF412a2b)),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete account",
                        tint = Color(0xFFff5c52)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Удалить аккаунт",
                        color = Color(0xFFff5c52)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DeleteAccountModalPreview() {
    BarbershopTheme {
        DeleteAccountModal({}) { }
    }
}