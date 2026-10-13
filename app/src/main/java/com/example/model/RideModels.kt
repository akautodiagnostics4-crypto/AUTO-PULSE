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
        displayName = "Auto Standard",
        subtitle = "Classic 3-wheeler, swift in traffic",
        baseFare = 30.0,
        perKmRate = 15.0,
        capacity = 3,
        isElectric = false,
        etaMinutes = 2
    ),
    AUTO_ELECTRIC(
        displayName = "Auto Electric",
        subtitle = "Zero emissions, smooth & silent",
        baseFare = 35.0,
        perKmRate = 14.0,
        capacity = 3,
        isElectric = true,
        etaMinutes = 4
    ),
    CAB_MINI(
        displayName = "Cab Mini AC",
        subtitle = "AC hatchback comfort with boot space",
        baseFare = 60.0,
        perKmRate = 22.0,
        capacity = 4,
        isElectric = false,
        etaMinutes = 5
    ),
    BIKE_TAXI(
        displayName = "Bike Taxi",
        subtitle = "Quickest solo commute, beats traffic",
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
    UPI(title = "UPI / QR", iconName = "qr_code"),
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

data class DriverProfile(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Float,
    val totalTrips: Int,
    val vehicleType: RideType,
    val vehiclePlate: String,
    val vehicleModel: String,
    val insuranceValidity: String = "31 Dec 2027",
    val licenseNumber: String = "TN41 20180004921",
    val standLocation: String = "Pollachi Town Auto Stand",
    val x: Float,
    val y: Float,
    val heading: Float = 0f,
    val isOnline: Boolean = true
)

data class DriverVehicleRegistration(
    val driverName: String,
    val phone: String,
    val vehicleType: RideType,
    val vehiclePlate: String,
    val vehicleModel: String,
    val insuranceValidity: String,
    val licenseNumber: String = "",
    val standLocation: String = "Pollachi Town Auto Stand"
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
    val paymentStatus: String = "PENDING", // PENDING, PAID
    val rating: TripRating? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val progress: Float = 0f // 0f to 1f along path
)

data class DriverStats(
    val todayEarnings: Double = 1450.0,
    val tripsCompleted: Int = 11,
    val onlineHours: Double = 6.5,
    val acceptanceRate: Int = 96,
    val rating: Float = 4.91f
)

data class DriverJobRequest(
    val id: String,
    val booking: RideBooking,
    val passengerName: String,
    val pickupDistanceKm: Float,
    val timeoutSeconds: Int = 15
)
