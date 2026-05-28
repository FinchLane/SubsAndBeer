package com.example.barbershop.ui.subscription.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.barbershop.data.model.subscription.NotificationCheckbox

private val ELEMENT_HEIGHT = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    checkboxes: List<NotificationCheckbox>,
    onStateChange: (Int, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Уведомления",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            checkboxes.forEach { checkbox ->
                GeneralCheckBox(
                    text = checkbox.text,
                    state = checkbox.isChecked,
                    onStateChange = { value ->
                        onStateChange(checkbox.id, value)
                    }
                )
            }
        }
    }
}

@Composable
fun GeneralCheckBox(
    text: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(ELEMENT_HEIGHT)
            .selectable(
                selected = state,
                onClick = { onStateChange(!state) },
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(state, null)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}