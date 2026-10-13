# KaiPay

KaiPay is a local-only e-wallet simulation built with Kotlin, Jetpack Compose, Room, and coroutines. It is for learning Android app architecture and transaction validation.

> DEMO ONLY: no real money, bank, payment gateway, or network transfer is involved. Demo balances are stored locally on the device.

## Features
- Three seeded demo accounts (Kai, Alex, and Naya)
- Local transfer between demo accounts
- Sender/receiver and amount validation
- Atomic balance updates and transaction history using a Room database transaction
- Compose UI with Indonesian Rupiah formatting
- Instrumented tests for successful and rejected transfers

## Requirements
- Android Studio with Android SDK 35
- JDK 17
- Internet access for Gradle dependency resolution

Open this repository as an Android Studio project, allow Gradle sync, then run the app configuration on an emulator or Android device. Gradle wrapper binaries are not included yet.

## Demo accounts
On first launch, the local database is seeded with:
- Kai Demo - Rp1.000.000
- Alex Demo - Rp750.000
- Naya Demo - Rp500.000

## Safety and scope
KaiPay does not request financial credentials and does not connect to any financial institution. Clearing app data resets the local demo database. This code is an educational prototype, not a production wallet.
