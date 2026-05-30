package io.github.naupyon.arisanak.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import io.github.naupyon.arisanak.presentation.ui.screens.*
import io.github.naupyon.arisanak.presentation.ui.theme.*
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel

@Composable
fun ArisanApp() {
    val viewModel: ArisanViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsState()

    MyApplicationTheme(
        darkTheme = settings?.isDarkMode ?: false,
        dynamicColor = settings?.colorMode == "Material You"
    ) {
        SecurityLockScreen(viewModel = viewModel) {
            MainNavigationShell(viewModel = viewModel)
        }
    }
}

@Composable
fun MainNavigationShell(viewModel: ArisanViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf("home", "groups", "profil")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = WarmSurface,
                    tonalElevation = 0.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = RoseRed,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier.height(100.dp)
                    ) {
                        NavigationBarItem(
                            selected = currentRoute == "home",
                            onClick = {
                                navController.navigate("home") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home,
                                    contentDescription = "Home",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoseRed,
                                unselectedIconColor = BalanceSec,
                                selectedTextColor = RoseRed,
                                unselectedTextColor = BalanceSec,
                                indicatorColor = RoseContainer
                            )
                        )

                        NavigationBarItem(
                            selected = currentRoute == "groups",
                            onClick = {
                                navController.navigate("groups") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == "groups") Icons.Default.Groups else Icons.Outlined.Groups,
                                    contentDescription = "Groups",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text("Groups", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoseRed,
                                unselectedIconColor = BalanceSec,
                                selectedTextColor = RoseRed,
                                unselectedTextColor = BalanceSec,
                                indicatorColor = RoseContainer
                            )
                        )

                        NavigationBarItem(
                            selected = currentRoute == "profil",
                            onClick = {
                                navController.navigate("profil") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == "profil") Icons.Default.Person else Icons.Outlined.Person,
                                    contentDescription = "Profil",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text("Profil", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoseRed,
                                unselectedIconColor = BalanceSec,
                                selectedTextColor = RoseRed,
                                unselectedTextColor = BalanceSec,
                                indicatorColor = RoseContainer
                            )
                        )
                    }
                }
            }
        },
        containerColor = WarmBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToGroup = { id -> navController.navigate("group_detail/$id") },
                    onNavigateToPiutang = { navController.navigate("piutang") }
                )
            }

            composable("groups") {
                GroupsListScreen(
                    viewModel = viewModel,
                    onNavigateToGroup = { id -> navController.navigate("group_detail/$id") }
                )
            }

            composable("piutang") {
                PiutangScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("profil") {
                ProfilSettingsScreen(viewModel = viewModel)
            }

            composable(
                route = "group_detail/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.LongType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                GroupDetailScreen(
                    groupId = groupId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
