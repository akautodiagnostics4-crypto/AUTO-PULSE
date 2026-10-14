# Pollachi AutoGo: App Diagnostic, Startup Fix & View Switcher Plan

Comprehensive diagnostic and implementation plan to address the "app not working" report by ensuring default startup lands directly on the functional, interactive Rider booking & live map screen, providing an instant one-tap view switcher to Driver Console, and harmonizing GitHub Actions build workflows with the native Android Gradle project.

***

## User Review & Critical Decisions

> [!IMPORTANT]
> The primary reason the app may appear "not working" is:
> 1. **Initial Startup State**: The app currently defaults to `_isAuthenticated = false`, trapping the user on a credential entry form on launch instead of displaying the live map, vehicle telemetry, and booking interface immediately.
> 2. **GitHub Actions Scaffolding Conflict**: A newly added `.github/workflows/blank.yml` attempts to dynamically scaffold an empty, separate `com.example.autopulse` app via inline bash scripts instead of building the existing, fully working `Pollachi AutoGo` codebase in the repository.
> 
> **Proposed Fix Strategy**:
> - **Immediate Active Screen on Launch**: Default `_isAuthenticated = true` with a pre-configured Rider session so launching the app immediately displays the interactive Pollachi map, moving autos, fare cards, and the Rider/Driver view switcher.
> - **Instant View Switcher**: Keep the dedicated `RiderDriverViewSwitcher` visible at all times in the top bar to toggle between Rider and Driver views without reloading or recreating activities.
> - **Seamless Account & Auth Access**: Allow switching between Rider, Driver, and Split Registration screens at any time via the profile/logout button in the top bar.
> - **Standardized GitHub Actions Workflow**: Align `.github/workflows/blank.yml` to build the actual Android project directly (`gradle assembleDebug`) instead of generating throwaway placeholder files.

***

## 1. Overview & Core Concept

- **What It Does**: Pollachi AutoGo is an on-demand local ride-hailing and auto dispatch application tailored for Pollachi, Tamil Nadu. It provides a unified single-app experience for both passengers (Riders) and auto rickshaw drivers (Drivers).
  - **Rider Experience**: Live interactive vector map with ambient nearby autos, preset Pollachi transit landmarks (New Bus Stand, Railway Station, Daily Market, Mahalingapuram), upfront fare estimation across vehicle types (Pollachi Auto, Green EV, Town Cab, Bike Taxi), booking ring alerts, driver photo & plate tracking, and trip safety OTP verification.
  - **Driver Console**: Duty toggle (Online/Offline), incoming job alerts with countdown and ring sound, passenger pickup navigation, OTP validation, and daily earnings dashboard with vehicle document verification (RC, Insurance, License for JOHNS KHAN).
- **Target Audience**: Pollachi commuters, college students, shoppers, and local auto drivers based at Pollachi auto stands.
- **Key Value**: Immediate, zero-friction startup showing real vehicles and transparent local fares, with fluid role switching between Rider and Driver.

***

## 2. User Experience & Visual Design

### Key User Flows

```
┌────────────────────────────────────────────────────────────────────────┐
│                        APP LAUNCH (DEFAULT)                           │
│              Direct Entry: Interactive Rider Map View                  │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
         ┌─────────────────────────┴─────────────────────────┐
         ▼                                                   ▼
┌────────────────────────────────┐         ┌────────────────────────────────┐
│       RIDER SCREEN (HOME)      │         │     DRIVER CONSOLE SCREEN      │
│  - Live Interactive Map        │◄───────►│  - Duty Switch (Online/Offline)│
│  - Moving Autos & Route Lines  │ Toggle  │  - Incoming Job Ring Alert     │
│  - Fare Card (Auto, EV, Cab)   │ Switcher│  - Customer Pickup Telemetry   │
│  - Booking & OTP Generation    │         │  - Trip OTP Verification       │
└────────────────────────────────┘         └────────────────────────────────┘
                 │                                           │
                 └─────────────────┬─────────────────────────┘
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                 OPTIONAL: SPLIT LOGIN & REGISTRATION                   │
│   Accessible via "Account / Role" icon in Top App Bar at any time     │
│   - Rider Credentials Entry                                            │
│   - Driver Vehicle & Document Registration (RC, Insurance, License)   │
└────────────────────────────────────────────────────────────────────────┘
```

### Visual Identity & Theme
- **Color Tokens**:
  - **Auto Rickshaw Amber** (`#FFB300` / `#F57F17`): Primary brand color representing the iconic Indian auto rickshaw.
  - **Eco Green** (`#00A86B`): Highlights driver online status, EV fleet options, and confirmed bookings.
  - **Midnight Slate** (`#0F141C` / `#182230`): Rich dark theme background providing strong contrast for map tiles and telemetry.
- **Typography**: Clean Material 3 typography with Tamil and English bilingual subtitles ("பொள்ளாச்சி மக்களின் உள்ளூர் ஆட்டோ").
- **Motion & Transitions**:
  - Seamless horizontal slide and fade transitions (`AnimatedContent`) between Rider and Driver screens using the shared ViewModel state.
  - Pulsing radar wave animation during driver matching and ring alerts.

***

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Default to Authenticated Rider Screen on App Open**
  - *Chosen Approach*: Initialize `_isAuthenticated = true` in `AutoGoViewModel` with pre-filled default demo credentials (Karthik Subramanian, Pollachi New Bus Stand).
  - *Why*: Prevents new users from seeing an empty login hurdle or wondering why the map isn't showing. Users immediately experience the working app features.
  - *Alternative Considered*: Showing split login every time app opens. Rejected because it led to the perception that the app is stuck or "not working".

- **Decision 2: Single-Activity In-Memory View Switcher**
  - *Chosen Approach*: Shared `AutoGoViewModel` drives `currentMode` (`PASSENGER` vs `DRIVER_CONSOLE`) rendered through Jetpack Compose `AnimatedContent` inside `MainActivity`.
  - *Why*: Eliminates Android Activity lifecycle overhead, maintains state (active ride, driver duty, wallet, coordinates) across switches, and fulfills the explicit requirement of not creating new activities.

- **Decision 3: Harmonize GitHub Actions Workflow**
  - *Chosen Approach*: Replace the ad-hoc inline bash script in `.github/workflows/blank.yml` with a clean, standard Gradle build step that builds the actual project (`gradle :app:assembleDebug`).
  - *Why*: Ensures GitHub CI/CD exports the actual, functional Pollachi AutoGo APK rather than failing or building a hollow template.

***

## 4. Technical Architecture & Data Strategy

### Component & State Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│                    (Single Android ComponentActivity)                  │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         AutoGoViewModel (Shared)                       │
│  - currentMode: StateFlow<AppMode> (PASSENGER / DRIVER_CONSOLE)        │
│  - isAuthenticated: StateFlow<Boolean> (defaults to true)             │
│  - activeBooking: StateFlow<RideBooking?>                              │
│  - nearbyDrivers: StateFlow<List<DriverProfile>>                       │
│  - isDriverOnline: StateFlow<Boolean>                                  │
│  - currentDriverProfile: StateFlow<DriverProfile?> (Johns Khan)        │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
         ┌─────────────────────────┴─────────────────────────┐
         ▼                                                   ▼
┌────────────────────────────────┐         ┌────────────────────────────────┐
│      PassengerHomeScreen       │         │      DriverConsoleScreen       │
│  - LiveInteractiveMap          │         │  - DriverDutyCard              │
│  - SearchLocationSheet         │         │  - IncomingJobAlertCard        │
│  - RideSelectionSheet          │         │  - OtpVerificationDialog       │
│  - ActiveRideTrackingSheet     │         │  - VehicleRegistrationSheet    │
└────────────────────────────────┘         └────────────────────────────────┘
```

### Proposed Changes:
1. **`AutoGoViewModel.kt`**: Set `_isAuthenticated = MutableStateFlow(true)` so the app immediately displays the live interactive map and booking UI.
2. **`MainActivity.kt`**: Confirm `AnimatedContent` view switcher responds immediately to `currentMode`.
3. **`.github/workflows/blank.yml`**: Update workflow to build the repository's native Gradle project directly for APK artifacts.
4. **Verification**: Run `compile_applet` and `gradle :app:testDebugUnitTest` to guarantee all tests pass.
