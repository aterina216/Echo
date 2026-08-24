package com.example.echo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun menu(
    modifier: Modifier,
    onNavigationClick: () -> Unit = {},
    onMenuItemclick: (String) -> Unit = {},
    onExpandedChange: (Boolean) -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = @androidx.compose.runtime.Composable {
            Text("Echo", fontWeight = FontWeight.Light)
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Меню"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                expanded = true
                onExpandedChange(true)
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Еще"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    onExpandedChange(false)
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
                    )

            ) {
                DropdownMenuItem(
                    text = { Text("О приложении") },
                    onClick = {
                        expanded = false
                        onExpandedChange(false)
                        onMenuItemclick("about")
                    }
                )

                DropdownMenuItem(
                    text = { Text("Настройки") },
                    onClick = {
                        expanded = false
                        onExpandedChange(false)
                        onMenuItemclick("settings")
                    }
                )
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
    )
}