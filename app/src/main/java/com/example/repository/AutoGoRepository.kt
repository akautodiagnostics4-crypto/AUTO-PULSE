package com.example.repository

import com.example.model.DriverJobRequest
import com.example.model.DriverProfile
import com.example.model.DriverStats
import com.example.model.MapLocation
import com.example.model.PaymentMethod
import com.example.model.RideBooking
import com.example.model.RideStatus
import com.example.model.RideType
import com.example.model.TripRating
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

class AutoGoRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    // Preset Landmarks & Popular Places in Pollachi
    val presetLocations = listOf(
        MapLocation("loc_bus_stand", "Pollachi New Bus Stand", "Palakkad Road, Pollachi", 490f, 490f, "Transit"),
        MapLocation("loc_station", "Pollachi Junction Railway Station", "Station Road, Pollachi", 200f, 270f, "Transit"),
        MapLocation("loc_mahalingapuram", "Mahalingapuram Roundana", "Coimbatore Main Road, Pollachi", 700f, 300f, "Town Center"),
        MapLocation("loc_market", "Gandhi Statue & Daily Market", "Market Road, Pollachi", 750f, 520f, "Shopping"),
        MapLocation("loc_hospital", "Pollachi Government Hospital (GH)", "Udumalpet Road, Pollachi", 320f, 720f, "Medical"),
        MapLocation("loc_aachipatti", "Aachi Patti Residential Colony", "Meenkarai Road, Pollachi", 180f, 490f, "Home"),
        MapLocation("loc_subcollector", "Sub-Collector Office & Court", "Court Road, Pollachi", 520f, 220f, "Govt"),
        MapLocation("loc_anaimalai", "Anaimalai Gateway Junction", "Sethumadai Road, Pollachi", 820f, 740f, "Leisure")
    )

    private val _userLocation = MutableStateFlow(presetLocations[0])
    val userLocation: StateFlow<MapLocation> = _userLocation.asStateFlow()

    private val _selectedDropoff = MutableStateFlow<MapLocation?>(presetLocations[1])
    val selectedDropoff: StateFlow<MapLocation?> = _selectedDropoff.asStateFlow()

    private val _selectedRideType = MutableStateFlow(RideType.AUTO_STANDARD)
    val selectedRideType: StateFlow<RideType> = _selectedRideType.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.UPI)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _walletBalance = MutableStateFlow(350.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    // Active Ride Booking State
    private val _activeBooking = MutableStateFlow<RideBooking?>(null)
    val activeBooking: StateFlow<RideBooking?> = _activeBooking.asStateFlow()

    // Past Trips History
    private val _pastTrips = MutableStateFlow<List<RideBooking>>(emptyList())
    val pastTrips: StateFlow<List<RideBooking>> = _pastTrips.asStateFlow()

    // Ambient Nearby Drivers pool
    private val _nearbyDrivers = MutableStateFlow<List<DriverProfile>>(emptyList())
    val nearbyDrivers: StateFlow<List<DriverProfile>> = _nearbyDrivers.asStateFlow()

    // Driver Partner Mode State
    private val _isDriverOnline = MutableStateFlow(true)
    val isDriverOnline: StateFlow<Boolean> = _isDriverOnline.asStateFlow()

    private val _currentDriverProfile = MutableStateFlow<DriverProfile?>(null)
    val currentDriverProfile: StateFlow<DriverProfile?> = _currentDriverProfile.asStateFlow()

    private val _driverStats = MutableStateFlow(DriverStats())
    val driverStats: StateFlow<DriverStats> = _driverStats.asStateFlow()

    private val _incomingJobRequest = MutableStateFlow<DriverJobRequest?>(null)
    val incomingJobRequest: StateFlow<DriverJobRequest?> = _incomingJobRequest.asStateFlow()

    private var trackingJob: Job? = null
    private var ambientMotionJob: Job? = null
    private var jobCountdownJob: Job? = null

    init {
        initializeInitialData()
        startAmbientVehicleMotion()
    }

    private fun initializeInitialData() {
        val drivers = listOf(
            DriverProfile(
                id = "drv_1",
                name = "JOHNS KHAN",
                phone = "+91 90800 40503",
                rating = 4.95f,
                totalTrips = 2450,
                vehicleType = RideType.AUTO_STANDARD,
                vehiclePlate = "TN 41 AB 4821",
                vehicleModel = "Bajaj RE Auto (Pollachi Town Stand)",
                x = 240f,
                y = 440f,
                heading = 45f
            ),
            DriverProfile(
                id = "drv_2",
                name = "Senthil Kumar",
                phone = "+91 98422 11024",
                rating = 4.88f,
                totalTrips = 1420,
                vehicleType = RideType.AUTO_ELECTRIC,
                vehiclePlate = "TN 41 EV 1024",
                vehicleModel = "Mahindra Treo EV (Station Stand)",
                x = 140f,
                y = 520f,
                heading = 135f
            ),
            DriverProfile(
                id = "drv_3",
                name = "Murugan R.",
                phone = "+91 94433 88120",
                rating = 4.82f,
                totalTrips = 1910,
                vehicleType = RideType.AUTO_STANDARD,
                vehiclePlate = "TN 41 F 3320",
                vehicleModel = "Piaggio Ape Auto (Bus Stand)",
                x = 220f,
                y = 560f,
                heading = 270f
            ),
            DriverProfile(
                id = "drv_4",
                name = "Karthi V.",
                phone = "+91 97870 55667",
                rating = 4.86f,
                totalTrips = 1120,
                vehicleType = RideType.CAB_MINI,
                vehiclePlate = "TN 41 CA 8831",
                vehicleModel = "WagonR AC Taxi (Mahalingapuram)",
                x = 350f,
                y = 480f,
                heading = 90f
            ),
            DriverProfile(
                id = "drv_5",
                name = "Vignesh M.",
                phone = "+91 99440 22720",
                rating = 4.92f,
                totalTrips = 3100,
                vehicleType = RideType.BIKE_TAXI,
                vehiclePlate = "TN 41 BK 7720",
                vehicleModel = "Honda Activa 6G (Market Express)",
                x = 160f,
                y = 410f,
                heading = 315f
            )
        )
        _nearbyDrivers.value = drivers
        _currentDriverProfile.value = drivers[0]

        // Sample initial past trip in Pollachi
        val samplePast = RideBooking(
            id = "TRIP_POLLACHI_101",
            pickup = presetLocations[0], // New Bus Stand
            dropoff = presetLocations[1], // Railway Station
            rideType = RideType.AUTO_STANDARD,
            distanceKm = 2.4f,
            estimatedMinutes = 8,
            fareAmount = 66.0,
            discountAmount = 15.0,
            finalFare = 51.0,
            safetyOtp = "4821",
            status = RideStatus.COMPLETED,
            driver = drivers[0],
            paymentMethod = PaymentMethod.UPI,
            paymentStatus = "PAID",
            rating = TripRating(5, listOf("Polite Driver", "Smooth Ride", "Safe Driving"), 10.0, "Very fast and polite service in Pollachi town!"),
            timestamp = System.currentTimeMillis() - 86400000L
        )
        _pastTrips.value = listOf(samplePast)
    }

    private fun startAmbientVehicleMotion() {
        ambientMotionJob?.cancel()
        ambientMotionJob = scope.launch {
            while (isActive) {
                delay(1200)
                _nearbyDrivers.value = _nearbyDrivers.value.map { driver ->
                    // Drift vehicles slightly on the road network
                    val dx = (Random.nextFloat() - 0.5f) * 12f
                    val dy = (Random.nextFloat() - 0.5f) * 12f
                    val newX = (driver.x + dx).coerceIn(100f, 900f)
                    val newY = (driver.y + dy).coerceIn(100f, 900f)
                    driver.copy(
                        x = newX,
                        y = newY,
                        heading = (driver.heading + Random.nextInt(-15, 16) + 360) % 360
                    )
                }
            }
        }
    }

    fun setDropoffLocation(location: MapLocation) {
        _selectedDropoff.value = location
    }

    fun setPickupLocation(location: MapLocation) {
        _userLocation.value = location
    }

    fun setRideType(type: RideType) {
        _selectedRideType.value = type
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun calculateDistanceKm(from: MapLocation, to: MapLocation): Float {
        val dx = (to.x - from.x)
        val dy = (to.y - from.y)
        val pixelDist = hypot(dx, dy)
        // 100 map pixels ~ 1.2 km
        return ((pixelDist / 100f) * 1.2f).coerceAtLeast(1.0f)
    }

    fun calculateFare(rideType: RideType, distanceKm: Float): Double {
        val raw = rideType.baseFare + (distanceKm * rideType.perKmRate)
        return (Math.round(raw * 10.0) / 10.0)
    }

    fun requestRide(promoCode: String? = null) {
        val pickup = _userLocation.value
        val dropoff = _selectedDropoff.value ?: presetLocations[1]
        val rideType = _selectedRideType.value
        val distance = calculateDistanceKm(pickup, dropoff)
        val fare = calculateFare(rideType, distance)
        val discount = if (promoCode.equals("AUTOGO50", ignoreCase = true) || promoCode.equals("SAVE50", ignoreCase = true)) 30.0 else 0.0
        val finalFare = (fare - discount).coerceAtLeast(20.0)

        // Generate 4-digit Safety OTP
        val otp = (1000..9999).random().toString()

        val booking = RideBooking(
            id = "TRIP_${Random.nextInt(1000, 9999)}",
            pickup = pickup,
            dropoff = dropoff,
            rideType = rideType,
            distanceKm = distance,
            estimatedMinutes = (distance * 3.2f).toInt().coerceAtLeast(5),
            fareAmount = fare,
            discountAmount = discount,
            finalFare = finalFare,
            safetyOtp = otp,
            status = RideStatus.SEARCHING_DRIVER,
            paymentMethod = _selectedPaymentMethod.value
        )

        _activeBooking.value = booking

        // Simulate Dispatch to Driver Console & Driver matching
        triggerDriverDispatchSimulation(booking)
    }

    private fun triggerDriverDispatchSimulation(booking: RideBooking) {
        // Post incoming job request to Driver Console
        _incomingJobRequest.value = DriverJobRequest(
            id = "JOB_${booking.id}",
            booking = booking,
            passengerName = "Arjun K.",
            pickupDistanceKm = 0.8f,
            timeoutSeconds = 15
        )

        // Start countdown timer for driver job
        startDriverJobCountdown()

        // Background simulation for passenger side matching
        scope.launch {
            delay(2800) // 2.8s radar search
            if (_activeBooking.value?.status == RideStatus.SEARCHING_DRIVER) {
                val assignedDriver = _nearbyDrivers.value.firstOrNull { it.vehicleType == booking.rideType }
                    ?: _nearbyDrivers.value.first()

                _activeBooking.value = _activeBooking.value?.copy(
                    status = RideStatus.DRIVER_ARRIVING,
                    driver = assignedDriver,
                    progress = 0f
                )

                startRealTimeTrackingSimulation()
            }
        }
    }

    private fun startDriverJobCountdown() {
        jobCountdownJob?.cancel()
        jobCountdownJob = scope.launch {
            for (sec in 15 downTo 1) {
                delay(1000)
                val current = _incomingJobRequest.value ?: break
                _incomingJobRequest.value = current.copy(timeoutSeconds = sec - 1)
            }
            if (_incomingJobRequest.value?.timeoutSeconds == 0) {
                _incomingJobRequest.value = null
            }
        }
    }

    private fun startRealTimeTrackingSimulation() {
        trackingJob?.cancel()
        trackingJob = scope.launch {
            // Phase 1: Driver arriving to pickup (progress 0f to 1f)
            val arrivalSteps = 20
            for (i in 1..arrivalSteps) {
                delay(600)
                val b = _activeBooking.value ?: break
                if (b.status != RideStatus.DRIVER_ARRIVING) break
                val prog = i.toFloat() / arrivalSteps
                _activeBooking.value = b.copy(progress = prog)
            }

            // Driver arrived at pickup
            val bArrived = _activeBooking.value
            if (bArrived != null && bArrived.status == RideStatus.DRIVER_ARRIVING) {
                _activeBooking.value = bArrived.copy(
                    status = RideStatus.IN_TRANSIT,
                    progress = 0f
                )
            }

            // Phase 2: In transit to dropoff (progress 0f to 1f)
            val tripSteps = 30
            for (i in 1..tripSteps) {
                delay(700)
                val b = _activeBooking.value ?: break
                if (b.status != RideStatus.IN_TRANSIT) break
                val prog = i.toFloat() / tripSteps
                _activeBooking.value = b.copy(progress = prog)
            }

            // Arrived at destination -> PAYMENT_PENDING
            val bDone = _activeBooking.value
            if (bDone != null && bDone.status == RideStatus.IN_TRANSIT) {
                _activeBooking.value = bDone.copy(
                    status = RideStatus.PAYMENT_PENDING,
                    progress = 1f
                )
            }
        }
    }

    fun completePayment(method: PaymentMethod) {
        val current = _activeBooking.value ?: return

        // If paying with wallet, deduct wallet balance
        if (method == PaymentMethod.WALLET) {
            _walletBalance.value = (_walletBalance.value - current.finalFare).coerceAtLeast(0.0)
        }

        val completedBooking = current.copy(
            paymentMethod = method,
            paymentStatus = "PAID",
            status = RideStatus.COMPLETED
        )

        _activeBooking.value = completedBooking
        _pastTrips.value = listOf(completedBooking) + _pastTrips.value

        // Update driver stats
        val curStats = _driverStats.value
        _driverStats.value = curStats.copy(
            todayEarnings = curStats.todayEarnings + current.finalFare,
            tripsCompleted = curStats.tripsCompleted + 1
        )
    }

    fun submitRating(stars: Int, tags: List<String>, tipAmount: Double, feedback: String) {
        val current = _activeBooking.value ?: return
        val ratingObj = TripRating(
            stars = stars,
            tags = tags,
            tipAmount = tipAmount,
            feedback = feedback
        )

        if (tipAmount > 0 && current.paymentMethod == PaymentMethod.WALLET) {
            _walletBalance.value = (_walletBalance.value - tipAmount).coerceAtLeast(0.0)
        }

        val updated = current.copy(rating = ratingObj)
        _activeBooking.value = updated

        // Update in history
        _pastTrips.value = _pastTrips.value.map { if (it.id == updated.id) updated else it }

        // Close active booking after short moment
        scope.launch {
            delay(1200)
            _activeBooking.value = null
        }
    }

    fun cancelRide() {
        trackingJob?.cancel()
        _activeBooking.value = null
        _incomingJobRequest.value = null
    }

    // --- Driver Console Actions ---
    fun toggleDriverDuty() {
        _isDriverOnline.value = !_isDriverOnline.value
    }

    fun driverAcceptJob() {
        jobCountdownJob?.cancel()
        val request = _incomingJobRequest.value ?: return
        _incomingJobRequest.value = null

        val assignedDriver = _nearbyDrivers.value.first()
        _activeBooking.value = request.booking.copy(
            status = RideStatus.DRIVER_ARRIVING,
            driver = assignedDriver,
            progress = 0f
        )
        startRealTimeTrackingSimulation()
    }

    fun driverDeclineJob() {
        jobCountdownJob?.cancel()
        _incomingJobRequest.value = null
    }

    fun verifyDriverOtp(inputOtp: String): Boolean {
        val booking = _activeBooking.value ?: return false
        if (booking.safetyOtp == inputOtp.trim()) {
            _activeBooking.value = booking.copy(
                status = RideStatus.IN_TRANSIT,
                progress = 0f
            )
            return true
        }
        return false
    }

    fun driverCompleteTripDirectly() {
        trackingJob?.cancel()
        val b = _activeBooking.value ?: return
        _activeBooking.value = b.copy(status = RideStatus.PAYMENT_PENDING)
    }

    fun topUpWallet(amount: Double) {
        _walletBalance.value = _walletBalance.value + amount
    }

    fun registerDriverVehicle(reg: com.example.model.DriverVehicleRegistration) {
        val current = _currentDriverProfile.value ?: _nearbyDrivers.value.first()
        val updated = current.copy(
            name = reg.driverName.ifBlank { current.name },
            phone = reg.phone.ifBlank { current.phone },
            vehicleType = reg.vehicleType,
            vehiclePlate = reg.vehiclePlate.trim().uppercase(),
            vehicleModel = reg.vehicleModel.ifBlank { current.vehicleModel },
            insuranceValidity = reg.insuranceValidity,
            licenseNumber = reg.licenseNumber.ifBlank { current.licenseNumber },
            standLocation = reg.standLocation
        )
        _currentDriverProfile.value = updated
        _nearbyDrivers.value = _nearbyDrivers.value.map {
            if (it.id == updated.id) updated else it
        }
    }
}
