package com.sih.drugtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih.drugtest.ui.components.AppBottomNavigation
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.DarkNavy
import com.sih.drugtest.ui.theme.Ivory
import com.sih.drugtest.ui.theme.NavyBackground
import com.sih.drugtest.ui.theme.SecondaryText


@Composable
fun ProfileScreen(
    fullName: String,
    officerId: String,
    email: String,
    onLogoutClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {

    Scaffold(
        containerColor = NavyBackground,

        bottomBar = {

            AppBottomNavigation(
                selectedTab = "profile",
                onHomeClick = onHomeClick,
                onTestsClick = onTestsClick,
                onHistoryClick = onHistoryClick,
                onProfileClick = {}
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 20.dp)
        ) {

            Text(
                text = "Officer Profile",
                color = Ivory,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Authenticated officer information",
                color = SecondaryText,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =====================================================
            // PROFILE AVATAR
            // =====================================================

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    color = Color(0xFF0B3455)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Officer Profile",
                            tint = CyanAccent,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =====================================================
            // OFFICER DETAILS CARD
            // =====================================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Ivory
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    ProfileDetailRow(
                        icon = Icons.Outlined.Person,
                        label = "Full Name",
                        value = fullName
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    ProfileDetailRow(
                        icon = Icons.Outlined.Badge,
                        label = "Officer ID",
                        value = officerId
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    ProfileDetailRow(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = email
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =====================================================
            // LOGOUT BUTTON
            // =====================================================

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB3261E)
                )
            ) {

                Text(
                    text = "Logout",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = CyanAccent.copy(
                alpha = 0.14f
            )
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = DarkNavy,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = label,
                color = Color(0xFF607080),
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                color = NavyBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}