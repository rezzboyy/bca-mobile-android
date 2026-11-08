# Rules & Standard Pedoman Pengembangan (BCA Mobile Android Native)

Dokumen ini memuat standar teknis, konvensi penamaan, arsitektur, standar keamanan perbankan, dan aturan kualitas kode untuk proyek **BCA Mobile Android** berbasis **Kotlin + Jetpack Compose** (Android Native murni).

---

## 1. Tech Stack & Library Standards

| Komponen | Teknologi / Library | Catatan & Alasan Pemilihan |
|---|---|---|
| **Bahasa Pemrograman** | Kotlin (1.9+ / 2.0+) | Null-safety ketat, modern concurrency via Coroutines |
| **UI Toolkit** | Jetpack Compose (Compose BOM 2024.x+) | UI deklaratif modern, reactive state-driven, 60-120 FPS |
| **Design System** | Material 3 + Custom BCA Theme Tokens | Skema warna BCA Deep Navy, gradien metalik 3D, typography resmi |
| **Arsitektur** | Clean Architecture + MVVM / MVI | UI (Composable Screen) -> ViewModel -> UseCase -> Repository |
| **State Management** | Kotlin StateFlow & SharedFlow | `collectAsStateWithLifecycle()` untuk lifecycle-safe rendering |
| **Dependency Injection** | Hilt (Dagger Hilt) | `@AndroidEntryPoint`, `@HiltViewModel`, modular decoupling |
| **Navigasi** | Navigation Compose | Type-safe navigation (Kotlin Serialization routes) |
| **Keamanan & Storage** | Android Keystore + EncryptedSharedPreferences | Penyimpanan token, session, dan biometric key berbasis hardware |
| **Biometric Auth** | BiometricPrompt API (AndroidX Biometric) | Fingerprint & Face Unlock bawaan hardware |
| **NFC Integration** | Android NFC Adapter (`IsoDep` / `NfcF`) | Simulasi & pembacaan kartu chip BCA Flazz |
| **Asynchronous Engine** | Kotlin Coroutines (`Dispatchers.IO`, `Main`) | Eksekusi non-blocking bebas ANR |
| **Build & Minifikasi** | Gradle Kotlin DSL (`.kts`) + ProGuard/R8 | Target ukuran APK release: **< 6 MB** |

---

## 2. Arsitektur & Prinsip Kode

### 2.1 Pola MVVM / MVI dengan Jetpack Compose
1. **Composable Screen (Presentation/View Layer):**
   - Composable murni bertindak sebagai rendering visual dari state dan pengirim event user (*State Down, Events Up*).
   - Dilarang keras menaruh logika bisnis perbankan, kalkulasi saldo, validasi enkripsi, atau request I/O di dalam fungsi `@Composable`.
   - Wajib menerapkan **State Hoisting**: Pisahkan stateful screen (`*Route` / `*ScreenContainer`) dari stateless presentation (`*Content`).
   - Setiap screen wajib memiliki anotasi `@Preview` dengan variasi device (smartphone standar 390x844dp, layar kecil 360x640dp, dan Dark Mode).

2. **ViewModel Layer:**
   - Menyimpan seluruh state UI dalam satu objek `StateFlow<UiState>` yang immutable (sealed interface/class).
   - Menggunakan `viewModelScope` untuk memicu use case dan operasi asynchronous.
   - Dilarang memegang referensi ke `Context`, `Activity`, atau `Composable Node` untuk mencegah memory leak.

3. **Domain Layer (Use Cases):**
   - Setiap Use Case merepresentasikan satu aksi bisnis perbankan (contoh: `ValidatePinUseCase`, `ReadFlazzBalanceUseCase`, `CheckBiometricAvailabilityUseCase`).
   - Murni kode Kotlin (tidak bergantung pada framework Android).

4. **Data Layer (Repository & Data Sources):**
   - Menjadi *Single Source of Truth* (SSOT).
   - Menangani komunikasi jaringan lokal/mock, penyimpanan data terenkripsi, dan sensor hardware (NFC, Biometric).
   - Seluruh operasi I/O wajib dipaksa berjalan pada `Dispatchers.IO`.

---

## 3. Konvensi Penamaan (Naming Conventions)

### 3.1 Kode Kotlin & Compose
| Tipe | Format Penamaan | Contoh |
|---|---|---|
| **Composable Function** | PascalCase (Kata Benda/Frasa) | `BcaHubScreen()`, `GlossyActionCard()`, `PinKeypad()` |
| **Screen Route / Container** | `<Feature>Screen` / `<Feature>Route` | `LoginPinScreen`, `FlazzCardScreen`, `BcaHubRoute` |
| **ViewModel** | `<Feature>ViewModel` | `BcaHubViewModel`, `PinAuthViewModel`, `FlazzViewModel` |
| **UI State (Sealed)** | `<Feature>UiState` | `HubUiState`, `PinAuthUiState`, `FlazzUiState` |
| **UI Event / Intent** | `<Feature>UiEvent` | `PinEntered`, `FlazzCardTapped`, `DismissModal` |
| **Repository** | `<Feature>Repository` | `AuthRepository`, `FlazzCardRepository` |
| **Functions / Methods** | camelCase (Kata Kerja) | `verifyUserPin()`, `onFlazzCardDetected()` |
| **Constants** | UPPER_SNAKE_CASE | `PIN_LENGTH`, `MAX_LOGIN_ATTEMPTS`, `ANIM_DURATION_MS` |

### 3.2 Resources (Drawables, Colors, Strings)
Semua aset Android wajib menggunakan format `lowercase_snake_case`:
- **Vector / Icon Drawables:** `ic_bca_<nama>.xml` (contoh: `ic_bca_logo.xml`, `ic_info_circle.xml`, `ic_flazz.xml`).
- **Bitmap / Backgrounds:** `bg_bca_<deskripsi>.png` (contoh: `bg_bca_radiant_navy.png`, `bg_button_glossy.xml`).
- **Color Tokens:** `bca_<tujuan>_<shade>` (contoh: `bca_navy_dark`, `bca_blue_primary`, `bca_metallic_cyan`).
- **String Resources:** `<feature>_<deskripsi>` (contoh: `hub_mbca_title`, `pin_enter_instruction`, `error_wrong_pin`).

---

## 4. Standar UI/UX BCA Mobile (High-Fidelity)

1. **Akurasi Visual Presisi:**
   - Background utama wajib mereplikasi gradien radial khas BCA: Navy pekat di tepi (`#000518`), biru royal di tengah (`#001438`), dengan efek *geometric starburst* tepat di balik logo.
   - Kartu 3-Row (`m-BCA`, `KlikBCA`, `myBCA`) wajib memiliki border putih presisi 2dp, sudut rounded (18dp), divider pemisah, dan gradien metalik gloss vertikal.
2. **Ukuran Font & Spacing:**
   - Spacing & Padding: Wajib kelipatan 4dp / 8dp (`4.dp`, `8.dp`, `12.dp`, `16.dp`, `20.dp`, `24.dp`).
   - Ukuran Teks: Wajib menggunakan `sp` (`10.sp`, `12.sp`, `14.sp`, `18.sp`, `22.sp`).
3. **Sentuhan & Feedback (Touch Feedback):**
   - Setiap tombol interaktif (`Buka Rekening Baru`, `Flazz`, baris menu) wajib memiliki transisi visual saat ditekan (`scale` 0.98x atau `brightness` highlight) serta *haptic feedback* ringan standar perbankan.
4. **Adaptive Screen Layout:**
   - Layout tidak boleh terpotong pada berbagai aspect ratio HP (dari layar rasio 16:9 lawas hingga layar panjang 20:9 atau foldable).
   - Gunakan `Modifier.navigationBarsPadding()` dan `Modifier.statusBarsPadding()` untuk mendukung Android 15 Edge-to-Edge secara sempurna.

---

## 5. Standar Keamanan Aplikasi FinTech & Perbankan

1. **Anti-Screen Capture & Recording (`FLAG_SECURE`):**
   - Layar input PIN, nomor rekening, dan informasi saldo wajib mengaktifkan flag `WindowManager.LayoutParams.FLAG_SECURE` untuk memblokir screenshot dan screen recorder.
2. **Penyimpanan Kredensial Sensitif:**
   - Dilarang menyimpan PIN atau plaintext password di memori jangka panjang atau SharedPreferences biasa.
   - Wajib menggunakan **Android Keystore System** dengan algoritma AES-256 GCM untuk enkripsi data lokal.
3. **Sanitasi Logcat (No Leak in Production):**
   - Tidak boleh ada logging nomor kartu, saldo, nama nasabah, atau PIN ke Android Logcat. Gunakan *Timber tree* yang dimatikan pada mode `release`.
4. **Root & Integrity Detection:**
   - Sediakan layer deteksi integritas OS (Root detection via SafetyNet / Play Integrity API) untuk memperingatkan nasabah jika perangkat tidak aman.
