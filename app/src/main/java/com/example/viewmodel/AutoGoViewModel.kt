package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppMode
import com.example.model.DriverJobRequest
import com.example.model.DriverProfile
import com.example.model.DriverStats
import com.example.model.MapLocation
import com.example.model.PaymentMethod
import com.example.model.RideBooking
import com.example.model.RideType
import com.example.repository.AutoGoRepository
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
    }

    fun openVehicleRegistration() { _showVehicleRegistrationSheet.value = true }
    fun closeVehicleRegistration() { _showVehicleRegistrationSheet.value = false }

    fun registerVehicle(reg: com.example.model.DriverVehicleRegistration) {
        repository.registerDriverVehicle(reg)
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

    fun requestRide() {
        repository.requestRide(_promoCode.value)
    }

    fun cancelRide() {
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
        repository.driverAcceptJob()
    }

    fun driverDeclineJob() {
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
