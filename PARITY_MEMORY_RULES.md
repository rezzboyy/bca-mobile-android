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

### 5. Kalibrasi Kartu Putih Bawah Menempel Mulus (Seamless White Touch) ke Kubah QRIS & Scroll Persistence
- **Tugas**: Menyamakan badge bawah kartu putih pada `m-Info` agar menempel mulus langsung (*nempel putih tanpa celah*) ke kubah tombol QRIS seperti contoh video asli BCA, dan saat di-scroll ke bawah badge bawah tetap menempel stasioner di QRIS.
- **Implementasi Teknis**:
  - `BcaBottomNavBar.kt`:
    - Menambahkan `tightDome: Boolean = false`.
    - Saat `tightDome = true`: Box menggunakan `aspectRatio(720f / 174f)` untuk memangkas 26px area transparan di atas kubah.
    - `Image` menggunakan `alignment = Alignment.BottomCenter` dan `contentScale = ContentScale.FillWidth`. Puncak kubah QRIS kini tepat berada di batas $y = 0$ dari navbar.
  - `MInfoMenuScreen.kt`:
    - Menggunakan arsitektur `Column(modifier = Modifier.fillMaxSize())`:
      1. `BcaSubMenuTopBar` (Header putih m-Info dengan sinyal hijau)
      2. Kontainer Kartu Putih:
         `Modifier.fillMaxWidth().weight(1f).offset(y = 5.dp).padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 0.dp).clip(RoundedCornerShape(14.dp)).background(Color.White)`
      3. Di dalam kartu: Fixed Sticky Header 68dp + Divider Putih Solid 1dp + `Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()))`
      4. `BcaBottomNavBar(selectedTab = BcaNavTab.NONE, onTabSelected = onTabSelected, tightDome = true, modifier = Modifier.zIndex(1f))`
  - **Hasil & Verifikasi Live (OnePlus 7 Pro)**:
    - Putih kartu menyatu mulus ke bibir kubah QRIS ($y = 2612\text{ px}$) dengan ZERO DARK GAP.
    - Sisi kiri dan kanan tetap menampilkan navy background selebar 17dp (68 px).
    - Saat di-scroll ke bawah sampai *Info KPR -> Inquiry Pinjaman*, kontainer kartu dan badge bawah TETAP DIAM NEMPEL PUTIH KE QRIS tanpa bergeser 1 piksel pun.
- **Audit & Bukti Visual**: `AUDIT_BEFORE_AFTER/20_Audit_Presisi_Badge_Bawah_Sampai_Nyentuh_QRIS/`.

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

### 6. Aturan Kontak Presisi Kartu Bawah vs Kubah QRIS (Seamless White Touch)
- Pada layar kartu mengambang berlatar navy (`m-Info`, `m-Transfer`):
  - Container kartu putih tidak boleh dibiarkan mengambang dengan celah kosong di atas tombol QRIS, dan tidak boleh ada garis gelap yang memisahkan putih kartu dengan kubah QRIS.
  - Tepi bawah kartu putih harus memanjang ke bawah sampai **menempel putih secara mulus (*seamless white touch*)** dengan kubah tombol QRIS.
  - **Arsitektur Wajib**:
    1. Di `BcaBottomNavBar`:
       ```kotlin
       Box(modifier = Modifier.fillMaxWidth().aspectRatio(if (tightDome) 720f / 174f else 720f / 200f)) {
           Image(
               painter = painterResource(id = navImageRes),
               contentDescription = "BCA Navigation Bar",
               alignment = if (tightDome) Alignment.BottomCenter else Alignment.Center,
               modifier = Modifier.fillMaxSize(),
               contentScale = ContentScale.FillWidth
           )
           // 5 Clickable zones
       }
       ```
    2. Di Layar Menu Kartu (`MInfoMenuScreen.kt`):
       ```kotlin
       Column(modifier = Modifier.fillMaxSize()) {
           BcaSubMenuTopBar(...)
           Column(
               modifier = Modifier
                   .fillMaxWidth()
                   .weight(1f)
                   .offset(y = 5.dp)
                   .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 0.dp)
                   .clip(RoundedCornerShape(14.dp))
                   .background(Color.White)
           ) {
               // Fixed Sticky Header (68dp) + Divider Putih (1dp) + Scrollable Items (weight(1f))
           }
           BcaBottomNavBar(selectedTab = BcaNavTab.NONE, onTabSelected = onTabSelected, tightDome = true, modifier = Modifier.zIndex(1f))
       }
       ```
    3. Formula ini menjamin:
       - Putih kartu menyatu mulus ke bibir kubah QRIS tanpa celah gelap di tengah.
       - Sisi kiri dan kanan tetap mengekspos lengkungan sudut kartu 14dp dan navy background 17dp di atas flat navbar.
       - Saat konten di-scroll, batas kartu putih dan badge bawah **TETAP DIAM MENEMPEL PADA QRIS** tanpa bergerak atau tenggelam ke belakang navbar.
