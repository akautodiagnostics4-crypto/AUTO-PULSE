package com.example.ui.screens.passenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.model.RideBooking
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco

@Composable
fun PaymentCheckoutSheet(
    booking: RideBooking,
    walletBalance: Double,
    selectedPaymentMethod: PaymentMethod,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onConfirmPayment: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    var cardCvv by remember { mutableStateOf("") }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("payment_checkout_sheet")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Trip Arrived & Amount Due
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Trip Completed",
                            tint = EmeraldEco,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trip Completed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Arrived at ${booking.dropoff.name}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${booking.finalFare.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total Fare",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Fare breakdown card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    FareRow("Base Fare", "₹${booking.rideType.baseFare.toInt()}")
                    FareRow("Distance (${String.format("%.1f", booking.distanceKm)} km)", "₹${(booking.fareAmount - booking.rideType.baseFare).toInt()}")
                    if (booking.discountAmount > 0) {
                        FareRow("Promo Discount", "-₹${booking.discountAmount.toInt()}", isGreen = true)
                    }
                    FareRow("Taxes & Tolls", "Included")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SELECT PAYMENT METHOD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Payment Methods Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethodChip(
                    method = PaymentMethod.UPI,
                    isSelected = selectedPaymentMethod == PaymentMethod.UPI,
                    onClick = { onSelectPaymentMethod(PaymentMethod.UPI) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    method = PaymentMethod.CARD,
                    isSelected = selectedPaymentMethod == PaymentMethod.CARD,
                    onClick = { onSelectPaymentMethod(PaymentMethod.CARD) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    method = PaymentMethod.WALLET,
                    isSelected = selectedPaymentMethod == PaymentMethod.WALLET,
                    subtitle = "₹${walletBalance.toInt()}",
                    onClick = { onSelectPaymentMethod(PaymentMethod.WALLET) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    method = PaymentMethod.CASH,
                    isSelected = selectedPaymentMethod == PaymentMethod.CASH,
                    onClick = { onSelectPaymentMethod(PaymentMethod.CASH) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Detailed input for selected method
            when (selectedPaymentMethod) {
                PaymentMethod.UPI -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Choose UPI Provider:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Google Pay", "PhonePe", "Paytm QR").forEach { upi ->
                                    val isChosen = selectedUpiApp == upi
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isChosen) AmberPrimary else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .clickable { selectedUpiApp = upi }
                                            .testTag("upi_app_${upi.replace(" ", "_")}")
                                    ) {
                                        Text(
                                            text = upi,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isChosen) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                PaymentMethod.CARD -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = "Card", tint = AmberPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "HDFC Visa •••• 4022", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Expires 08/29", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            OutlinedTextField(
                                value = cardCvv,
                                onValueChange = { if (it.length <= 3) cardCvv = it },
                                placeholder = { Text("CVV", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberPrimary
                                ),
                                modifier = Modifier
                                    .width(74.dp)
                                    .testTag("card_cvv_input")
                            )
                        }
                    }
                }
                PaymentMethod.WALLET -> {
                    val isSufficient = walletBalance >= booking.finalFare
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSufficient) EmeraldEco.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = if (isSufficient) EmeraldEco else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSufficient) "Wallet Balance: ₹${walletBalance.toInt()} (Sufficient)" else "Insufficient Wallet balance (₹${walletBalance.toInt()}). Top up or pick another method.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                PaymentMethod.CASH -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = "Cash", tint = EmeraldEco)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hand over ₹${booking.finalFare.toInt()} cash to ${booking.driver?.name ?: "the driver"}.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pay Button
            Button(
                onClick = { onConfirmPayment(selectedPaymentMethod) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberPrimary,
                    contentColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_payment_btn")
            ) {
                Text(
                    text = when (selectedPaymentMethod) {
                        PaymentMethod.CASH -> "Confirm Cash Paid (₹${booking.finalFare.toInt()})"
                        PaymentMethod.WALLET -> "Pay ₹${booking.finalFare.toInt()} from Wallet"
                        PaymentMethod.CARD -> "Authorize & Pay ₹${booking.finalFare.toInt()}"
                        PaymentMethod.UPI -> "Pay ₹${booking.finalFare.toInt()} via $selectedUpiApp"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun FareRow(title: String, amount: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = amount,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isGreen) EmeraldEco else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PaymentMethodChip(
    method: PaymentMethod,
    isSelected: Boolean,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (method) {
        PaymentMethod.UPI -> Icons.Default.QrCode
        PaymentMethod.CARD -> Icons.Default.CreditCard
        PaymentMethod.WALLET -> Icons.Default.AccountBalanceWallet
        PaymentMethod.CASH -> Icons.Default.Payments
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AmberPrimary) else null,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("payment_chip_${method.name}")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = method.title,
                tint = if (isSelected) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (method) {
                    PaymentMethod.UPI -> "UPI"
                    PaymentMethod.CARD -> "Card"
                    PaymentMethod.WALLET -> "Wallet"
                    PaymentMethod.CASH -> "Cash"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = if (isSelected) Color(0xFF1E293B) else EmeraldEco
                )
            }
        }
    }
}
