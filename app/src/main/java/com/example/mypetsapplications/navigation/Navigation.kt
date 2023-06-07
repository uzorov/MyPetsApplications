package com.example.mypetsapplications.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mypetsapplications.database.CredentialsDataViewModel
import com.example.mypetsapplications.navigation.screens.*


object NavigationPaths {
    const val SPLASH_SCREEN = "splash_screen"
    const val AUTH_SCREEN = "auth_screen"
    const val PETS_SCREEN = "pets_screen"
    const val FILL_THE_APPLICATION_SCREEN = "fill_the_application_screen"
    const val ADMIN_SCREEN = "admin_screen"
}

@Composable
fun Navigation(navController: NavHostController) {
    val sharedViewModel = viewModel<SharedViewModel>(viewModelStoreOwner = LocalViewModelStoreOwner.current!!)
    val credentialsDataViewModel = viewModel<CredentialsDataViewModel>()

    NavHost(navController = navController, startDestination = NavigationPaths.SPLASH_SCREEN) {


        composable(NavigationPaths.SPLASH_SCREEN) {
            SplashScreen(navController)
        }

        composable(NavigationPaths.AUTH_SCREEN) {
            AuthorizationScreen(navController, sharedViewModel, credentialsDataViewModel)
        }

        composable(NavigationPaths.PETS_SCREEN) {
            PetsScreen(navController, sharedViewModel, credentialsDataViewModel)
        }

        composable(NavigationPaths.FILL_THE_APPLICATION_SCREEN) {
            FillTheApplicationScreen(navController, sharedViewModel, credentialsDataViewModel)
        }
        composable(NavigationPaths.ADMIN_SCREEN) {
            AdminScreen(navController,sharedViewModel, credentialsDataViewModel)
        }

    }

}