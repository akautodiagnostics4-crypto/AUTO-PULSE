package com.example.ui.screens.auth

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DriverVehicleRegistration
import com.example.model.RideType
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco
import com.example.ui.theme.EmeraldLight
import com.example.viewmodel.AutoGoViewModel

enum class SplitViewTab(val label: String, val icon: String) {
    SPLIT("Split View", "⚡"),
    RIDER("Rider (பயணி)", "🚶‍♂️"),
    DRIVER("Driver (ஓட்டுநர்)", "🛺")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplitAuthScreen(
    viewModel: AutoGoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Rider form credentials
    var riderName by remember { mutableStateOf("Karthik Subramanian") }
    var riderPhone by remember { mutableStateOf("98421 55678") }
    var riderLocation by remember { mutableStateOf("Pollachi New Bus Stand") }
    var riderNameError by remember { mutableStateOf(false) }
    var riderPhoneError by remember { mutableStateOf(false) }

    // Driver form credentials & documents
    var driverName by remember { mutableStateOf("JOHNS KHAN") }
    var driverPhone by remember { mutableStateOf("90800 40503") }
    var driverVehicleType by remember { mutableStateOf(RideType.AUTO_STANDARD) }
    var driverPlate by remember { mutableStateOf("TN 41 AB 4821") }
    var driverModel by remember { mutableStateOf("Bajaj RE Auto (Pollachi Town)") }
    var driverRcNumber by remember { mutableStateOf("RC-TN41-89302-K") }
    var driverRcAttached by remember { mutableStateOf(true) }
    var driverInsuranceValidity by remember { mutableStateOf("31 Dec 2027") }
    var driverInsuranceAttached by remember { mutableStateOf(true) }
    var driverLicenseNumber by remember { mutableStateOf("TN41 20180004921") }
    var driverLicenseAttached by remember { mutableStateOf(true) }
    var driverStand by remember { mutableStateOf("Pollachi Town Auto Stand") }

    var driverPlateError by remember { mutableStateOf(false) }

    val pollachiLocations = listOf(
        "Pollachi New Bus Stand",
        "Town Auto Stand",
        "Railway Station Stand",
        "Mahalingapuram",
        "Daily Market",
        "Anaimalai Junction"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Branding Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmberPrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_pollachi_auto_logo),
                                contentDescription = "AutoGo Logo",
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Pollachi ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "AutoGo",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AmberPrimary
                                )
                            }
                            Text(
                                text = "உள்ளூர் ஆட்டோ & பைக் சேவை",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Helpline Button for Johns Khan
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:9080040503")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Support: Johns Khan 90800 40503", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .testTag("support_helpline_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Call Support",
                                tint = EmeraldEco,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "90800 40503",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Selector for Split / Rider / Driver views
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = AmberPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SplitViewTab.values().forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = "${tab.icon} ${tab.label}",
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Main Body Content
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ==========================================
            // TOP PANEL / SECTION: RIDER LOGIN & CREDENTIALS
            // ==========================================
            if (selectedTab == 0 || selectedTab == 1) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.5.dp, AmberPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rider_section_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        AmberPrimary.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        // Rider Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = AmberPrimary,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "Rider Icon",
                                            tint = Color(0xFF1E293B)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "RIDER MODE (பயணி)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AmberPrimary.copy(alpha = 0.2f),
                                            modifier = Modifier.padding(2.dp)
                                        ) {
                                            Text(
                                                text = "Passenger",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AmberDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Enter location & book Auto, EV, Cab, or Bike easily across Pollachi",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Rider Inputs
                        Text(
                            text = "RIDER CREDENTIALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberDark,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Name Input
                        OutlinedTextField(
                            value = riderName,
                            onValueChange = {
                                riderName = it
                                riderNameError = it.isBlank()
                            },
                            label = { Text("Passenger Full Name / பெயர்") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AmberDark) },
                            singleLine = true,
                            isError = riderNameError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rider_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                focusedLabelColor = AmberDark
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Phone Input
                        OutlinedTextField(
                            value = riderPhone,
                            onValueChange = {
                                riderPhone = it
                                riderPhoneError = it.isBlank()
                            },
                            label = { Text("Mobile Phone Number / மொபைல்") },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = AmberDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+91", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = riderPhoneError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rider_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                focusedLabelColor = AmberDark
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preferred Stand / Pickup Area
                        Text(
                            text = "Default Pickup Stand in Pollachi:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pollachiLocations.take(4).forEach { loc ->
                                val selected = riderLocation == loc
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (selected) AmberDark else Color.Transparent),
                                    modifier = Modifier
                                        .clickable { riderLocation = loc }
                                        .testTag("rider_pickup_chip_${loc.replace(" ", "_")}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (selected) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = loc,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons for Rider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.quickDemoRider()
                                    Toast.makeText(context, "Logged in as Karthik S. (Rider)", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AmberDark),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("rider_quick_demo_button")
                            ) {
                                Text("Quick Demo", color = AmberDark, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (riderName.isBlank()) {
                                        riderNameError = true
                                        return@Button
                                    }
                                    if (riderPhone.isBlank()) {
                                        riderPhoneError = true
                                        return@Button
                                    }
                                    viewModel.loginAsRider(
                                        name = riderName.trim(),
                                        phone = riderPhone.trim(),
                                        pickupLocation = riderLocation
                                    )
                                    Toast.makeText(context, "Welcome to Pollachi AutoGo, ${riderName.trim()}!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberPrimary,
                                    contentColor = Color(0xFF1E293B)
                                ),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("rider_login_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Continue as Rider",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // BOTTOM PANEL / SECTION: DRIVER REGISTRATION & CREDENTIALS
            // ==========================================
            if (selectedTab == 0 || selectedTab == 2) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.5.dp, EmeraldEco.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("driver_section_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        EmeraldEco.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        // Driver Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldEco,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "🛺", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "DRIVER PARTNER (ஓட்டுநர்)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldEco.copy(alpha = 0.2f),
                                            modifier = Modifier.padding(2.dp)
                                        ) {
                                            Text(
                                                text = "Auto Captain",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldEco,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Complete vehicle registration, RC copy & insurance to join and receive ride rings",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Driver Inputs & Required Documents
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DRIVER CREDENTIALS & VEHICLE RC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldEco,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "All docs required",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Driver Name
                        OutlinedTextField(
                            value = driverName,
                            onValueChange = { driverName = it },
                            label = { Text("Driver Full Name / ஓட்டுநர் பெயர்") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldEco) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldEco,
                                focusedLabelColor = EmeraldEco
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Driver Phone Number
                        OutlinedTextField(
                            value = driverPhone,
                            onValueChange = { driverPhone = it },
                            label = { Text("Driver Mobile / அலைபேசி") },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldEco, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+91", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldEco,
                                focusedLabelColor = EmeraldEco
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Vehicle Type selector
                        Text(
                            text = "Vehicle Category in Pollachi:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RideType.values().forEach { vType ->
                                val selected = driverVehicleType == vType
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) EmeraldEco else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.5f) else Color.Transparent),
                                    modifier = Modifier
                                        .clickable { driverVehicleType = vType }
                                        .testTag("driver_vtype_${vType.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = when (vType) {
                                                RideType.AUTO_STANDARD -> "🛺"
                                                RideType.AUTO_ELECTRIC -> "⚡"
                                                RideType.CAB_MINI -> "🚕"
                                                RideType.BIKE_TAXI -> "🛵"
                                            },
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = vType.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Vehicle Registration Number Plate (TN 41 AB 4821)
                        OutlinedTextField(
                            value = driverPlate,
                            onValueChange = {
                                driverPlate = it.uppercase()
                                driverPlateError = it.isBlank()
                            },
                            label = { Text("Vehicle Registration Plate (வண்டி எண்)") },
                            placeholder = { Text("TN 41 AB 4821") },
                            leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = EmeraldEco) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            singleLine = true,
                            isError = driverPlateError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_plate_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldEco,
                                focusedLabelColor = EmeraldEco
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // RC Book Copy & RC Number
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (driverRcAttached) EmeraldEco.copy(alpha = 0.5f) else Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = EmeraldEco, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "RC Book (பதிவுச் சான்றிதழ்)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // RC Copy Upload toggle
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (driverRcAttached) EmeraldEco.copy(alpha = 0.15f) else AmberPrimary.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .clickable { driverRcAttached = !driverRcAttached }
                                            .testTag("driver_rc_upload_toggle")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                if (driverRcAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                                contentDescription = null,
                                                tint = if (driverRcAttached) EmeraldEco else AmberDark,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (driverRcAttached) "RC Copy Attached ✓" else "Upload RC Copy",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (driverRcAttached) EmeraldEco else AmberDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = driverRcNumber,
                                    onValueChange = { driverRcNumber = it.uppercase() },
                                    label = { Text("RC Number") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("driver_rc_number_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldEco,
                                        focusedLabelColor = EmeraldEco
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Insurance Policy & Copy
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (driverInsuranceAttached) EmeraldEco.copy(alpha = 0.5f) else Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldEco, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Insurance Policy (காப்பீடு)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Insurance Copy Upload toggle
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (driverInsuranceAttached) EmeraldEco.copy(alpha = 0.15f) else AmberPrimary.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .clickable { driverInsuranceAttached = !driverInsuranceAttached }
                                            .testTag("driver_insurance_upload_toggle")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                if (driverInsuranceAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                                contentDescription = null,
                                                tint = if (driverInsuranceAttached) EmeraldEco else AmberDark,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (driverInsuranceAttached) "Insurance Attached ✓" else "Upload Insurance",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (driverInsuranceAttached) EmeraldEco else AmberDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = driverInsuranceValidity,
                                    onValueChange = { driverInsuranceValidity = it },
                                    label = { Text("Insurance Validity Date") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("driver_insurance_validity_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldEco,
                                        focusedLabelColor = EmeraldEco
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Driving License
                        OutlinedTextField(
                            value = driverLicenseNumber,
                            onValueChange = { driverLicenseNumber = it.uppercase() },
                            label = { Text("Driving License Number (ஓட்டுநர் உரிமம்)") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = EmeraldEco) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_license_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldEco,
                                focusedLabelColor = EmeraldEco
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Auto Stand Location in Pollachi
                        Text(
                            text = "Auto Stand / Base Location in Pollachi:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pollachiLocations.take(4).forEach { stand ->
                                val selected = driverStand == stand
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) EmeraldEco else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.5f) else Color.Transparent),
                                    modifier = Modifier
                                        .clickable { driverStand = stand }
                                        .testTag("driver_stand_chip_${stand.replace(" ", "_")}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stand,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons for Driver
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.quickDemoDriver()
                                    Toast.makeText(context, "Logged in as Johns Khan (Verified Driver)", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, EmeraldEco),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("driver_autofill_button")
                            ) {
                                Text("Autofill Johns Khan", color = EmeraldEco, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    if (driverPlate.isBlank()) {
                                        driverPlateError = true
                                        Toast.makeText(context, "Please enter vehicle registration plate", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val reg = DriverVehicleRegistration(
                                        driverName = driverName.ifBlank { "JOHNS KHAN" },
                                        phone = driverPhone.ifBlank { "90800 40503" },
                                        vehicleType = driverVehicleType,
                                        vehiclePlate = driverPlate.trim().uppercase(),
                                        vehicleModel = driverModel,
                                        rcNumber = driverRcNumber,
                                        rcDocumentAttached = driverRcAttached,
                                        insuranceValidity = driverInsuranceValidity,
                                        insuranceDocumentAttached = driverInsuranceAttached,
                                        licenseNumber = driverLicenseNumber,
                                        licenseDocumentAttached = driverLicenseAttached,
                                        standLocation = driverStand
                                    )
                                    viewModel.loginOrRegisterAsDriver(reg)
                                    Toast.makeText(context, "Driver registered! Welcome to Pollachi AutoGo.", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldEco,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("driver_register_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Register & Enter Console",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
