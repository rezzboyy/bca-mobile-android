# Laporan Audit Paritas 100% - Pembesaran Skala Penuh 1800 px & Penyelarasan Posisi 100% Pas di Tengah (Banner 600k Hadiah myBCA)

Dokumen ini memuat evaluasi, perhitungan matematis, dan hasil kalibrasi pembesaran stiker promosi **"Total 600.000+ Hadiah Siap Dimenangkan!"** pada aplikasi Android Native **BCA Mobile** (`App Android/Bca Mobile Android`). Sesuai arahan lanjutan pengguna (*"sedikiy lagi"*), dimensi teks dibesarkan sedikit lagi dari `1750 px` menjadi **`1800 px`** (`1798 px` terukur presisi) dengan mempertahankan posisi **100% tepat di tengah kartu (*dead center*)** secara simetris sempurna (`373 px == 373 px`).

---

## 🎯 1. Ringkasan Permintaan & Analisis Kalibrasi

### 🔍 Kebutuhan Pengguna:
- Teks iklan promosi *"Total 600.000+ Hadiah Siap Dimenangkan!"* pada iterasi sebelumnya telah berukuran `1750 px` (margin `398 px`).
- Pengguna meminta agar teks **"sedikit lagi"** dibesarkan agar tipografi 3D tampil lebih megah, tebal, dan padat memenuhi area banner layaknya versi promosi resmi BCA Mobile, namun **tetap berada pas di tengah** secara simetris tanpa menabrak tombol pill aksi di sebelah kanan maupun maskot di sebelah kiri.

---

## 📐 2. Solusi & Perhitungan Geometris Presisi (1800 px Pas di Tengah)

1. **Dimensi Kanvas Master:** `2544 x 336 px` (rasio baku `636:84`).
2. **Titik Pusat Kanvas (50% Canvas Axis):** `2544 / 2 = 1272.0 px`.
3. **Pelebaran & Pembesaran Dimensi Teks:**
   - Lebar baru: **`1798 px`** (skala `1800 px`, bertambah `+50 px` dari versi `1750 px` atau `+258 px` dari versi awal `1540 px`).
   - Tinggi baru: **`109 px`** (sebelumnya `106 px` / `101 px` / `98 px`, rasio aspek baku `16.25:1` tetap presisi).
4. **Koordinat Penempatan Teks Simetris:**
   - Setengah lebar (`half_w`): `1798 / 2 = 899 px`.
   - Titik awal teks (`x_start`): `1272 - 899 =` **`373 px`**
   - Titik akhir teks (`x_end`): `1272 + 899 =` **`2171 px`**
5. **Hasil Pengukuran Jarak:**
   - **Jarak Kiri (ke tepi kanvas `x = 0`):** `373 - 0 =` **`373 px`**
   - **Jarak Kanan (ke tepi kanvas `x = 2544`):** `2544 - 2171 =` **`373 px`**
   - **Status Kesetaraan:** **`Jarak Kiri (373 px) == Jarak Kanan (373 px)`** 👉 **100% PERSIS PAS DI TENGAH!**
6. **Jarak Bebas ke Elemen Sekitar (Keseimbangan Optis & Anti-Tabrakan):**
   - **Ke Maskot / Logo myBCA (kiri, `x = 350`):** `373 - 350 =` **`23 px`**
   - **Ke Bodi Tombol Pill Aksi (kanan, bodi padat `x = 2200`):** `2200 - 2171 =` **`29 px`**
   - *Keseimbangan Optis:* Celah bebas visual ke maskot kiri (`23 px`) dan bodi tombol kanan (`29 px`) berada dalam deviasi **hanya 6 px**, menghasilkan harmoni visual yang luar biasa seimbang.
   - **Anti-Tabrakan:** Teks tidak menimpa bodi tombol aksi di sebelah kanan (jarak bersih `29 px`), bebas tabrakan 100%.
7. **Penyelarasan Vertikal:** Sumbu `y = 139.0 px` berhimpit presisi dengan pusat vertikal tombol pill kanan (`y = 139.0 px`).

---

## 📊 3. Tabel Metrik Evolusi Skala Teks

| Parameter Ukur | Iterasi 1 (1540 px) | Iterasi 2 (1600 px) | Iterasi 3 (1680 px) | Iterasi 4 (1750 px) | Iterasi 5 (1800 px - Terbaru) | Evaluasi & Status |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| **Lebar Teks** | `1540 px` | `1600 px` | `1678 px` | `1748 px` | **`1798 px` (~1800 px)** | **Dibesarkan sedikit lagi (+50 px)** |
| **Tinggi Teks** | `95 px` | `98 px` | `101 px` | `106 px` | **`109 px`** | **Maksimal dominan & gagah** |
| **Pusat Horisontal** | `x = 1272.0 px` | `x = 1272.0 px` | `x = 1272.0 px` | `x = 1272.0 px` | **`x = 1272.0 px` (50.0%)** | **100% Tetap Pas di Tengah** |
| **Koordinat Horisontal** | `502 .. 2042` | `472 .. 2072` | `433 .. 2111` | `398 .. 2146` | **`373 .. 2171`** | **Simetris Kiri & Kanan** |
| **Jarak dari Tepi Kiri** | `502 px` | `472 px` | `433 px` | `398 px` | **`373 px`** | **Sama persis dengan kanan** |
| **Jarak dari Tepi Kanan** | `502 px` | `472 px` | `433 px` | `398 px` | **`373 px`** | **Sama persis dengan kiri** |
| **Selisih Margin** | `0 px` | `0 px` | `0 px` | `0 px` | **`0 px`** | **100% Dead Center (373 px == 373 px)** |
| **Jarak ke Bodi Tombol** | `158 px` | `128 px` | `89 px` | `54 px` | **`29 px`** | **Ruang Aman Bebas Tabrakan** |
| **Jarak ke Maskot Kiri** | `152 px` | `122 px` | `83 px` | `48 px` | **`23 px`** | **Harmonis & Seimbang (deviasi 6 px)** |
| **Render Edge Quality** | SDF Lanczos 4x | SDF Lanczos 4x | SDF Lanczos 4x | SDF Lanczos 4x | **SDF Lanczos 4x Luminous White** | **Pristine Tanpa Gray Halo** |

---

## 🖼️ 4. Berkas Papan Komparasi Audit Visual

Seluruh papan bukti visual telah diperbarui dan disimpan pada direktori:  
📁 `C:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android\AUDIT_BEFORE_AFTER\18_Audit_Update_Banner_600k_Hadiah_Dari_myBCA\`

1. **`02_Pengukuran_Presisi_Jarak_Kiri_dan_Kanan_Pas_di_Tengah.png`**  
   Diagram teknis dengan garis ukur (*dimension bracket*) dan garis tengah sumbu (*red center line `x = 1272.0 px`*), membuktikan secara visual & matematis bahwa `Jarak Kiri (373 px) == Jarak Kanan (373 px)` dengan lebar teks `1798 px`.
2. **`03_Perbandingan_Banner_Full_Sebelum_vs_Sesudah.png`**  
   Perbandingan langsung banner penuh antara versi 1750 px (margin 398 px) versus versi 1800 px (margin 373 px).
3. **`04_Perbandingan_Layar_Dashboard_Full_Sebelum_vs_Sesudah.png`**  
   Perbandingan tampilan layar dashboard BCA Mobile secara penuh.
4. **`05_Zoom_Mikroskopis_Detail_Ujung_Teks_Bebas_Tabrakan.png`**  
   Zoom detail subpixel pada area ujung teks `"Dimenangkan!"` dan tombol pill, memperlihatkan jarak aman bebas tabrakan (29 px).

---

## ✅ 5. Status Verifikasi Teknis

- [x] Teks dibesarkan menjadi `1800 px` (`1798 px` terukur presisi).
- [x] Teks tetap 100% berada pada sumbu tengah kanvas (`x = 1272.0 px`).
- [x] Jarak tepi kiri (`373 px`) dan tepi kanan (`373 px`) sama persis (`selisih = 0 px`).
- [x] Kanvas master `banner_fg_600k.png` (`2544 x 336 px`, rasio `636:84`) telah diperbarui.
- [x] Sinkronisasi ke folder `app/src/main/res/drawable/` dan paket mandiri `iklan banner/` selesai 100%.
- [x] Fallback composite `banner_hadiah_600k.png` (636 x 84 px) telah diperbarui.
- [x] Ruang bebas ke bodi tombol kanan `29 px` dan ke maskot kiri `23 px` (zero collision).
