# BCA Mobile Android — Parity Memory & Master Rules (100% Visual & Functional Fidelity)

Dokumen ini mencatat ringkasan aturan permanen yang dipelajari dan disepakati bersama pengguna untuk menjaga paritas 100% pada aplikasi **BCA Mobile Android**.

---

## 📌 Rekapitulasi Pekerjaan Selesai (19 November 2026)

### 1. Banner Iklan 600k Hadiah dari myBCA (Update, Refinement, & Presisi Tengah)
- **Tugas**: Mengambil materi "Total 600.000+ Hadiah Siap Dimenangkan!" dari `Bank Mobile/myBCA` untuk menggantikan grafis 600rb lama di `Bca Mobile Android` tanpa tabrakan (*ngga nabrak*), merapikan outline halus, dan memposisikan tepat di tengah.
- **Implementasi Teknis**:
  - Aset master: `banner_fg_600k.png` (2544 x 336 px, rasio 636:84).
  - Eliminasi total dirty grey fringes dengan *4x SDF (Signed Distance Field) subpixel anti-aliasing* dan *pure white edge extrapolation*.
  - Titik pusat horizontal tepat di sumbu kanvas `x = 1272.0 px` (`x = 502..2042 px`).
  - Jarak margin kiri `502 px` == margin kanan `502 px` (simetris sempurna), serta jarak aman ke tombol pill kanan `58 px`.
  - Sinkronisasi aset ke:
    - `app/src/main/res/drawable/banner_fg_600k.png`
    - `iklan banner/iklan banner/drawable/banner_fg_600k.png`
    - `iklan banner/iklan banner/res/drawable/banner_fg_600k.png`
    - `app/src/main/res/drawable/banner_hadiah_600k.png` (legacy composite fallback).
- **Audit & Bukti Visual**: `AUDIT_BEFORE_AFTER/18_Audit_Update_Banner_600k_Hadiah_Dari_myBCA/`.

### 2. Penguncian Header Badge `m-Info` (Fixed Stationary saat Scroll)
- **Tugas**: Mengunci badge `m-Info` di kartu atas agar tidak ikut tergulir (*gak ikut kescroll*) saat pengguna men-scroll daftar item menu ke bawah.
- **Implementasi Teknis**:
  - File: `app/src/main/java/com/bca/mobile/ui/screens/MInfoMenuScreen.kt`.
  - Memisahkan arsitektur layout menjadi 3 zona hierarki:
    1. **Outer Floating Card**: `Column` berborder putih dengan background putih solid dan sudut membulat 14dp.
    2. **Fixed Header Row**: Baris stasioner dengan tinggi tetap 68dp, squircle tile 56dp (`tile_minfo`), dan judul "m-Info" 20sp bold yang tetap diam di atas.
    3. **Independent Scrollable Container**: `Column(modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()))` yang memuat seluruh 5 grup item menu (Info Saldo, Mutasi, Deposito, Reward BCA, Reksadana, RDN, KPR, Kartu Kredit).
- **Audit & Bukti Visual**: `AUDIT_BEFORE_AFTER/19_Audit_Lock_Badge_mInfo_Fixed_Header/`.

### 3. Garis Pemisah (Divider) Putih Solid
- **Tugas**: Mengubah garis pemisah tepat di bawah badge header `m-Info` menjadi warna putih solid.
- **Implementasi Teknis**:
  - Mengganti divider warna abu-abu default dengan:
    ```kotlin
    HorizontalDivider(
        color = Color.White,
        thickness = 1.dp
    )
    ```
  - Menghasilkan transisi yang mulus dan bersih antara header stasioner dan daftar menu yang bergulir di bawahnya.

### 4. Kompilasi & Rilis Otomatis (1-Click Auto-Release Engine)
- **Tugas**: Eksekusi rilis binary produksi dan pembaruan OTA.
- **Implementasi Teknis**:
  - Menjalankan `release.ps1` dengan parameter catatan rilis komprehensif.
  - Bump versi: `v1.0.31` (code 32) ➔ `v1.0.32` (code 33).
  - Kompilasi Gradle: `./gradlew assembleRelease` sukses dalam 1m 4s.
  - Keystore signature: RSA 2048-bit `bca_release.jks`.
  - Binary dihasilkan: `app/build/outputs/apk/release/app-release.apk` (16.52 MB).
  - Git commit & push: Commit `5f4ea6b` dengan tag `v1.0.32` ke branch `master`.
  - GitHub Release: Publikasi otomatis ke `https://github.com/rezzboyy/bca-mobile-android/releases/tag/v1.0.32`.
  - OTA Update: Aktif via `GitHubUpdateManager.kt` di semua HP nasabah.

---

## 📐 Aturan Baku & Invarian Desain (Memory Rules)

### 1. Aturan Anti-Halo & Edge Extrapolation Grafis
- Jangan pernah melakukan resampling Lanczos langsung dari gambar dengan latar belakang transparan `(0, 0, 0, 0)`. Hal ini akan menyebabkan piksel tepi tercampur menjadi abu-abu kotor (*halo effect*).
- **Prosedur Wajib**:
  1. Ekstrapolasikan nilai warna asli (misal putih `(255, 255, 255)`) ke seluruh piksel transparan di sekitar tepi sebelum downsampling.
  2. Gunakan *4x Signed Distance Field (SDF)* atau *supersampling anti-aliasing* untuk menghasilkan kontur teks dan ikon yang tajam dan mulus.

### 2. Aturan Perataan Simetris Kanvas (Centered Symmetry)
- Saat meletakkan elemen teks/grafis di dalam kanvas banner dengan komponen interaktif (seperti tombol pill di kanan):
  - Ukur margin kiri terhadap tepi kanvas `x = 0`.
  - Ukur margin kanan terhadap tepi kanan teks atau batas elemen interaktif.
  - Pastikan perataan dead center dihitung dari total lebar kanvas (`x_center = W / 2`), dengan jarak kiri dan kanan seimbang (`margin_left == margin_right`) dan tetap menjaga batas aman (*safe gap >= 40 px*) terhadap tombol aksi.

### 3. Aturan Fixed Header pada Floating Menu Cards
- Ketika pengguna meminta badge atau header modul menu tidak ikut tergulir saat konten di-scroll:
  - Dilarang memasang `Modifier.verticalScroll()` pada container terluar kartu.
  - Struktur wajib:
    ```
    Card Container (fillMaxWidth, weight(1f))
    ├── Fixed Header Row (height 68dp, squircle tile + title) [TIDAK SCROLL]
    ├── HorizontalDivider (Color.White, 1.dp)                 [TIDAK SCROLL]
    └── Scrollable Items Column (weight(1f), verticalScroll)  [BISA SCROLL]
        └── Menu Groups & Items
    ```

### 4. Aturan Garis Pembatas Putih Solid
- Pembatas di bawah fixed header badge harus menggunakan `Color.White` solid dengan ketebalan `1.dp`.

### 5. Aturan Kerja 3 Tahap & Audit Visual
- Saat pengguna meminta pengerjaan dalam beberapa tahap (*"kerjakan dalam 3 tahap supaya hasilnya rapih dan sesuai"*):
  - **Tahap 1**: Refactoring struktur layout dan pemisahan kontainer.
  - **Tahap 2**: Implementasi state scrolling independen dan styling garis pemisah.
  - **Tahap 3**: Pengujian unit test, verifikasi render visual, dan pembuatan papan komparasi side-by-side di direktori `AUDIT_BEFORE_AFTER/`.
