package com.example.githubuser.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.githubuser.ui.screen.AllUsersScreen
import com.example.githubuser.ui.screen.FavoriteUsersScreen
import com.example.githubuser.ui.screen.UserDetailScreen
import com.example.githubuser.util.networkStatusFlow

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem("all_users", Icons.Default.Home, "Home")
    data object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites,
    )

    val bottomBarRoutes = bottomNavItems.map { it.route }

    // Create one global SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    // Network status flow
    val isConnected by networkStatusFlow()

    // Show/dismiss offline snackbar
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            snackbarHostState.showSnackbar(
                message = "No Internet Connection",
                duration = SnackbarDuration.Indefinite
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    // Conditional bottom bar
    val bottomBar: @Composable () -> Unit = {
        if (currentRoute in bottomBarRoutes) {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Global snackbar
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                end = padding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            composable(BottomNavItem.Home.route) {
                AllUsersScreen(
                    onUserClick = { username ->
                        navController.navigate("user_detail/$username")
                    }
                )
            }

            // --- Favorites ---
            composable(BottomNavItem.Favorites.route) {
                FavoriteUsersScreen(
                    onBack = { navController.popBackStack() },
                    onUserClick = { username ->
                        navController.navigate("user_detail/$username")
                    }
                )
            }

            composable(
                "user_detail/{username}",
                arguments = listOf(navArgument("username") { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                UserDetailScreen(
                    username = username,
                    onBack = { navController.popBackStack() } // This is the implementation
                )
            }
        }
    }
}
