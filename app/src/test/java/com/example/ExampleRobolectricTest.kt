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

  @Test
  fun `verify split auth screen rider credential collection and login`() {
    val viewModel = com.example.viewmodel.AutoGoViewModel()
    // Defaults to authenticated Rider mode for instant app usability
    assertEquals(true, viewModel.isAuthenticated.value)
    assertEquals(com.example.model.AppMode.PASSENGER, viewModel.currentMode.value)

    // User can switch account / logout to open split auth screen
    viewModel.logout()
    assertEquals(false, viewModel.isAuthenticated.value)

    viewModel.loginAsRider(
      name = "Suresh Raina",
      phone = "98421 99887",
      pickupLocation = "Mahalingapuram Roundana"
    )

    assertEquals(true, viewModel.isAuthenticated.value)
    assertEquals(com.example.model.AppMode.PASSENGER, viewModel.currentMode.value)
    val rider = viewModel.currentRiderProfile.value
    assertEquals("Suresh Raina", rider.name)
    assertEquals("98421 99887", rider.phone)

    // Logout returns to split screen
    viewModel.logout()
    assertEquals(false, viewModel.isAuthenticated.value)
  }

  @Test
  fun `verify split auth screen driver registration with documents`() {
    val viewModel = com.example.viewmodel.AutoGoViewModel()
    val driverReg = com.example.model.DriverVehicleRegistration(
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

    viewModel.loginOrRegisterAsDriver(driverReg)

    assertEquals(true, viewModel.isAuthenticated.value)
    assertEquals(com.example.model.AppMode.DRIVER_CONSOLE, viewModel.currentMode.value)
    assertEquals(true, viewModel.isDriverDocumentsComplete.value)

    val profile = viewModel.currentDriverProfile.value
    assertNotNull(profile)
    assertEquals("JOHNS KHAN", profile!!.name)
    assertEquals("+91 90800 40503", profile.phone)
    assertEquals("TN 41 AB 4821", profile.vehiclePlate)
    assertEquals(true, profile.isDocumentsVerified)
    assertEquals(true, profile.rcDocumentAttached)
    assertEquals(true, profile.insuranceDocumentAttached)
  }

  @Test
  fun `verify view switcher toggling between Rider and Driver using shared ViewModel state`() {
    val viewModel = com.example.viewmodel.AutoGoViewModel()
    // Login as Rider first
    viewModel.quickDemoRider()
    assertEquals(com.example.model.AppMode.PASSENGER, viewModel.currentMode.value)

    // Switch to Driver view using shared ViewModel state
    viewModel.switchMode(com.example.model.AppMode.DRIVER_CONSOLE)
    assertEquals(com.example.model.AppMode.DRIVER_CONSOLE, viewModel.currentMode.value)

    // Toggle back to Rider view using shared ViewModel toggleMode
    viewModel.toggleMode()
    assertEquals(com.example.model.AppMode.PASSENGER, viewModel.currentMode.value)

    // Toggle once more to Driver view
    viewModel.toggleMode()
    assertEquals(com.example.model.AppMode.DRIVER_CONSOLE, viewModel.currentMode.value)

    // Verify repository and booking state are shared across both views
    assertNotNull(viewModel.nearbyDrivers.value)
    assertTrue(viewModel.nearbyDrivers.value.isNotEmpty())
  }
}
