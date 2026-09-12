package com.sih.drugtest.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.DarkNavy
import com.sih.drugtest.ui.theme.Ivory

@Composable
fun AppBottomNavigation(
    selectedTab: String,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = DarkNavy,
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            selected = selectedTab == "home",
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 12.sp
                )
            },
            colors = appNavigationColours()
        )

        NavigationBarItem(
            selected = selectedTab == "tests",
            onClick = onTestsClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Science,
                    contentDescription = "Tests"
                )
            },
            label = {
                Text(
                    text = "Tests",
                    fontSize = 12.sp
                )
            },
            colors = appNavigationColours()
        )

        NavigationBarItem(
            selected = selectedTab == "history",
            onClick = onHistoryClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = "History"
                )
            },
            label = {
                Text(
                    text = "History",
                    fontSize = 12.sp
                )
            },
            colors = appNavigationColours()
        )

        NavigationBarItem(
            selected = selectedTab == "settings",
            onClick = onSettingsClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text(
                    text = "Settings",
                    fontSize = 12.sp
                )
            },
            colors = appNavigationColours()
        )
    }
}

@Composable
private fun appNavigationColours() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = CyanAccent,
        selectedTextColor = CyanAccent,
        unselectedIconColor = Ivory,
        unselectedTextColor = Ivory,
        indicatorColor = Color(0xFF0B3455)
    )