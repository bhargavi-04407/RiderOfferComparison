# Project Plan

RiderWise — Independent, unofficial earnings intelligence assistant for ride-hailing, food-delivery, parcel-delivery, and logistics partners in India.

Key Features & Scope:
1. AccessibilityService integration: Detect offer screens on supported third-party apps (e.g. Rapido, Shadowfax, Ola, etc.) after explicit user consent. Extract only allowlisted fields (earning, incentive, pickup distance, trip distance, pickup ETA, trip time, order ID). Strictly NO customer personal data, addresses, photos, or chat.
2. Earnings & Verdict Engine:
   - Calculate Net Earnings = (Earning + Incentive) * (1 - Commission%) - (Pickup Dist + Trip Dist) * RunningCost/km.
   - Calculate Net ₹/hour and Net ₹/km.
   - Verdicts: GOOD (Net ₹/hour >= target AND pickup <= max_pickup), POOR (Net ₹/hour < 70% target OR pickup > max_pickup), OK (otherwise).
   - Same-platform offer ranking & recent 20-offer history comparison (<24h retention).
3. Live Floating Offer Assistant Overlay (WindowManager): Draggable, non-blocking, collapsible, dismissible. NO automated taps or platform control simulations.
4. UI/UX (Jetpack Compose, Material 3, Deep Navy & Teal theme, dark/light theme, high contrast):
   - Splash / Initialization
   - Welcome & Language selection (English, Hindi)
   - Phone Login & OTP Verification (with real backend interface & dev mock)
   - Prominent Disclosure & Consent screen
   - Platform Selection & Toggles
   - Vehicle & Earnings parameters setup
   - Main Dashboard with global monitoring toggle & today's summary
   - Live Floating Offer Overlay
   - Earnings Calculation Details screen
   - Offer History (<24h, max 20 offers per platform, zero customer details)
   - Insights & Daily Summary (Optional, <90d)
   - Settings with Master Stop switch and Quick Settings tile / Notification fallback
   - Permission & Compatibility Centre (Handling Android 13+ restricted settings)
   - Privacy & Data Management Centre (Account deletion flow)
5. Security & Privacy Safeguards: Strict allowlist data models, room local storage, network traffic restricted to authentication host only.

Tech Stack:
- Kotlin, Jetpack Compose, Material 3
- MVVM, Coroutines, Flow
- Hilt DI, Room DB, DataStore
- Retrofit/OkHttp (Auth only)
- Android AccessibilityService & WindowManager Overlay
- JUnit, Compose UI Tests
- Min SDK 29 (Android 10), Target SDK 34+

## Project Brief

# RiderWise — Project Brief (MVP)

## Features
1. **Accessibility-Based Offer Screen Detection**: Detects supported third-party app offer screens (e.g., Rapido, Shadowfax, Ola) using an AccessibilityService to extract allowlisted trip metrics (earning, incentive, pickup/trip distances, ETA, order ID) without touching any personal customer data.
2. **Real-Time Earnings & Verdict Engine**: Automatically calculates net earnings, net ₹/hour, and net ₹/km against vehicle running costs and commissions, displaying an instant verdict (GOOD, OK, or POOR).
3. **Live Floating Offer Overlay**: Non-blocking, draggable, collapsible WindowManager overlay that presents offer metrics, verdicts, and recent offer history directly on top of third-party delivery and ride-hailing apps.
4. **Vehicle & Earnings Parameter Configuration**: Enables drivers to customize key earnings parameters, including running cost per kilometer, target hourly earnings, maximum pickup distance, and commission rates.
5. **Privacy & Permission Control Center**: Handles explicit disclosure and consent onboarding, master monitoring toggles, quick settings tiles, and step-by-step resolution for Android 13+ restricted settings.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3 (Deep Navy & Teal high-contrast theme)
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Architecture & Asynchronous Framework**: MVVM, Kotlin Coroutines, Flow
- **Dependency Injection**: Hilt
- **Android Services**: Android `AccessibilityService` (offer screen parsing) and `WindowManager` (floating overlay)
- **Data Persistence**: Room Database and DataStore (for local parameter storage and <24h offer history retention)
- **Networking**: Retrofit and OkHttp (strictly for authentication API communication)

## Implementation Steps

### Task_1_CoreDataAndEngine: Set up core data models, Room database, DataStore repository for parameters, Hilt DI, and the net earnings & verdict calculation engine.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Project builds successfully
  - DataStore and Room persistence implemented
  - Verdict calculation engine correctly computes net earnings, ₹/hr, ₹/km, and verdict
- **StartTime:** 2026-10-15 14:12:27 IST

### Task_2_AccessibilityServiceParsing: Implement AccessibilityService for ride-hailing/delivery offer screen detection and allowlisted metric extraction.
- **Status:** PENDING
- **Acceptance Criteria:**
  - AccessibilityService registered and configured in AndroidManifest.xml
  - Metric extraction logic parses earning, pickup/trip distance, ETA without capturing PII
  - Extracted offers are emitted via Flow to the local database

### Task_3_FloatingOverlayUI: Implement WindowManager draggable and collapsible overlay displaying real-time offer metrics and verdict badges.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Floating WindowManager overlay renders over target apps
  - Overlay displays real-time offer details and GOOD/OK/POOR verdict badge
  - Overlay supports dragging, collapsing, and system overlay permissions

### Task_4_ComposeUIAndPrivacyCenter: Build Jetpack Compose UI including Material 3 theme, Driver Parameter Settings, Onboarding, Privacy & Permission Control Center, and Offer History.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Compose UI implemented with Deep Navy & Teal theme
  - Driver Parameter Configuration screen allows updating vehicle costs and targets
  - Privacy & Permission Control Center handles disclosures, restricted settings guidance, and master toggles

### Task_5_RunAndVerify: Build, run, and verify the entire RiderWise application. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** PENDING
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - Application verified for stability and core feature completion

