# Rider Offer Comparison App (MVP)

An independent, read-only Android application designed for delivery and ride-hailing partners (Swiggy, Zomato, Rapido, Ola, Shadowfax) to estimate real net hourly earnings and compare offers in real-time.

---

## Project Overview & Objectives

When an offer pops up in a supported platform app, this app reads it via Android's **AccessibilityService** without ever tapping, accepting, rejecting, or interacting with the platform app. Before the driver accepts or declines, it calculates net earnings, ₹/km, ₹/hour, and provides a **Good / OK / Poor** verdict based on personal targets.

### Core Principles
- **Read-Only & Unofficial:** Zero automated taps, zero gesture simulation.
- **One App at a Time:** Compares offers inside the platform app on screen; never combines offers across different apps.
- **Fail Closed:** If any required field cannot be parsed with confidence, the offer is rejected rather than showing incorrect data.
- **Glanceable:** Designed for riders on the move.

---

## Strict Privacy & Data Minimization (Section 6 & 7 Compliance)

- **Absolute Anti-Screenshot & Screen Recording:** Actively blocks screenshots and screen recordings across all activities using `window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)` and `canTakeScreenshot="false"` in the Accessibility service configuration.
- **Zero GPS / Location Permissions:** The app requests zero location permissions (`ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION`). It exclusively reads numeric distance (km) and ETA minutes from screen text.
- **Active Customer Data Filtering:** The parser and accessibility service explicitly ignore and drop all customer identity, phone numbers, exact addresses, restaurant/store names, and payment modes.
- **Local-Only Storage:** Offer data stays strictly in local memory or local private storage (Room database for recent history, DataStore for preferences) and is never transmitted over a network.

---

## Earnings Engine (Section 8)

Every offer is evaluated using the following calculation model:

$$\text{Net Earning} = (\text{Earning Shown} + \text{Incentive Shown}) \times (1 - c) - (d_{\text{pickup}} + d_{\text{trip}}) \times k$$

$$\text{Net ₹/hour} = \frac{\text{Net Earning}}{t_{\text{pickup}} + t_{\text{trip}}} \times 60$$

- **$k$**: Fuel and running cost per km (set per app and vehicle).
- **$c$**: Platform commission share (0 for subscription plans).
- **$t$**: Time in minutes (using ETA / trip time when shown, else distance $\div$ average speed).

### Verdict Rules
- **Good:** Net ₹/hour at or above target, and pickup distance within max pickup km limit.
- **Poor:** Net ₹/hour below 70% of target, or pickup distance exceeding max limit.
- **OK:** Everything else.

---

## The One-Page Plan (Milestones & Risks)

### Milestones
1. **Milestone 1:** Background Service Optimization for battery efficiency (<5% drain over 4 hours of active session) and adding the daily summary per app (offers seen, share rated Good, average estimated ₹/hour).

### Key Risks & Mitigations
- **UI DOM Obfuscation / Platform Updates:** Ride platforms frequently update their user interfaces and view hierarchies, which can break screen parsing.
  - *Mitigation:* Version pinning, auto-disable on unknown versions, and continuous parser maintenance.
- **Platform Policy & Driver Safety:** Ensuring strict read-only compliance, prominent privacy disclosures, and an instantaneous master off switch.
- **Rider Distraction:** Ensuring cards are glanceable and require zero input while riding.

---

## Technical Stack & Architecture

- **Language:** Kotlin 2.2+
- **UI Framework:** Jetpack Compose & Material 3 with adaptive layouts and bilingual support (English & हिंदी).
- **Architecture:** MVVM, Coroutines & Flow.
- **Database:** Room (Offer history limited to last 20 offers per app, purged after 24h).
- **Preferences:** Jetpack DataStore (Per-app calculation settings and onboarding consent).
- **Accessibility:** `AccessibilityService` with strict package filtering (`com.rapido.passenger`, `com.swiggy.deliverypartner`, `com.application.zomato`, `com.olacabs.customer`, `com.shadowfax.rider`).
