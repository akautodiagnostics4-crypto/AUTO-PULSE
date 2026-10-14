package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppMode
import com.example.model.DriverJobRequest
import com.example.model.DriverProfile
import com.example.model.DriverStats
import com.example.model.DriverVehicleRegistration
import com.example.model.MapLocation
import com.example.model.PaymentMethod
import com.example.model.RideBooking
import com.example.model.RideType
import com.example.repository.AutoGoRepository
import com.example.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AutoGoViewModel(
    val repository: AutoGoRepository = AutoGoRepository()
) : ViewModel() {

    private val _currentMode = MutableStateFlow(AppMode.PASSENGER)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    // Authentication & Split-Screen Login/Registration State (defaults to true for immediate startup)
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Role Selection Dialog on startup
    private val _showRoleChooserDialog = MutableStateFlow(false)
    val showRoleChooserDialog: StateFlow<Boolean> = _showRoleChooserDialog.asStateFlow()

    // Driver document completion state
    private val _isDriverDocumentsComplete = MutableStateFlow(true)
    val isDriverDocumentsComplete: StateFlow<Boolean> = _isDriverDocumentsComplete.asStateFlow()

    val currentRiderProfile = repository.currentRiderProfile
    val userLocation: StateFlow<MapLocation> = repository.userLocation
    val selectedDropoff: StateFlow<MapLocation?> = repository.selectedDropoff
    val selectedRideType: StateFlow<RideType> = repository.selectedRideType
    val selectedPaymentMethod: StateFlow<PaymentMethod> = repository.selectedPaymentMethod
    val walletBalance: StateFlow<Double> = repository.walletBalance
    val activeBooking: StateFlow<RideBooking?> = repository.activeBooking
    val nearbyDrivers: StateFlow<List<DriverProfile>> = repository.nearbyDrivers
    val pastTrips: StateFlow<List<RideBooking>> = repository.pastTrips

    val isDriverOnline: StateFlow<Boolean> = repository.isDriverOnline
    val driverStats: StateFlow<DriverStats> = repository.driverStats
    val incomingJobRequest: StateFlow<DriverJobRequest?> = repository.incomingJobRequest

    val currentDriverProfile: StateFlow<DriverProfile?> = repository.currentDriverProfile

    // UI state toggles
    private val _showSearchSheet = MutableStateFlow(false)
    val showSearchSheet: StateFlow<Boolean> = _showSearchSheet.asStateFlow()

    private val _showHistorySheet = MutableStateFlow(false)
    val showHistorySheet: StateFlow<Boolean> = _showHistorySheet.asStateFlow()

    private val _showWalletSheet = MutableStateFlow(false)
    val showWalletSheet: StateFlow<Boolean> = _showWalletSheet.asStateFlow()

    private val _showVehicleRegistrationSheet = MutableStateFlow(false)
    val showVehicleRegistrationSheet: StateFlow<Boolean> = _showVehicleRegistrationSheet.asStateFlow()

    private val _promoCode = MutableStateFlow("")
    val promoCode: StateFlow<String> = _promoCode.asStateFlow()

    private val _otpInputError = MutableStateFlow(false)
    val otpInputError: StateFlow<Boolean> = _otpInputError.asStateFlow()

    fun switchMode(mode: AppMode) {
        _currentMode.value = mode
        if (mode == AppMode.DRIVER_CONSOLE && !_isDriverDocumentsComplete.value) {
            _showVehicleRegistrationSheet.value = true
        }
    }

    /**
     * Toggles between Rider (PASSENGER) and Driver (DRIVER_CONSOLE) views
     * using the single shared ViewModel state without creating new activities.
     */
    fun toggleMode() {
        val next = if (_currentMode.value == AppMode.PASSENGER) AppMode.DRIVER_CONSOLE else AppMode.PASSENGER
        switchMode(next)
    }

    fun openRoleChooser() {
        _showRoleChooserDialog.value = true
    }

    fun closeRoleChooser() {
        _showRoleChooserDialog.value = false
    }

    fun selectRole(mode: AppMode) {
        _currentMode.value = mode
        _showRoleChooserDialog.value = false
        if (mode == AppMode.DRIVER_CONSOLE && !_isDriverDocumentsComplete.value) {
            _showVehicleRegistrationSheet.value = true
        }
    }

    fun loginAsRider(name: String, phone: String, pickupLocation: String = "Pollachi New Bus Stand") {
        repository.updateRiderProfile(name, phone, pickupLocation)
        _currentMode.value = AppMode.PASSENGER
        _isAuthenticated.value = true
    }

    fun loginOrRegisterAsDriver(registration: DriverVehicleRegistration) {
        repository.registerDriverVehicle(registration)
        _isDriverDocumentsComplete.value = true
        _currentMode.value = AppMode.DRIVER_CONSOLE
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun quickDemoRider() {
        loginAsRider(
            name = "Karthik Subramanian",
            phone = "98421 55678",
            pickupLocation = "Pollachi New Bus Stand"
        )
    }

    fun quickDemoDriver() {
        loginOrRegisterAsDriver(
            DriverVehicleRegistration(
                driverName = "JOHNS KHAN",
                phone = "90800 40503",
                vehicleType = RideType.AUTO_STANDARD,
                vehiclePlate = "TN 41 AB 4821",
                vehicleModel = "Bajaj RE Auto (Pollachi Town)",
                rcNumber = "RC-TN41-89302-K",
                rcDocumentAttached = true,
                insuranceValidity = "31 Dec 2027",
                insuranceDocumentAttached = true,
                licenseNumber = "TN41 20180004921",
                licenseDocumentAttached = true,
                standLocation = "Pollachi Town Auto Stand"
            )
        )
    }

    fun openVehicleRegistration() { _showVehicleRegistrationSheet.value = true }
    fun closeVehicleRegistration() { _showVehicleRegistrationSheet.value = false }

    fun registerVehicle(reg: DriverVehicleRegistration) {
        repository.registerDriverVehicle(reg)
        _isDriverDocumentsComplete.value = true
        _showVehicleRegistrationSheet.value = false
    }

    fun openSearchSheet() { _showSearchSheet.value = true }
    fun closeSearchSheet() { _showSearchSheet.value = false }

    fun openHistorySheet() { _showHistorySheet.value = true }
    fun closeHistorySheet() { _showHistorySheet.value = false }

    fun openWalletSheet() { _showWalletSheet.value = true }
    fun closeWalletSheet() { _showWalletSheet.value = false }

    fun selectDropoff(location: MapLocation) {
        repository.setDropoffLocation(location)
        _showSearchSheet.value = false
    }

    fun selectPickup(location: MapLocation) {
        repository.setPickupLocation(location)
    }

    fun selectRideType(type: RideType) {
        repository.setRideType(type)
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        repository.setPaymentMethod(method)
    }

    fun setPromoCode(code: String) {
        _promoCode.value = code
    }

    fun requestRide(context: Context? = null) {
        repository.requestRide(_promoCode.value)
        if (context != null) {
            SoundManager.playRideBookingRingSound(context)
        }
    }

    fun cancelRide() {
        SoundManager.stopRingSound()
        repository.cancelRide()
    }

    fun completePayment(method: PaymentMethod) {
        repository.completePayment(method)
    }

    fun submitRating(stars: Int, tags: List<String>, tipAmount: Double, feedback: String) {
        repository.submitRating(stars, tags, tipAmount, feedback)
    }

    fun toggleDriverDuty() {
        repository.toggleDriverDuty()
    }

    fun driverAcceptJob() {
        SoundManager.stopRingSound()
        repository.driverAcceptJob()
    }

    fun driverDeclineJob() {
        SoundManager.stopRingSound()
        repository.driverDeclineJob()
    }

    fun driverVerifyOtp(otp: String): Boolean {
        val success = repository.verifyDriverOtp(otp)
        _otpInputError.value = !success
        return success
    }

    fun driverCompleteTrip() {
        repository.driverCompleteTripDirectly()
    }

    fun topUpWallet(amount: Double) {
        repository.topUpWallet(amount)
        _showWalletSheet.value = false
    }
}
