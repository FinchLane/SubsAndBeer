package com.example.barbershop.ui.subscription.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.ui.components.customComponent.GeneralRadioButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultCategory = Category(id = "-1", name = "Не выбрана")
    val categoriesWithDefault = listOf(defaultCategory) + categories
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Категория",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {onClick()},
                    colors = ButtonDefaults.buttonColors(Color.DarkGray),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Управл.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            categoriesWithDefault.forEach { category ->
                GeneralRadioButton(
                    text = category.name,
                    value = category.id,
                    selectedOption = selectedCategoryId,
                    onOptionSelect = onCategorySelected
                )
            }
        }
    }
}
