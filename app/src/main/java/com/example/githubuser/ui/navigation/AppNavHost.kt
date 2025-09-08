package com.example.githubuser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.githubuser.ui.screen.AllUsersScreen
import com.example.githubuser.ui.screen.UserDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = "all_users") {

        // All Users Screen
        composable("all_users") {
            AllUsersScreen(
                onUserClick = { username ->
                    navController.navigate("user_detail/$username")
                }
            )
        }

        // User Detail Screen
        composable(
            "user_detail/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserDetailScreen(username = username)
        }
    }
}
