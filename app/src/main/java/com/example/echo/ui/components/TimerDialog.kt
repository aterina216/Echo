package com.example.echo.ui.components

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimerDialog(
    onDismiss: () -> Unit,
    onStart: (Int) -> Unit
) {
    var timerInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {Text("Таймер сна", color = MaterialTheme.colorScheme.onBackground)},
        text = {
            Column {
                Text("Введите количество минут", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = timerInput,
                    onValueChange = {newValue ->
                        if (newValue.all { it.isDigit() }) timerInput = newValue
                    },
                    placeholder = {Text("0", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))},
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        cursorColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = timerInput.toIntOrNull()
                    if (minutes != null && minutes > 0){
                        onStart(minutes)
                        timerInput = ""
                        onDismiss()
                    }
                },
                enabled = timerInput.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Установить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    )
}