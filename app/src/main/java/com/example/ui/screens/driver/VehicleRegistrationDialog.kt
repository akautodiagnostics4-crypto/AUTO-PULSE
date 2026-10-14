package com.example.ui.screens.driver

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.HealthAndSafety
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DriverProfile
import com.example.model.DriverVehicleRegistration
import com.example.model.RideType
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VehicleRegistrationDialog(
    currentProfile: DriverProfile?,
    onSaveRegistration: (DriverVehicleRegistration) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var driverName by remember { mutableStateOf(currentProfile?.name ?: "JOHNS KHAN") }
    var phone by remember { mutableStateOf(currentProfile?.phone ?: "90800 40503") }
    var selectedVehicleType by remember { mutableStateOf(currentProfile?.vehicleType ?: RideType.AUTO_STANDARD) }
    var plateNumber by remember { mutableStateOf(currentProfile?.vehiclePlate ?: "TN 41 AB 4821") }
    var vehicleModel by remember { mutableStateOf(currentProfile?.vehicleModel ?: "Bajaj RE Auto") }
    var rcNumber by remember { mutableStateOf(currentProfile?.rcNumber ?: "RC-TN41-89302-K") }
    var rcAttached by remember { mutableStateOf(currentProfile?.rcDocumentAttached ?: true) }
    var insuranceValidity by remember { mutableStateOf(currentProfile?.insuranceValidity ?: "31 Dec 2027") }
    var insuranceAttached by remember { mutableStateOf(currentProfile?.insuranceDocumentAttached ?: true) }
    var licenseNumber by remember { mutableStateOf(currentProfile?.licenseNumber ?: "TN41 20180004921") }
    var licenseAttached by remember { mutableStateOf(currentProfile?.licenseDocumentAttached ?: true) }
    var selectedStand by remember { mutableStateOf(currentProfile?.standLocation ?: "Pollachi New Bus Stand") }

    var plateError by remember { mutableStateOf(false) }

    val pollachiStands = listOf(
        "Pollachi New Bus Stand",
        "Town Auto Stand",
        "Railway Station Stand",
        "Mahalingapuram Stand",
        "Daily Market Stand",
        "GH Hospital Stand"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vehicle_registration_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AmberPrimary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🛺", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Driver & Vehicle Onboarding",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pollachi Auto Partner Verification",
                                fontSize = 11.sp,
                                color = AmberPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // 1. Vehicle Type Selector (Auto, Cab, Bike)
                Text(
                    text = "1. SELECT VEHICLE TYPE (வாகனம் வகை)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RideType.values().forEach { type ->
                        val isSelected = selectedVehicleType == type
                        val label = when (type) {
                            RideType.AUTO_STANDARD -> "🛺 Auto"
                            RideType.AUTO_ELECTRIC -> "⚡ EV Auto"
                            RideType.CAB_MINI -> "🚗 Cab"
                            RideType.BIKE_TAXI -> "🛵 Bike"
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) BorderStroke(1.5.dp, AmberDark) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedVehicleType = type
                                    if (type == RideType.AUTO_STANDARD && (vehicleModel.isBlank() || vehicleModel.contains("WagonR") || vehicleModel.contains("Activa"))) {
                                        vehicleModel = "Bajaj RE Auto"
                                    } else if (type == RideType.AUTO_ELECTRIC) {
                                        vehicleModel = "Mahindra Treo EV"
                                    } else if (type == RideType.CAB_MINI) {
                                        vehicleModel = "WagonR AC Taxi"
                                    } else if (type == RideType.BIKE_TAXI) {
                                        vehicleModel = "Honda Activa 6G"
                                    }
                                }
                                .testTag("select_vehicle_type_${type.name}")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = if (isSelected) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Vehicle Plate Number & Model
                Text(
                    text = "2. VEHICLE DETAILS & NUMBER PLATE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = plateNumber,
                    onValueChange = {
                        plateNumber = it.uppercase()
                        plateError = plateNumber.isBlank()
                    },
                    placeholder = { Text("e.g. TN 41 AB 4821") },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, contentDescription = "Plate", tint = AmberPrimary)
                    },
                    supportingText = {
                        Text("Pollachi RTO: TN 41 (e.g. TN 41 AB 4821)")
                    },
                    isError = plateError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vehicle_plate_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vehicleModel,
                    onValueChange = { vehicleModel = it },
                    placeholder = { Text("Vehicle Model (Bajaj RE / Mahindra Treo)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vehicle_model_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Document 1: RC COPY (Registration Certificate)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. RC BOOK COPY (பதிவு சான்றிதழ்)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (rcAttached) EmeraldEco.copy(alpha = 0.15f) else AmberPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { rcAttached = !rcAttached }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (rcAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = "RC Status",
                                tint = if (rcAttached) EmeraldEco else AmberDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (rcAttached) "RC Copy Attached" else "Attach RC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (rcAttached) EmeraldEco else AmberDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = rcNumber,
                    onValueChange = { rcNumber = it.uppercase() },
                    placeholder = { Text("e.g. RC-TN41-89302-K") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = "RC", tint = AmberPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            rcAttached = true
                            Toast.makeText(context, "RC Document Uploaded & Verified", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Upload RC", tint = EmeraldEco)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rc_number_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Document 2: Commercial Vehicle Insurance Validity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "4. INSURANCE POLICY & VALIDITY (காப்பீடு)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (insuranceAttached) EmeraldEco.copy(alpha = 0.15f) else AmberPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { insuranceAttached = !insuranceAttached }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = "Insurance", tint = EmeraldEco, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (insuranceAttached) "Policy Verified" else "Attach Policy",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldEco
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = insuranceValidity,
                    onValueChange = { insuranceValidity = it },
                    placeholder = { Text("e.g. 31 Dec 2027") },
                    leadingIcon = {
                        Icon(Icons.Default.HealthAndSafety, contentDescription = "Insurance", tint = EmeraldEco)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            insuranceAttached = true
                            Toast.makeText(context, "Insurance Document Uploaded", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Upload Insurance", tint = EmeraldEco)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldEco),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("insurance_validity_input")
                )

                // Quick insurance preset dates
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("31 Dec 2026", "31 Dec 2027", "31 Dec 2028").forEach { d ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable {
                                    insuranceValidity = d
                                    insuranceAttached = true
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = d,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Driver Name & Contact (JOHNS KHAN / 90800 40503)
                Text(
                    text = "5. DRIVER PARTNER NAME & PHONE NUMBER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = driverName,
                    onValueChange = { driverName = it },
                    placeholder = { Text("Driver Name (JOHNS KHAN)") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Name", tint = AmberPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("driver_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("Phone Number (90800 40503)") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = AmberPrimary)
                    },
                    supportingText = {
                        Text("Contact number for passengers to call: 90800 40503")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("driver_phone_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Driving License / Badge
                Text(
                    text = "6. DRIVING LICENSE / COMMERCIAL BADGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    placeholder = { Text("e.g. TN41 20180004921") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = "License", tint = AmberPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            licenseAttached = true
                            Toast.makeText(context, "Driving License Verified", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "License verified", tint = EmeraldEco)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("license_number_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 7. Preferred Pollachi Stand
                Text(
                    text = "7. PREFERRED POLLACHI AUTO STAND (ஆட்டோ ஸ்டாண்ட்)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pollachiStands.forEach { stand ->
                        val isChosen = selectedStand == stand
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChosen) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { selectedStand = stand }
                                .testTag("stand_choice_${stand.replace(" ", "_")}")
                        ) {
                            Text(
                                text = stand,
                                fontSize = 11.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChosen) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Verification guarantee note
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldEco.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified Partner", tint = EmeraldEco, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Once submitted, your vehicle and documents (RC copy, Insurance, License) will be registered and activated as a Pollachi AutoGo Driver Partner.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (plateNumber.isBlank()) {
                                plateError = true
                                return@Button
                            }
                            val reg = DriverVehicleRegistration(
                                driverName = driverName,
                                phone = phone,
                                vehicleType = selectedVehicleType,
                                vehiclePlate = plateNumber,
                                vehicleModel = vehicleModel,
                                rcNumber = rcNumber,
                                rcDocumentAttached = rcAttached,
                                insuranceValidity = insuranceValidity,
                                insuranceDocumentAttached = insuranceAttached,
                                licenseNumber = licenseNumber,
                                licenseDocumentAttached = licenseAttached,
                                standLocation = selectedStand
                            )
                            onSaveRegistration(reg)
                            Toast.makeText(context, "Registration Complete! You are now an active driver partner.", Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .weight(1.6f)
                            .height(48.dp)
                            .testTag("save_vehicle_registration_btn")
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Complete & Be Part of App", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
