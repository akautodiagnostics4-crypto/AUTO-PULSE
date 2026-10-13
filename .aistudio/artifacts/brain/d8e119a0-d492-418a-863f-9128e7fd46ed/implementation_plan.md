# AutoGo: Local Auto & On-Demand Ride Hailing App

An on-demand local urban transport Android app featuring real-time driver tracking, a mixed fleet (Auto Rickshaw, Electric Auto, Cab Mini, Bike Taxi), seamless multi-method payments (UPI, Card, Wallet, Cash), trip OTP security, a user rating system, and a dedicated Driver Console view.

***

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions incorporate your selections from Phase 1:
> - **Fleet Coverage**: Full support for mixed urban mobility options: **Local Auto Rickshaws** (Standard & Eco Electric), **Cabs / Mini Taxis**, and **Bike Taxis**, each with distinct pricing models, capacities, and ETAs.
> - **Payment Spectrum**: Flexible checkout supporting **UPI (Google Pay, PhonePe, QR)**, **Credit/Debit Cards**, **In-App Digital Wallet**, and **Cash to Driver** upon arrival.
> - **Dual Operational Perspectives**: Seamless dual-mode architecture featuring a rich **Passenger Booking Experience** alongside a full **Driver Partner Console** (dispatch radar, incoming request alerts with acceptance timer, OTP verification, live navigation simulation, and daily earnings dashboard).
> - **Live Interactive Map Engine**: A high-performance, stylized Compose Canvas vector map featuring fluid vehicle movement, route polylines, pickup/drop-off pins, neighborhood grid landmarks, and real-time proximity calculations.

***

## 1. Overview & Core Concept

- **What It Does**: AutoGo connects city commuters with local auto-rickshaw and taxi drivers instantly. Riders can select pickup and drop-off points on an interactive map, view upfront transparent fares across vehicle categories, request a ride with instant driver matching, track the vehicle in real time with animated route telemetry, pay safely via preferred digital or cash channels, and rate the trip. Drivers can switch to the Driver Console to manage ride requests, accept bookings, verify rider OTPs, and track daily earnings.
- **Target Audience / Persona**: Daily city commuters, students, office workers needing quick auto/cab transport, and local auto & cab drivers seeking steady on-demand trip bookings.
- **Key Value**: Eliminates bargaining and street hailing uncertainty with transparent per-km fares, live driver arrival countdowns, passenger safety features (SOS, OTP verification), and flexible payment choices.

***

## 2. User Experience & Visual Design

### Key User Flows

```
[PASSENGER FLOW]
Browse Map & Nearby Autos ─► Set Pickup/Drop-off ─► Compare Fleet & Fares (Auto/Cab/Bike) 
  ─► Select Payment Method ─► Request Ride ─► Driver Assigned & Live Tracking (ETA + Route)
  ─► OTP Verification & In-Ride Telemetry ─► Arrive & Instant Receipt ─► 5-Star Rating & Driver Tip

[DRIVER CONSOLE FLOW]
Go Online (Radar Active) ─► Incoming Trip Dispatch (15s Timer) ─► Accept Booking 
  ─► Navigate to Pickup ─► Input Passenger OTP ─► Start Trip & Route Tracking 
  ─► Complete Ride ─► Collect Fare ─► View Daily Earnings & Ratings
```

1. **Explore & Destination Selection**:
   - Dynamic map view showcasing active nearby autos and cabs moving in the vicinity.
   - Quick search bar with popular shortcuts ("Metro Station", "Tech Park", "City Center", "Home", "Work").
   - Pickup location auto-detection with interactive pin adjustment.

2. **Fleet Comparison & Fare Estimation**:
   - Tiered ride selector cards:
     - **Auto Standard**: Traditional 3-seater auto, fastest in heavy traffic, budget friendly.
     - **Auto Electric**: Eco-friendly green auto, smooth & quiet ride.
     - **Cab Mini / Sedan**: AC comfort, spacious luggage capacity.
     - **Bike Taxi**: Single passenger express, ideal for solo commuters.
   - Transparent price calculation showing base fare, distance rate, estimated travel time, and discount voucher support.

3. **Live Booking & Real-Time Tracking**:
   - Matching screen showing pulsating radar search for nearby drivers.
   - Driver confirmation card displaying driver name, photo avatar, rating badge, vehicle model, plate number, and one-tap call/message action.
   - 4-digit Ride Safety PIN / OTP for trip start verification.
   - Real-time animated vehicle progress along the plotted route with dynamic ETA countdown.
   - Emergency SOS floating button with instant share-trip and helpline access.

4. **Payments & Checkout**:
   - One-tap payment method switcher:
     - **UPI**: Direct simulation of UPI payment apps (GPay, PhonePe, Paytm) and dynamic QR code generation.
     - **Card**: Saved cards with secure CVV authorization.
     - **AutoGo Wallet**: Stored balance with instant debit and top-up option.
     - **Cash**: Direct cash payment to driver marked upon arrival.
   - Itemized digital invoice with trip fare, taxes, and applied promos.

5. **Driver Rating & Review System**:
   - Post-trip modal with interactive 5-star rating bar.
   - Quick compliment chips: *"Polite Driver"*, *"Clean Auto"*, *"Smooth Driving"*, *"Quickest Route"*.
   - Optional tip selector ($10 / $20 / $50 / Custom).
   - Written review feedback field saved directly to driver records.

6. **Driver Partner Console**:
   - Online/Offline toggle with active duty status pill.
   - Incoming request modal with 15-second countdown circular timer, passenger distance, pickup address, and estimated payout.
   - Step-by-step trip progression: "At Pickup" -> "Enter OTP" -> "Trip in Progress" -> "Trip Completed".
   - Shift summary dashboard: Today's Earnings, Trips Completed, Acceptance Rate, and Driver Rating.

### Visual Identity & Theme

- **Aesthetic Direction**: Urban Mobility Neo-Modern — sleek dark asphalt surfaces paired with vibrant **Electric Amber / Auto Yellow (`#FFB300` / `#FFC107`)** and **Emerald Green (`#10B981`)** accents inspired by classic auto-rickshaw styling and urban transit signages.
- **Color Palette**:
  - `Primary`: Auto Saffron / Amber Gold (`#FFA000`)
  - `PrimaryContainer`: Warm Amber Glow (`#FFE082`)
  - `Secondary`: Emerald Green (`#00897B`) for eco & confirm states
  - `Surface`: Deep Midnight Slate (`#121820`) / Crisp Off-White (`#F8F9FA` in light theme)
  - `SurfaceVariant`: Elevated card charcoal (`#1E2632`)
  - `Accent`: Traffic Alert Coral (`#FF5252`) for SOS & cancellations
- **Typography & Hierarchy**:
  - Clean, legible geometric fonts with high-contrast numerals for ETAs, fares, and vehicle plates.
  - Generous spacing for quick glanceability while on the move.
- **Component Styling & Motion**:
  - Custom vector canvas with smooth vehicle rotation and interpolation along waypoints.
  - Floating bottom sheets with spring physics for fleet selection and driver telemetry.
  - Pulsating radar waves during driver search and dispatch.

***

## 3. Key Product Decisions & Trade-Offs

- **Map Engine Strategy**:
  - *Chosen Approach*: Rich custom Jetpack Compose Canvas vector map engine with street grid rendering, traffic congestion overlays, dynamic landmark icons, and smooth hardware-accelerated vehicle interpolation.
  - *Why*: Delivers an immediate, zero-API-key-dependent, 60fps smooth simulation that works consistently out of the box without requiring external billing setup or Google Maps API key configuration, while preserving full interactive touch controls (pan, zoom, pin-drag).
  - *Alternative Considered*: Standard Google Maps SDK; discarded because missing API keys in developer sandboxes cause blank gray screens and build crashes.
- **Role Switching UX**:
  - *Chosen Approach*: Prominent top-app-bar mode switch button ("Passenger App" vs. "Driver Console") that allows seamless toggling between passenger booking and driver job acceptance in real time.
  - *Why*: Gives users and testers the complete end-to-end transportation ecosystem in one unified app without needing two separate APK installations.
- **Data Persistence**:
  - *Chosen Approach*: Reactive StateFlow repository combined with Room database persistence for saved trips, favorite places, active wallet transactions, and driver ratings.
  - *Why*: Ensures trips, user profile, wallet balance, and driver reviews persist reliably across app restarts.

***

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              AutoGo App                                │
└────────────────────────────────────────────────────────────────────────┘
                                    │
           ┌────────────────────────┴────────────────────────┐
           ▼                                                 ▼
┌───────────────────────┐                         ┌──────────────────────┐
│    Passenger Mode     │                         │    Driver Console    │
│  - Interactive Map    │                         │  - Duty Radar Status │
│  - Ride Category Tray │                         │  - Dispatch Incoming │
│  - Ride Tracking Sheet│                         │  - OTP Verification  │
│  - Payment Sheet      │                         │  - Trip Nav Progress │
│  - Rating & Tip Modal │                         │  - Daily Earnings    │
└───────────────────────┘                         └──────────────────────┘
           │                                                 │
           └────────────────────────┬────────────────────────┘
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        AutoGoViewModel (MVI/MVVM)                      │
│   - Screen State & Active Ride State Flow                              │
│   - Fleet & Pricing Calculator (Autos, Cabs, Bikes)                    │
│   - Real-time Driver Tracker & Route Telemetry Simulator              │
│   - Payment Processing & In-App Wallet Engine                          │
│   - Rating Submission & Driver Reputation Calculator                  │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                    Repository & Persistence Layer                      │
│   - RideRepository (Active & Historic Rides, Saved Locations)          │
│   - DriverRepository (Driver Pool, Real-time Positions, Status)       │
│   - PaymentRepository (Wallet Balance, Card Stubs, UPI Intents)       │
│   - Room Database (RidesDao, PlacesDao, TransactionsDao)               │
└────────────────────────────────────────────────────────────────────────┘
```

### Core Entities & Data Models

- **`RideBooking`**: `id`, `pickupName`, `pickupLatLng`, `dropoffName`, `dropoffLatLng`, `rideType` (`AUTO_STANDARD`, `AUTO_ELECTRIC`, `CAB_MINI`, `BIKE_TAXI`), `fareAmount`, `distanceKm`, `status` (`SEARCHING`, `CONFIRMED`, `ARRIVING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`), `safetyOtp`, `driverId`, `paymentMethod`, `paymentStatus`, `timestamp`.
- **`DriverProfile`**: `id`, `name`, `rating`, `totalTrips`, `vehicleType`, `vehiclePlate`, `vehicleModel`, `avatarRes`, `phone`, `currentLatLng`, `isOnline`.
- **`RideTypeOption`**: `type`, `displayName`, `tagline`, `baseFare`, `ratePerKm`, `estimatedMinutes`, `capacity`, `icon`.
- **`PaymentMethod`**: `UPI`, `CARD`, `WALLET`, `CASH`.
- **`TripRating`**: `tripId`, `driverId`, `stars`, `tags`, `tipAmount`, `feedbackComment`.
- **`DriverEarnings`**: `todayTotal`, `tripsCount`, `onlineHours`, `acceptanceRate`.

### Key User Interactions & State Transitions

1. **Category Selection**: Updating selected vehicle type dynamically recalculates fare, estimated arrival time, and route route ETA.
2. **Ride Request & Matching**:
   - Triggers `SEARCHING` state with simulated dispatch to nearest available vehicle.
   - Within 2-3 seconds, matches a driver, sets `CONFIRMED`, generates a unique 4-digit OTP, and triggers realistic waypoint motion along the path.
3. **Trip Progress & Driver Console Sync**:
   - When switching to Driver Console, the driver sees the pending trip request, can accept/decline, enter passenger OTP to verify pickup, and advance trip status to completion.
4. **Checkout & Rating**:
   - Selecting payment method processes mock transaction, deducts wallet balance if selected, produces invoice, and launches the review & rating dialog.
