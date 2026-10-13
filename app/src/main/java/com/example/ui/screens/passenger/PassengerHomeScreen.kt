package com.example.ui.screens.passenger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MapLocation
import com.example.model.RideBooking
import com.example.model.RideStatus
import com.example.ui.components.LiveInteractiveMap
import com.example.ui.theme.AmberPrimary
import com.example.viewmodel.AutoGoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerHomeScreen(
    viewModel: AutoGoViewModel,
    modifier: Modifier = Modifier
) {
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedDropoff by viewModel.selectedDropoff.collectAsState()
    val selectedRideType by viewModel.selectedRideType.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val nearbyDrivers by viewModel.nearbyDrivers.collectAsState()
    val pastTrips by viewModel.pastTrips.collectAsState()

    val showSearchSheet by viewModel.showSearchSheet.collectAsState()
    val showHistorySheet by viewModel.showHistorySheet.collectAsState()
    val showWalletSheet by viewModel.showWalletSheet.collectAsState()
    val promoCode by viewModel.promoCode.collectAsState()

    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val distanceKm = remember(userLocation, selectedDropoff) {
        if (selectedDropoff != null) {
            viewModel.repository.calculateDistanceKm(userLocation, selectedDropoff!!)
        } else 3.5f
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Live Interactive Map Engine in Background
        LiveInteractiveMap(
            booking = activeBooking,
            nearbyDrivers = nearbyDrivers,
            userLocation = userLocation,
            onLocationSelected = { loc ->
                if (activeBooking == null || activeBooking?.status == RideStatus.IDLE) {
                    viewModel.selectDropoff(loc)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Location Quick Chips (Only visible when Idle)
        if (activeBooking == null || activeBooking?.status == RideStatus.IDLE) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.repository.presetLocations.forEach { loc ->
                        val isSelected = selectedDropoff?.id == loc.id
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .testTag("quick_chip_${loc.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = loc.name,
                                    tint = if (isSelected) androidx.compose.ui.graphics.Color(0xFF1E293B) else AmberPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = loc.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Dynamic Bottom Sheets based on Ride Status
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            when (val b = activeBooking) {
                null, RideBooking(status = RideStatus.IDLE, id = "", pickup = userLocation, dropoff = userLocation, rideType = selectedRideType, distanceKm = 0f, estimatedMinutes = 0, fareAmount = 0.0, finalFare = 0.0, safetyOtp = "") -> {
                    RideSelectionSheet(
                        pickup = userLocation,
                        dropoff = selectedDropoff,
                        selectedType = selectedRideType,
                        selectedPaymentMethod = selectedPaymentMethod,
                        distanceKm = distanceKm,
                        promoCode = promoCode,
                        onSelectType = { viewModel.selectRideType(it) },
                        onOpenSearch = { viewModel.openSearchSheet() },
                        onApplyPromo = { viewModel.setPromoCode(it) },
                        onRequestRide = { viewModel.requestRide() }
                    )
                }

                else -> {
                    when (b.status) {
                        RideStatus.IDLE -> {
                            RideSelectionSheet(
                                pickup = userLocation,
                                dropoff = selectedDropoff,
                                selectedType = selectedRideType,
                                selectedPaymentMethod = selectedPaymentMethod,
                                distanceKm = distanceKm,
                                promoCode = promoCode,
                                onSelectType = { viewModel.selectRideType(it) },
                                onOpenSearch = { viewModel.openSearchSheet() },
                                onApplyPromo = { viewModel.setPromoCode(it) },
                                onRequestRide = { viewModel.requestRide() }
                            )
                        }

                        RideStatus.SEARCHING_DRIVER -> {
                            SearchingDriverCard(
                                booking = b,
                                onCancel = { viewModel.cancelRide() }
                            )
                        }

                        RideStatus.DRIVER_ASSIGNED, RideStatus.DRIVER_ARRIVING, RideStatus.IN_TRANSIT -> {
                            ActiveRideTrackingSheet(
                                booking = b,
                                onCancelRide = { viewModel.cancelRide() }
                            )
                        }

                        RideStatus.PAYMENT_PENDING -> {
                            PaymentCheckoutSheet(
                                booking = b,
                                walletBalance = walletBalance,
                                selectedPaymentMethod = selectedPaymentMethod,
                                onSelectPaymentMethod = { viewModel.selectPaymentMethod(it) },
                                onConfirmPayment = { viewModel.completePayment(it) }
                            )
                        }

                        RideStatus.COMPLETED -> {
                            if (b.rating == null) {
                                RatingFeedbackDialog(
                                    driver = b.driver,
                                    finalFare = b.finalFare,
                                    onSubmitRating = { stars, tags, tip, feedback ->
                                        viewModel.submitRating(stars, tags, tip, feedback)
                                    },
                                    onDismiss = {
                                        viewModel.submitRating(5, emptyList(), 0.0, "")
                                    }
                                )
                            }
                        }

                        RideStatus.CANCELLED -> {
                            // Automatically handled by view model resetting state
                        }
                    }
                }
            }
        }

        // Modals & Sheets
        if (showSearchSheet) {
            SearchLocationSheet(
                sheetState = searchSheetState,
                locations = viewModel.repository.presetLocations,
                currentPickup = userLocation,
                onSelectPickup = { viewModel.selectPickup(it) },
                onSelectDropoff = { viewModel.selectDropoff(it) },
                onDismiss = { viewModel.closeSearchSheet() }
            )
        }

        if (showHistorySheet) {
            TripHistorySheet(
                sheetState = historySheetState,
                trips = pastTrips,
                onDismiss = { viewModel.closeHistorySheet() }
            )
        }

        if (showWalletSheet) {
            WalletTopUpDialog(
                currentBalance = walletBalance,
                onTopUp = { viewModel.topUpWallet(it) },
                onDismiss = { viewModel.closeWalletSheet() }
            )
        }
    }
}
