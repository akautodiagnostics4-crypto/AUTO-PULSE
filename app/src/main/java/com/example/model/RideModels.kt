package com.example.model

enum class AppMode {
    PASSENGER,
    DRIVER_CONSOLE
}

enum class RideType(
    val displayName: String,
    val subtitle: String,
    val baseFare: Double,
    val perKmRate: Double,
    val capacity: Int,
    val isElectric: Boolean,
    val etaMinutes: Int
) {
    AUTO_STANDARD(
        displayName = "Pollachi Auto",
        subtitle = "Classic 3-wheeler, swift in town traffic",
        baseFare = 30.0,
        perKmRate = 15.0,
        capacity = 3,
        isElectric = false,
        etaMinutes = 2
    ),
    AUTO_ELECTRIC(
        displayName = "Green EV Auto",
        subtitle = "Zero emissions, quiet & smooth ride",
        baseFare = 35.0,
        perKmRate = 14.0,
        capacity = 3,
        isElectric = true,
        etaMinutes = 4
    ),
    CAB_MINI(
        displayName = "Town Cab AC",
        subtitle = "AC hatchback comfort with boot space",
        baseFare = 60.0,
        perKmRate = 22.0,
        capacity = 4,
        isElectric = false,
        etaMinutes = 5
    ),
    BIKE_TAXI(
        displayName = "Bike Taxi",
        subtitle = "Fastest single commute through town",
        baseFare = 20.0,
        perKmRate = 9.0,
        capacity = 1,
        isElectric = false,
        etaMinutes = 1
    )
}

enum class RideStatus {
    IDLE,
    SEARCHING_DRIVER,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVING,
    IN_TRANSIT,
    PAYMENT_PENDING,
    COMPLETED,
    CANCELLED
}

enum class PaymentMethod(val title: String, val iconName: String) {
    UPI(title = "UPI (GPay / PhonePe)", iconName = "qr_code"),
    CARD(title = "Credit / Debit Card", iconName = "credit_card"),
    WALLET(title = "AutoGo Wallet", iconName = "account_balance_wallet"),
    CASH(title = "Cash on Arrival", iconName = "payments")
}

data class MapLocation(
    val id: String,
    val name: String,
    val address: String,
    val x: Float, // 0..1000 coordinate space
    val y: Float, // 0..1000 coordinate space
    val category: String
)

data class RiderProfile(
    val id: String = "rider_1",
    val name: String = "Karthik Subramanian",
    val phone: String = "+91 98421 55678",
    val defaultLocation: String = "Pollachi New Bus Stand",
    val email: String = "karthik.pollachi@example.com",
    val preferredLanguage: String = "Tamil / தமிழ்"
)

data class DriverProfile(
    val id: String,
    val name: String = "JOHNS KHAN",
    val phone: String = "+91 90800 40503",
    val rating: Float = 4.95f,
    val totalTrips: Int = 2450,
    val vehicleType: RideType = RideType.AUTO_STANDARD,
    val vehiclePlate: String = "TN 41 AB 4821",
    val vehicleModel: String = "Bajaj RE Auto (Pollachi Town)",
    val insuranceValidity: String = "31 Dec 2027",
    val licenseNumber: String = "TN41 20180004921",
    val rcNumber: String = "RC-TN41-89302-K",
    val rcDocumentAttached: Boolean = true,
    val insuranceDocumentAttached: Boolean = true,
    val licenseDocumentAttached: Boolean = true,
    val isDocumentsVerified: Boolean = true,
    val driverPhotoUrl: String = "",
    val standLocation: String = "Pollachi Town Auto Stand",
    val x: Float = 240f,
    val y: Float = 440f,
    val heading: Float = 45f,
    val isOnline: Boolean = true
)

data class DriverVehicleRegistration(
    val driverName: String = "JOHNS KHAN",
    val phone: String = "90800 40503",
    val vehicleType: RideType = RideType.AUTO_STANDARD,
    val vehiclePlate: String = "TN 41 AB 4821",
    val vehicleModel: String = "Bajaj RE Compact",
    val rcNumber: String = "RC-TN41-89302-K",
    val rcDocumentAttached: Boolean = true,
    val insuranceValidity: String = "31 Dec 2027",
    val insuranceDocumentAttached: Boolean = true,
    val licenseNumber: String = "TN41 20180004921",
    val licenseDocumentAttached: Boolean = true,
    val standLocation: String = "Pollachi New Bus Stand"
)

data class TripRating(
    val stars: Int,
    val tags: List<String>,
    val tipAmount: Double,
    val feedback: String,
    val submittedAt: Long = System.currentTimeMillis()
)

data class RideBooking(
    val id: String,
    val pickup: MapLocation,
    val dropoff: MapLocation,
    val rideType: RideType,
    val distanceKm: Float,
    val estimatedMinutes: Int,
    val fareAmount: Double,
    val discountAmount: Double = 0.0,
    val finalFare: Double,
    val safetyOtp: String,
    val status: RideStatus,
    val driver: DriverProfile? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.UPI,
    val paymentStatus: String = "PENDING",
    val rating: TripRating? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val progress: Float = 0f
)

data class DriverStats(
    val todayEarnings: Double = 1450.0,
    val tripsCompleted: Int = 11,
    val onlineHours: Double = 6.5,
    val acceptanceRate: Int = 96,
    val rating: Float = 4.95f
)

data class DriverJobRequest(
    val id: String,
    val booking: RideBooking,
    val passengerName: String,
    val pickupDistanceKm: Float,
    val timeoutSeconds: Int = 15
)
