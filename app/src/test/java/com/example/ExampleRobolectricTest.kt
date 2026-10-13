package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PaymentMethod
import com.example.model.RideType
import com.example.repository.AutoGoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pollachi AutoGo", appName)
  }

  @Test
  fun `fare calculation is accurate`() {
    val repo = AutoGoRepository()
    val pickup = repo.presetLocations[0]
    val dropoff = repo.presetLocations[1]
    val distance = repo.calculateDistanceKm(pickup, dropoff)
    assertTrue(distance > 0f)

    val fareAuto = repo.calculateFare(RideType.AUTO_STANDARD, distance)
    assertTrue(fareAuto >= RideType.AUTO_STANDARD.baseFare)

    val fareCab = repo.calculateFare(RideType.CAB_MINI, distance)
    assertTrue(fareCab > fareAuto)
  }

  @Test
  fun `booking flow and driver otp verification`() {
    val repo = AutoGoRepository()
    repo.requestRide()
    val booking = repo.activeBooking.value
    assertNotNull(booking)
    assertEquals(4, booking!!.safetyOtp.length)

    // Verify OTP validation
    val correct = repo.verifyDriverOtp(booking.safetyOtp)
    assertTrue(correct)
  }

  @Test
  fun `verify driver Johns Khan contact details`() {
    val repo = AutoGoRepository()
    val driver = repo.nearbyDrivers.value.first { it.id == "drv_1" }
    assertEquals("JOHNS KHAN", driver.name)
    assertEquals("+91 90800 40503", driver.phone)
    assertEquals("TN 41 AB 4821", driver.vehiclePlate)
  }

  @Test
  fun `verify driver vehicle registration with vehicle type, plate, and insurance validity`() {
    val repo = AutoGoRepository()
    val reg = com.example.model.DriverVehicleRegistration(
      driverName = "JOHNS KHAN",
      phone = "90800 40503",
      vehicleType = RideType.AUTO_ELECTRIC,
      vehiclePlate = "TN 41 EV 8899",
      vehicleModel = "Mahindra Treo EV Auto",
      insuranceValidity = "15 Aug 2028",
      licenseNumber = "TN41-2020-009182",
      standLocation = "Pollachi New Bus Stand"
    )
    repo.registerDriverVehicle(reg)

    val updatedProfile = repo.currentDriverProfile.value
    assertNotNull(updatedProfile)
    assertEquals("TN 41 EV 8899", updatedProfile!!.vehiclePlate)
    assertEquals(RideType.AUTO_ELECTRIC, updatedProfile.vehicleType)
    assertEquals("15 Aug 2028", updatedProfile.insuranceValidity)
    assertEquals("Pollachi New Bus Stand", updatedProfile.standLocation)
  }
}
