package com.example.echo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.echo.ui.screens.EchoScreen
import com.example.echo.ui.screens.SettingsScreen
import com.example.echo.ui.viewmodel.EchoViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun InitNavigation(viewModel: EchoViewModel) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") {
            EchoScreen(viewModel, {navController.navigate("settings")})
        }
        composable("settings"){
            SettingsScreen(
                currentTheme = viewModel.themeOption.collectAsState().value,
                onThemeSelected = viewModel::setTheme,
                onBack = {navController.popBackStack()}
            )
        }
    }
}