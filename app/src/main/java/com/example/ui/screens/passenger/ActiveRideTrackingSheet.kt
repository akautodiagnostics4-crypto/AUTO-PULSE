package com.example.ui.screens.passenger

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RideBooking
import com.example.model.RideStatus
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco
import com.example.ui.theme.StarGold

@Composable
fun ActiveRideTrackingSheet(
    booking: RideBooking,
    onCancelRide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSosDialog by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }

    val isArriving = booking.status == RideStatus.DRIVER_ARRIVING
    val isInTransit = booking.status == RideStatus.IN_TRANSIT

    val remainingDist = if (isArriving) {
        (0.8f * (1f - booking.progress)).coerceAtLeast(0.1f)
    } else {
        (booking.distanceKm * (1f - booking.progress)).coerceAtLeast(0.1f)
    }

    val remainingMin = if (isArriving) {
        ((1f - booking.progress) * 3).toInt().coerceAtLeast(1)
    } else {
        ((1f - booking.progress) * booking.estimatedMinutes).toInt().coerceAtLeast(1)
    }

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_ride_tracking_sheet")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF64748B))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Live Status Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isArriving) AmberPrimary else EmeraldEco,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArriving) "Driver is Arriving..." else "Ride in Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isArriving) "$remainingMin min away (${String.format("%.1f", remainingDist)} km)" else "Heading to ${booking.dropoff.name}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // SOS Safety Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AlertRed.copy(alpha = 0.15f),
                    modifier = Modifier
                        .clickable { showSosDialog = true }
                        .testTag("sos_emergency_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "SOS",
                            tint = AlertRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SOS",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = AlertRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-time Progress Bar
            LinearProgressIndicator(
                progress = { booking.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isArriving) AmberPrimary else EmeraldEco,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Safety OTP Card (Crucial for passenger)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AmberPrimary.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SAFETY START PIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Share with driver upon boarding",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberPrimary,
                        modifier = Modifier.testTag("safety_otp_badge")
                    ) {
                        Text(
                            text = booking.safetyOtp,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Driver Photo & Vehicle Plate Card (Customer View)
            val driver = booking.driver
            if (driver != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Driver Photo / Avatar
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Surface(
                                    shape = CircleShape,
                                    color = AmberPrimary.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, AmberPrimary),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "👨🏽‍✈️", fontSize = 28.sp)
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldEco,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = "Verified",
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = driver.name,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Rating",
                                            tint = StarGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "${driver.rating}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Text(
                                    text = "${driver.vehicleModel} • ${driver.standLocation}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Verified Driver Partner • ${driver.totalTrips} trips",
                                    fontSize = 11.sp,
                                    color = EmeraldEco,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Call & Chat buttons
                            Row {
                                IconButton(
                                    onClick = { showCallDialog = true },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldEco.copy(alpha = 0.15f))
                                        .testTag("driver_call_btn")
                                ) {
                                    Icon(
                                        Icons.Default.Call,
                                        contentDescription = "Call Driver",
                                        tint = EmeraldEco,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prominent Vehicle Registration Plate Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E293B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vehicle_number_plate_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // IND Blue Strip
                                    Surface(
                                        shape = RoundedCornerShape(3.dp),
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "IND",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = driver.vehiclePlate,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 2.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AmberPrimary
                                ) {
                                    Text(
                                        text = driver.vehicleType.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1E293B),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cancel Ride Button
            OutlinedButton(
                onClick = onCancelRide,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cancel_ride_btn")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Cancel Ride", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    // Call Dialog
    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("Call ${booking.driver?.name ?: "Driver"}") },
            text = { Text("Connecting free VoIP in-app call to ${booking.driver?.phone ?: "driver"} (Vehicle ${booking.driver?.vehiclePlate}).") },
            confirmButton = {
                Button(
                    onClick = {
                        showCallDialog = false
                        Toast.makeText(context, "Calling ${booking.driver?.name}...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldEco)
                ) {
                    Text("Call Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCallDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // SOS Emergency Alert Dialog
    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            icon = { Icon(Icons.Default.Security, contentDescription = "Emergency", tint = AlertRed) },
            title = { Text("Emergency SOS Assistance") },
            text = {
                Text("Your live trip telemetry, vehicle plate (${booking.driver?.vehiclePlate}), and GPS coordinates are ready to be dispatched to local police helpline (112) and fleet supervisor JOHNS KHAN (90800 40503).")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSosDialog = false
                        Toast.makeText(context, "SOS Alert dispatched with Live Location!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Broadcast SOS Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
