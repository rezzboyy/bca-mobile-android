# Roadmap & Task Breakdown (BCA Mobile Android Native)

Dokumen ini memuat daftar tugas terperinci, tahapan eksekusi, dan pelacakan progres pengembangan aplikasi **BCA Mobile Android** dengan teknologi **Kotlin + Jetpack Compose** (Android Native murni).

---

## Status Progres
- [ ] Belum Dimulai
- [/] Sedang Dikerjakan
- [x] Selesai

> [!NOTE]
> Proyek ini dibangun sebagai konversi native murni (Opsi 2) dari prototipe web BCA Mobile (`E:\My Produk\App Android\Bca Mobile`).
> Seluruh aset visual (wallpaper starburst, logo resolusi tinggi, icon m-BCA, KlikBCA, myBCA) diadaptasi langsung untuk mencapai akurasi pixel-perfect dengan performa hardware Android maksimal (cold start instan, konsumsi RAM < 45 MB, ukuran APK < 6 MB).

---

## Fase 1: Inisialisasi Proyek & Fondasi Arsitektur
- [x] **1.1 Setup Android Project & Gradle DSL**
  - [x] Konfigurasi `settings.gradle.kts` dan root `build.gradle.kts` dengan Gradle 8.x + JDK 17.
  - [x] Konfigurasi `app/build.gradle.kts`:
    - [x] `compileSdk = 34`, `minSdk = 24`, `targetSdk = 34`.
    - [x] Compose Compiler plugin & Jetpack Compose BOM (`2024.05.00`).
    - [x] Material 3 (`androidx.compose.material3`).
    - [x] AndroidX Biometric (`androidx.biometric:biometric-ktx`).
    - [x] Security Crypto (`androidx.security:security-crypto`).
  - [x] Konfigurasi `AndroidManifest.xml` (Edge-to-Edge, permissions NFC & Biometric, `allowBackup="false"`).
- [x] **1.2 Setup Clean Architecture Package Structure**
  - [x] Paket `ui/theme` (`Color.kt`, `Theme.kt`, `Type.kt`).
  - [x] Paket `ui/components` (`MenuRowCard.kt`, `GlossyActionButton.kt`, `BcaModalDialog.kt`).
  - [x] Paket `ui/screens` (`BcaHubScreen.kt`).
  - [x] Activity entry point (`MainActivity.kt`).

---

## Fase 2: Migrasi Aset Visual & Design System (Tokens)
- [x] **2.1 Migrasi Aset dari Prototipe Web ke Android Resources**
  - [x] Konversi & salin wallpaper radiant navy ke `res/drawable-nodpi/bca_wallpaper.png`.
  - [x] Salin logo resmi `bca_mobile_logo.png` ke `res/drawable/bca_mobile_logo.png`.
  - [x] Salin icon rounded tiles: `mbca_clean.png`, `klikbca_clean.png`, `mybca_clean.png`.
  - [x] Salin icon info lingkaran `info_icon_clean.png` dan logo aplikasi `bca_app_icon.png`.
- [x] **2.2 Implementasi Jetpack Compose Theme & Tokens**
  - [x] Buat file `Color.kt` dengan definisi palet warna BCA (Navy background, royal blue, cyan highlight, white borders).
  - [x] Buat custom `Brush` gradien vertikal 3-stop untuk menu row dan 5-stop untuk tombol 3D glossy.
  - [x] Buat file `Type.kt` dengan tipografi presisi (*sans-serif* tebal, tracking, line height).
  - [x] Buat file `Theme.kt` (`BcaMobileTheme`) pembungkus context composable.

---

## Fase 3: Layar Utama Launcher (Hub Screen) UI Pixel-Perfect
- [x] **3.1 Container Layar & Adaptive Layout**
  - [x] Buat composable `BcaHubScreen` dengan background wallpaper edge-to-edge.
  - [x] Dukungan max-width container (448dp pada tablet/desktop) dan full-width pada smartphone.
  - [x] Setup `statusBarsPadding()` dan `navigationBarsPadding()` bebas tumpang tindih.
- [x] **3.2 Top Info Bar & Logo Area**
  - [x] Tombol info bulat `(i)` di pojok kanan atas dengan efek klik halus.
  - [x] Container logo `BCA mobile` dengan drop-shadow lembut.
- [x] **3.3 Kartu Aksi 3 Baris (3-Row Action Card)**
  - [x] Container border putih 2dp dengan sudut membulat 18dp.
  - [x] Baris 1: `m-BCA` (tile icon + label tebal + divider pemisah).
  - [x] Baris 2: `KlikBCA` (tile icon + label tebal + divider pemisah).
  - [x] Baris 3: `myBCA` (tile icon + label tebal + kapsul badge `"NEW!"`).
  - [x] Efek ripple dan animasi klik touch feedback pada setiap baris.
- [x] **3.4 Tombol Aksi Metalik 3D (Glossy Action Buttons)**
  - [x] Tombol `Buka Rekening Baru` dengan gradien metalik 5-stop, rounded 16dp, dan drop-shadow tebal.
  - [x] Tombol `Flazz` dengan styling serupa.
  - [x] Animasi reduksi skala (`0.985f`) saat tombol disentuh pengguna.
- [x] **3.5 Teks Regulasi & Disclaimer (Footer)**
  - [x] Susunan teks resmi OJK, BI, dan LPS rata tengah dengan opacity 95%.
- [x] **3.6 Dialog / Modal Interaktif**
  - [x] Modal informasi fitur saat menu ditekan (`Info`, `KlikBCA`, `myBCA`, `Buka Rekening Baru`).
  - [x] Dialog preview popup beranimasi fade-in dan backdrop blur.

---

## Fase 4: Modul Autentikasi & Dialog Kode Akses (m-BCA)
- [x] **4.1 Dialog Autentikasi Kode Akses (Pixel-Perfect dari Foto Referensi)**
  - [x] Card dialog putih presisi dengan border radius 6dp.
  - [x] Judul tengah `"Kode Akses"`.
  - [x] Input box dengan placeholder `"Input 6 alphanum"`.
  - [x] Filter alfanumerik 6-digit & auto-masking password.
  - [x] Tombol `"Cancel"` dan `"Login"` berdampingan dengan gradien biru BCA murni.
  - [x] Validasi panjang 6 karakter & pesan error interaktif.
- [x] **4.2 Dashboard Layar Menu Utama m-BCA (Sesuai IMG-20261021-WA0007.jpg)**
  - [x] Top bar putih dengan logo resmi `BCA mobile`, lampu indikator hijau online, dan tombol Logout.
  - [x] Greeting nasabah atas nama **DADANG MUBAROK** ("Selamat datang, DADANG MUBAROK").
  - [x] Kartu banner promo resmi ("THE NEW GACHA BERHADIAH").
  - [x] Grid 4 kolom dengan 11 icon tile asli:
    - [x] Baris 1: `m-Info`, `m-Transfer`, `m-Payment`, `m-Commerce`
    - [x] Baris 2: `Cardless`, `m-Admin`, `BCA Keyboard`, `Flazz`
    - [x] Baris 3: `BagiBagi`, `Lifestyle`, `Financial Diary`
  - [x] Bottom Navigation Bar (5 tabs: `Home` [aktif], `Transaksi`, `QRIS` [floating icon], `Notifikasi`, `Akun Saya`).
  - [x] **4.3 Pengerjaan Sub-Menu & Alur Transaksi**
    - [x] **m-Info**: Replikasi struktur 100% presisi pixel-perfect sesuai foto referensi asli (`pasted-image-2026-10-28T00-11-24-360Z.png`): Top bar putih tanpa panah back, kartu mengambang berlatar navy pekat, header squircle `m-Info`, susunan menu persis: Group 1 (Info Saldo, Mutasi Rekening, Rekening Deposito, Info Reward BCA), Section Info Reksadana (NAB Reksadana, Saldo Reksadana, Info Kurs), Section Info RDN (Info Saldo, Mutasi Rekening), Section Info KPR (Inquiry Pinjaman), Section Info Kartu Kredit (Saldo), chevron cyan `>`, dan Bottom Navigation Bar.
    - [x] **m-Transfer**: Replikasi struktur 100% presisi pixel-perfect sesuai foto referensi asli (`pasted-image-2026-10-28T00-11-49-982Z.png`): Top bar putih, Section Daftar Transfer (Antar Rekening, Antar Bank), Section Transfer (Antar Rekening, Antar Bank, BCA Virtual Account, Sakuku, Status Transaksi Sakuku, Inbox (16)), Bottom Navigation Bar aktif pada tab Transaksi.
    - [x] **Notifikasi**: Implementasi modul layar Notifikasi sesuai foto referensi asli (`pasted-image-2026-10-28T00-12-27-800Z.png`): Filter chip horizontal (Semua [aktif], OTP & Persetujuan, Info), tombol aksi "Hapus Semua" dan "Tandai Terbaca Semua", empty state "Silakan hubungi HALO BCA" dengan tombol 3D glossy "Refresh", Bottom Navigation Bar aktif pada tab Notifikasi.
    - [x] **Cardless**: Tarik tunai tanpa kartu di ATM BCA, pilihan nominal kelipatan Rp 100k s/d 1M, autentikasi PIN, penerbitan 6-digit kode tarik tunai & countdown timer 60 menit.
    - [x] **Bottom Navigation Bar Reusable**: Komponen `BcaBottomNavBar.kt` terintegrasi di Dashboard, m-Info, m-Transfer, dan Notifikasi dengan floating elevated button QRIS.

---

## Fase 5: Modul Flazz & Simulasi Chip NFC
- [x] **5.1 Layar Pembacaan Flazz**
  - [x] Animasi radar gelombang pembacaan kartu Flazz (infinite pulsing radar animation di Compose).
  - [x] Tombol dan engine simulasi tempel kartu Flazz (berfungsi penuh baik dengan NFC fisik maupun tanpa hardware NFC).
- [x] **5.2 Kartu & Transaksi Flazz**
  - [x] Tampilan kartu Flazz resmi (chip visual, nomor kartu tersamar `6019 **** **** 8821`, saldo aktif Rp 175.000).
  - [x] Riwayat mutasi kartu Flazz (Tol Cililitan, Indomaret, Transjakarta, Top Up ATM BCA).

---

## Fase 6: Keamanan Data Lokal & Enkripsi
- [ ] **6.1 Hardware Keystore Wrapper**
  - [ ] Kelas utilitas `CryptoManager.kt` menggunakan Android Keystore `AES/GCM/NoPadding`.
- [ ] **6.2 Enkripsi Preferensi & Cache Sesi**
  - [ ] Implementasi `EncryptedSharedPreferences` untuk menyimpan data akun mock, status aktivasi, dan preferensi biometric.
  - [ ] Timer otomatis Session Invalidation (logout otomatis setelah periode idle).

---

## Fase 7: Pengujian, Profiling & Visual Regression Audit
- [x] **7.1 Compose Previews & Unit Tests**
  - [x] Unit testing domain model & repository `BcaRepositoryTest.kt`, color palette & tokens `BcaThemeTest.kt`, serta struktur menu `BcaMenuFidelityTest.kt`.
  - [x] Skrip verifikasi audit otomatis `app/verify_audit.py` (57 assertions pass, 0 error, 0 warning).
- [ ] **7.2 Audit Fidelitas Visual**
  - [ ] Uji perbandingan berdampingan (side-by-side) antara render Jetpack Compose dengan prototipe web dan tangkapan layar `awal.jpeg`.
- [ ] **7.3 Optimasi Binary & Release Build**
  - [ ] Menjalankan kompilasi R8/ProGuard Release.
  - [ ] Verifikasi ukuran APK release final: **target < 6 MB**.
  - [ ] Verifikasi cold-start latency: **target < 150 ms**.
