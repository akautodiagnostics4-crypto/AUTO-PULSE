package com.example.ui.screens.driver

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.model.DriverJobRequest
import com.example.model.DriverProfile
import com.example.model.DriverStats
import com.example.model.RideBooking
import com.example.model.RideStatus
import com.example.model.RideType
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco
import com.example.ui.theme.StarGold
import com.example.viewmodel.AutoGoViewModel

@Composable
fun DriverConsoleScreen(
    viewModel: AutoGoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isOnline by viewModel.isDriverOnline.collectAsState()
    val stats by viewModel.driverStats.collectAsState()
    val incomingJob by viewModel.incomingJobRequest.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val currentDriverProfile by viewModel.currentDriverProfile.collectAsState()
    val showRegistrationSheet by viewModel.showVehicleRegistrationSheet.collectAsState()
    val otpError by viewModel.otpInputError.collectAsState()

    var enteredOtp by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        // 1. Driver Profile & Duty Status Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("driver_duty_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AmberPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when (currentDriverProfile?.vehicleType) {
                                        RideType.AUTO_STANDARD -> "🛺"
                                        RideType.AUTO_ELECTRIC -> "⚡🛺"
                                        RideType.CAB_MINI -> "🚗"
                                        RideType.BIKE_TAXI -> "🛵"
                                        else -> "🛺"
                                    },
                                    fontSize = 26.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentDriverProfile?.name ?: "JOHNS KHAN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AmberPrimary.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = StarGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "${stats.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "${currentDriverProfile?.vehicleType?.displayName ?: "Auto"} • ${currentDriverProfile?.vehiclePlate ?: "TN 41 AB 4821"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary
                            )
                            Text(
                                text = "${currentDriverProfile?.vehicleModel ?: "Bajaj RE Auto"} • ${currentDriverProfile?.phone ?: "90800 40503"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Duty Toggle Switch
                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { viewModel.toggleDriverDuty() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldEco,
                                checkedTrackColor = EmeraldEco.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("driver_duty_switch")
                        )
                        Text(
                            text = if (isOnline) "ONLINE" else "OFFLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) EmeraldEco else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                // Vehicle Details, Insurance Validity & Registration CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Insurance",
                                tint = EmeraldEco,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Insurance: ${currentDriverProfile?.insuranceValidity ?: "31 Dec 2027"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldEco
                            )
                        }
                        Text(
                            text = "Stand: ${currentDriverProfile?.standLocation ?: "Pollachi Town Auto Stand"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Register / Edit Vehicle Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberPrimary.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, AmberPrimary),
                        modifier = Modifier
                            .clickable { viewModel.openVehicleRegistration() }
                            .testTag("edit_register_vehicle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Register Vehicle",
                                tint = AmberDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Register Vehicle",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberDark
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Incoming Dispatch Request Alert (if any)
        if (incomingJob != null && isOnline) {
            IncomingJobCard(
                job = incomingJob!!,
                onAccept = {
                    viewModel.driverAcceptJob()
                    Toast.makeText(context, "Ride Accepted! Navigate to pickup.", Toast.LENGTH_SHORT).show()
                },
                onDecline = {
                    viewModel.driverDeclineJob()
                    Toast.makeText(context, "Ride Declined", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Active Ride Driver Control Console
        if (activeBooking != null && (activeBooking?.status == RideStatus.DRIVER_ARRIVING || activeBooking?.status == RideStatus.IN_TRANSIT || activeBooking?.status == RideStatus.PAYMENT_PENDING)) {
            val booking = activeBooking!!
            ActiveDriverJobCard(
                booking = booking,
                enteredOtp = enteredOtp,
                otpError = otpError,
                onOtpChange = { enteredOtp = it },
                onVerifyOtp = {
                    val ok = viewModel.driverVerifyOtp(enteredOtp)
                    if (ok) {
                        Toast.makeText(context, "OTP Verified! Trip Started.", Toast.LENGTH_SHORT).show()
                        enteredOtp = ""
                    }
                },
                onCompleteTrip = {
                    viewModel.driverCompleteTrip()
                    Toast.makeText(context, "Trip Completed! Collect Payment.", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Driver Shift Earnings & Performance Dashboard
        Text(
            text = "TODAY'S SHIFT SUMMARY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Total Earnings",
                value = "₹${stats.todayEarnings.toInt()}",
                subtitle = "+₹150 Tips",
                color = EmeraldEco,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Trips Done",
                value = "${stats.tripsCompleted}",
                subtitle = "Goal: 15 trips",
                color = AmberPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Acceptance Rate",
                value = "${stats.acceptanceRate}%",
                subtitle = "Platinum Tier",
                color = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Online Hours",
                value = "${stats.onlineHours}h",
                subtitle = "Started 7:00 AM",
                color = Color(0xFFA855F7),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auto Driver Community Tips Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Tips",
                        tint = AmberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "High Demand Surge Areas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔥 Pollachi New Bus Stand & Railway Station have 3x ride requests right now. Head towards Palakkad Road or Station Road for continuous passenger bookings in Pollachi.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 5. Vehicle Registration Modal Dialog
        if (showRegistrationSheet) {
            VehicleRegistrationDialog(
                currentProfile = currentDriverProfile,
                onSaveRegistration = { reg ->
                    viewModel.registerVehicle(reg)
                    Toast.makeText(context, "Vehicle ${reg.vehiclePlate} registered successfully!", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { viewModel.closeVehicleRegistration() }
            )
        }
    }
}

@Composable
private fun IncomingJobCard(
    job: DriverJobRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, AmberPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("incoming_job_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Incoming",
                        tint = AmberPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEW RIDE REQUEST",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = AmberPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = AlertRed,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${job.timeoutSeconds}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Passenger & Fare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Passenger: ${job.passengerName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${job.pickupDistanceKm} km away from your location",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${job.booking.finalFare.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldEco
                    )
                    Text(
                        text = "Estimated Fare",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pickup & Dropoff
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🟢", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = job.booking.pickup.name, fontSize = 12.sp, color = Color.White, maxLines = 1)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔴", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = job.booking.dropoff.name, fontSize = 12.sp, color = Color.White, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AlertRed
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("driver_decline_job_btn")
                ) {
                    Text("Decline", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldEco,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("driver_accept_job_btn")
                ) {
                    Text("ACCEPT RIDE", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun ActiveDriverJobCard(
    booking: RideBooking,
    enteredOtp: String,
    otpError: Boolean,
    onOtpChange: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onCompleteTrip: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_driver_job_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ACTIVE TRIP CONTROLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (booking.status) {
                RideStatus.DRIVER_ARRIVING -> {
                    Text(
                        text = "Navigating to Pickup: ${booking.pickup.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Passenger: Arjun K. • Verify OTP upon arrival",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // OTP Input for Driver
                    Text(
                        text = "ENTER 4-DIGIT PASSENGER OTP:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = onOtpChange,
                            placeholder = { Text("e.g. ${booking.safetyOtp}") },
                            singleLine = true,
                            isError = otpError,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("driver_otp_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onVerifyOtp,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldEco
                            ),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("driver_verify_otp_btn")
                        ) {
                            Text("Start Trip", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (otpError) {
                        Text(
                            text = "Invalid OTP code. Please ask the passenger.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                RideStatus.IN_TRANSIT -> {
                    Text(
                        text = "Driving to: ${booking.dropoff.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Trip progress: ${(booking.progress * 100).toInt()}% • Fare ₹${booking.finalFare.toInt()}",
                        fontSize = 12.sp,
                        color = EmeraldEco,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCompleteTrip,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("driver_arrive_dropoff_btn")
                    ) {
                        Text("ARRIVE AT DESTINATION & COLLECT FARE", fontWeight = FontWeight.Black)
                    }
                }

                RideStatus.PAYMENT_PENDING -> {
                    Text(
                        text = "Collect Payment from Passenger",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Payment Mode: ${booking.paymentMethod.title} • Amount: ₹${booking.finalFare.toInt()}",
                        fontSize = 13.sp,
                        color = EmeraldEco,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Waiting for passenger payment confirmation or cash receipt.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
