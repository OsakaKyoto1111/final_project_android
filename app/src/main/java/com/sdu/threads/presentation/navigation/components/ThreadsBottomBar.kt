package com.sdu.threads.presentation.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sdu.threads.presentation.navigation.Screen

private data class BottomBarItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun ThreadsBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        BottomBarItem(Screen.Home, Icons.Rounded.Home, "Feed"),
        BottomBarItem(Screen.Search, Icons.Rounded.Search, "Search"),
        BottomBarItem(Screen.CreatePost, Icons.Rounded.Add, "Create"),
        BottomBarItem(Screen.Profile, Icons.Rounded.Person, "Profile")
    )
    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination.isRouteInHierarchy(item.screen.route)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { destination -> destination.route == route } == true
}
