package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.HelloTalkViewModel
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.moments.MomentsFeedScreen
import com.example.ui.screens.partners.PartnerListScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.voicerooms.VoiceRoomsScreen

enum class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Connect("connect", "Connect", Icons.Default.People, Icons.Outlined.PeopleOutline),
    VoiceRooms("voicerooms", "Voice Rooms", Icons.Default.GraphicEq, Icons.Outlined.GraphicEq),
    Moments("moments", "Moments", Icons.Default.Public, Icons.Outlined.Public),
    Chats("chats", "Chats", Icons.Default.ChatBubble, Icons.Outlined.ChatBubbleOutline),
    Me("me", "Me", Icons.Default.Person, Icons.Outlined.PersonOutline)
}

@Composable
fun HelloTalkMainContainer(
    viewModel: HelloTalkViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                BottomNavItem.values().forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Connect.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Connect.route) {
                PartnerListScreen(
                    viewModel = viewModel,
                    onSelectPartnerChat = {
                        navController.navigate(BottomNavItem.Chats.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(BottomNavItem.VoiceRooms.route) {
                VoiceRoomsScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.Moments.route) {
                MomentsFeedScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.Chats.route) {
                ChatScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.Me.route) {
                ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
