# Production Readiness & Release Guide (BCA Mobile Android Native)

Dokumen ini memuat panduan persiapan rilis produksi, optimasi performa binary, konfigurasi ProGuard/R8, dan kepatuhan standar keamanan perbankan (Bank-Grade Standards & OWASP MASVS) untuk aplikasi **BCA Mobile Android** (Kotlin + Jetpack Compose).

---

## 1. Konfigurasi Build & Spesifikasi SDK

### 1.1 Spesifikasi Versi SDK
- **`compileSdk`**: `35` (Android 15)
- **`minSdk`**: `24` (Android 7.0 Nougat) — mencakup >97% perangkat aktif global dengan dukungan hardware crypto modern.
- **`targetSdk`**: `35` (Kepatuhan penuh standar Google Play Store terbaru).
- **`jvmTarget`**: `17` (atau `21` sesuai toolchain JDK modern).

### 1.2 Skema Versi Aplikasi
- **`versionCode`**: Nilai integer berurutan (`1`, `2`, `3`, ...) untuk setiap rilis.
- **`versionName`**: Semantic Versioning `vMAJOR.MINOR.PATCH` (contoh: `v1.0.0-rc1`).

---

## 2. Keamanan & Penandatanganan Aplikasi (App Signing)

### 2.1 File `keystore.properties` (Lokal & Terlindungi)
Keystore produksi **dilarang keras** di-commit ke repositori Git. Gunakan file konfigurasi terpisah:

```properties
# keystore.properties (Tambahkan ke .gitignore)
KEYSTORE_PATH=../keystore/bca_mobile_release.jks
KEYSTORE_PASSWORD=super_secure_keystore_password
KEY_ALIAS=bca_mobile_prod
KEY_PASSWORD=super_secure_key_password
```

### 2.2 Konfigurasi Gradle (`app/build.gradle.kts`)

```kotlin
import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(keystorePropertiesFile.inputStream())
    }
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("KEYSTORE_PATH") ?: "dummy.jks")
            storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD")
            keyAlias = keystoreProperties.getProperty("KEY_ALIAS")
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD")
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

---

## 3. Konfigurasi ProGuard / R8 (`proguard-rules.pro`)

Pengaturan R8 agresif untuk menekan ukuran APK hingga **< 6.0 MB** dan mengamankan kode dari rekayasa balik (reverse engineering):

```proguard
# ==============================================================================
# Jetpack Compose & Kotlin Rules
# ==============================================================================
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**

# ==============================================================================
# Hilt / Dagger Injection
# ==============================================================================
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }
-keep class * extends dagger.hilt.internal.ComponentManager { *; }
-keepclassmembers class * {
    @javax.inject.Inject *;
}

# ==============================================================================
# Keamanan Perbankan: Hapus Seluruh Debug Logging di Mode Release
# ==============================================================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# ==============================================================================
# Data Models & Serialization (Preserve Domain Models)
# ==============================================================================
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @kotlinx.serialization.SerialName <fields>;
}
```

---

## 4. Strategi Optimasi Ukuran Binary (< 6 MB Target)

1. **Format Aset Visual:**
   - Semua gambar raster berukuran besar (seperti background radiant navy) dikonversi ke format **WebP lossless/lossy 90%** (mengurangi bobot gambar hingga ~65% dibanding PNG biasa).
   - Ikon tombol, logo vektor, dan badge menggunakan **Android Vector Drawable (`VectorDrawable`)**.
2. **Android App Bundle (AAB):**
   - Kompilasi menggunakan format `.aab` saat rilis Play Store agar Google Play hanya mengunduh binary yang spesifik untuk arsitektur CPU target pengguna (`arm64-v8a`), memotong ukuran download hingga ~50%.
3. **Pembersihan Resource Tidak Terpakai (`isShrinkResources = true`):**
   - Membuang string dan resource bawaan library dependensi yang tidak pernah direferensikan dalam kode aplikasi.

---

## 5. Checklist Kepatuhan Keamanan FinTech (OWASP MASVS)

- [x] **Anti-Screen Capture:** `FLAG_SECURE` aktif pada seluruh dialog input PIN dan info saldo.
- [x] **Backup Terlarang:** Konfigurasi `android:allowBackup="false"` di `AndroidManifest.xml` untuk mencegah eksfiltrasi data via ADB backup.
- [x] **Hardware Keystore:** Enkripsi token sesi menggunakan Android Keystore `KeyGenParameterSpec.Builder(..., PURPOSE_ENCRYPT or PURPOSE_DECRYPT)`.
- [x] **Network Security Config:** Memaksa komunikasi HTTPS murni (`android:usesCleartextTraffic="false"`).
- [x] **Izin Minimal (Zero Intrusion):**
  - Hanya meminta izin `android.permission.NFC` (untuk kartu Flazz) dan `android.permission.USE_BIOMETRIC`.
  - Tidak meminta izin lokasi, kontak, atau penyimpanan eksternal yang tidak diperlukan.

---

## 6. Checklist Verifikasi Pra-Rilis Google Play Store

1. **Uji Cold Start:** Waktu startup aplikasi dari icon tap hingga UI tampil < 150 ms (diukur via Android Studio Profiler).
2. **Uji Kompatibilitas Multi-Device:**
   - Layar kecil: Resolusi 360x640 dp.
   - Layar standar: Resolusi 390x844 dp (iPhone/Galaxy aspect).
   - Layar tinggi: Rasio 20:9 (412x915 dp).
3. **Verifikasi Tanda Tangan APK:**
   ```bash
   apksigner verify --verbose bca-mobile-release.apk
   ```
4. **Deklarasi Layanan Finansial:** Mengisi formulir deklarasi aplikasi perbankan/keuangan di Google Play Console.
