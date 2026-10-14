package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppMode
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco

@Composable
fun TopAppBarHeader(
    currentMode: AppMode,
    walletBalance: Double,
    onModeChange: (AppMode) -> Unit,
    onOpenWallet: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenRoleChooser: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Mode Title (clickable to choose role)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onOpenRoleChooser() }
                    .testTag("brand_header")
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberPrimary,
                    modifier = Modifier.size(42.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pollachi_auto_logo),
                        contentDescription = "Pollachi AutoGo Logo",
                        modifier = Modifier.size(42.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pollachi ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "AutoGo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = AmberPrimary
                        )
                    }
                    Text(
                        text = if (currentMode == AppMode.PASSENGER) "பொள்ளாச்சி பயணி (Rider)" else "பொள்ளாச்சி ஆட்டோ (Driver)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Controls on Right: Mode Switcher, Wallet Pill, History Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Wallet Pill
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .clickable(onClick = onOpenWallet)
                        .testTag("top_bar_wallet_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = EmeraldEco,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "₹${walletBalance.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Support Contact Button (JOHNS KHAN)
                var showContactDialog by remember { mutableStateOf(false) }
                val context = androidx.compose.ui.platform.LocalContext.current

                IconButton(
                    onClick = { showContactDialog = true },
                    modifier = Modifier.testTag("top_bar_contact_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Contact Support",
                        tint = AmberPrimary
                    )
                }

                // Ride History button
                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier.testTag("top_bar_history_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Ride History",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (showContactDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showContactDialog = false },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Contact",
                                tint = EmeraldEco
                            )
                        },
                        title = {
                            Text(
                                text = "Pollachi Auto Support & Dispatch",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = "Name: JOHNS KHAN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Phone: 90800 40503",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = EmeraldEco
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Dedicated 24/7 helpline for Pollachi people, auto bookings, bus stand/station pickups, driver coordination, and emergency support.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        confirmButton = {
                            androidx.compose.material3.Button(
                                onClick = {
                                    showContactDialog = false
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                            data = android.net.Uri.parse("tel:9080040503")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Dialing JOHNS KHAN: 90800 40503", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = EmeraldEco
                                )
                            ) {
                                Text("Call 90800 40503")
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(onClick = { showContactDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Shared ViewModel State View Switcher
                RiderDriverViewSwitcher(
                    currentMode = currentMode,
                    onModeChange = onModeChange
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Switch Account / Logout button to return to SplitAuthScreen
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.testTag("top_bar_logout_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Switch Account / Logout",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
