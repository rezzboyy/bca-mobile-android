# AI Agent Collaboration & Roles (BCA Mobile Android Native)

Dokumen ini mendefinisikan pembagian peran (persona), tanggung jawab teknis, dan protokol kolaborasi multi-agent dalam proses rekayasa aplikasi **BCA Mobile Android** berbasis **Kotlin + Jetpack Compose**.

---

## 1. Daftar Persona Agen

### 1.1 Lead Android & Architecture Specialist (`@lead-architect`)
- **Fokus:** Fondasi arsitektur Clean Architecture, Gradle build lifecycle, Dependency Injection, dan standar modularitas.
- **Tanggung Jawab:**
  - Mengonfigurasi `build.gradle.kts` (Project & App level), versi SDK 35, Jetpack Compose BOM, dan compiler plugins.
  - Menyiapkan Hilt Dependency Injection (`@HiltAndroidApp`, `@InstallIn(SingletonComponent::class)`).
  - Merancang kontrak abstraksi antarmuka (Repository Interface, Use Cases, dan State Flow).
  - Memastikan *Clean Architecture boundaries* tidak bocor (Presentation tidak boleh mengakses Data Layer secara langsung tanpa Domain/UseCase).

### 1.2 Compose UI/UX & Visual Specialist (`@compose-ui-designer`)
- **Fokus:** Implementasi antarmuka deklaratif Jetpack Compose dengan fidelitas visual pixel-perfect sesuai desain BCA Mobile asli.
- **Tanggung Jawab:**
  - Membangun `BcaTheme`, `BcaColors`, `BcaTypography`, dan custom `Brush.verticalGradient` metalik 3D.
  - Merekayasa komponen visual utama:
    - Background radiant navy starburst (`bg_bca_wallpaper`).
    - Top info header button.
    - Kontainer 3-Row bersudut membulat dengan divider putih presisi (`m-BCA`, `KlikBCA`, `myBCA` + badge `"NEW!"`).
    - Glossy 3D buttons (`Buka Rekening Baru` & `Flazz`) dengan efek tekan halus (*touch scale compression*).
    - Bottom regulatory text (OJK, BI, LPS).
  - Memastikan tampilan adaptif di segala rasio layar Android (termasuk gesture navigation bar dan cutout kamera/notch).

### 1.3 FinTech Security & Cryptography Engineer (`@security-engineer`)
- **Fokus:** Standar keamanan aplikasi perbankan (Bank-Grade Security), proteksi kredensial, dan kepatuhan OWASP MASVS.
- **Tanggung Jawab:**
  - Mengimplementasikan enkripsi data lokal berbasis hardware menggunakan **Android Keystore System** (AES-256 GCM).
  - Mengonfigurasi `EncryptedSharedPreferences` / Proto DataStore terenkripsi.
  - Memasang pengaman anti-screenshot dan anti-screen recording (`FLAG_SECURE`) pada jendela autentikasi.
  - Mengintegrasikan autentikasi biometrik modern via `BiometricPrompt` API.
  - Menerapkan mekanisme proteksi anti-rooting dan integritas APK sederhana.

### 1.4 Core Banking & Keypad Engineer (`@banking-engineer`)
- **Fokus:** Logika bisnis perbankan, alur otorisasi PIN, dan manajemen sesi nasabah.
- **Tanggung Jawab:**
  - Membangun custom Composable PIN Pad (numeric keyboard 3x4 acak/standar khas m-BCA).
  - Mengelola *PinAuthUiState* (indikator titik PIN, validasi panjang 6 digit, batas toleransi salah PIN).
  - Mengatur *Auto-Session Timeout* (aplikasi otomatis logout jika idle lebih dari 3 menit).
  - Menyusun state machine MVI untuk navigasi menu dashboard perbankan.

### 1.5 Hardware & NFC Card Specialist (`@nfc-hardware-engineer`)
- **Fokus:** Integrasi sensor perangkat keras (NFC untuk BCA Flazz dan CameraX untuk QRIS).
- **Tanggung Jawab:**
  - Menghubungkan Android NFC Adapter dengan intent filter tag pembacaan kartu (`ACTION_TECH_DISCOVERED`, `IsoDep`, `NfcF`).
  - Mengembangkan simulator kartu Flazz (untuk mode debug / HP tanpa NFC) agar nasabah dapat menguji pengecekan saldo kapan saja.
  - Memberikan umpan balik fisik: *haptic feedback vibration* saat kartu berhasil terdeteksi.

### 1.6 QA, Performance & Release Auditor (`@qa-performance-auditor`)
- **Fokus:** Pengujian otomatis, optimasi ukuran APK, dan kesiapan rilis produksi.
- **Tanggung Jawab:**
  - Melakukan audit performa cold-start (harus di bawah **150 ms**).
  - Mengawasi stabilitas Compose Recomposition (memastikan tidak terjadi recomposition berulang/looping).
  - Memastikan ukuran file APK Release berada di bawah **6.0 MB** dengan optimasi ProGuard/R8.
  - Melakukan visual regression check membandingkan rendering Compose terhadap gambar referensi `awal.jpeg`.

---

## 2. Diagram Protokol Kerja Multi-Agent

```mermaid
flowchart TD
    Architect["@lead-architect<br/>(Setup Gradle, Hilt, Clean Arch)"] --> UIDesigner["@compose-ui-designer<br/>(Desain Theme, Starburst, 3-Row Card)"]
    Architect --> Security["@security-engineer<br/>(Keystore, FLAG_SECURE, Biometric)"]
    
    UIDesigner <--> Banking["@banking-engineer<br/>(PIN Pad Keypad & Auth Logic)"]
    Security <--> Banking
    
    Banking --> Hardware["@nfc-hardware-engineer<br/>(NFC Flazz & Haptic Feedback)"]
    
    UIDesigner --> Auditor["@qa-performance-auditor<br/>(Profiling, APK < 6MB, Visual Audit)"]
    Hardware --> Auditor
    Security --> Auditor
    
    Auditor --> FinalRelease["Production Ready APK (< 6MB)"]
```

---

## 3. Aturan Hand-off Antar-Agen

1. **Dari `@lead-architect` ke `@compose-ui-designer` & `@security-engineer`:**
   - Seluruh dependency dan token warna dasar sudah didefinisikan dalam `Theme.kt` sebelum pembuatan komponen UI dimulai.
2. **Dari `@banking-engineer` ke `@compose-ui-designer`:**
   - Antarmuka UI hanya boleh mengonsumsi `UiState` immutable dan mengirimkan fungsi callback event lambda (`onPinDigitClick: (Char) -> Unit`).
3. **Dari Seluruh Agen ke `@qa-performance-auditor`:**
   - Setiap fitur yang selesai harus dapat diuji melalui `@Preview` dan lolos kompilasi R8 minification tanpa peringatan (*zero compile warnings*).
