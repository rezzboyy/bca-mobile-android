# 📦 Paket Komponen: BCA Promo Animated Banner (Drop-in Ready)

Paket ini berisi komponen banner promosi dashboard m-BCA 1 kolom mandiri (*reusable*) lengkap dengan 2 iklan animasi bergantian ("The New Gacha Berhadiah" & "Total 600.000+ Hadiah Siap Dimenangkan!").
Animasi telah dilengkapi fitur **mundur menciut ke belakang (750 ms)** dan **masuk dari kecil mengembang menyembul ke depan (680 ms)** dengan ritme tenang & berwibawa.

---

## 📁 Struktur File dalam Paket Ini

```text
iklan banner/
├── BcaPromoBanner.kt              <- Komponen Jetpack Compose mandiri siap pakai
├── components/
│   └── BcaPromoBanner.kt          <- File komponen dalam folder components
├── res/drawable/
│   ├── banner_base_static.png     <- Layer 1: Frame biru BCA + Tombol "Main & Dapatkan Hadiah >"
│   ├── banner_fg_gacha.png        <- Layer 2: Stiker Iklan 1 ("The New Gacha Berhadiah")
│   └── banner_fg_600k.png         <- Layer 2: Stiker Iklan 2 ("Total 600.000+ Hadiah Siap Dimenangkan!")
├── drawable/                      <- Salinan langsung folder drawable
│   ├── banner_base_static.png
│   ├── banner_fg_gacha.png
│   └── banner_fg_600k.png
├── CARA_PAKAI_CEPAT.txt           <- Petunjuk singkat teks biasa (bisa dibuka langsung di Notepad)
└── README_PANDUAN_LENGKAP.md      <- Panduan lengkap integrasi
```

---

## ⚡ Kata Perintah Cepat untuk Asisten AI (ChatGPT / Gemini / Copilot / Claude)

Kapan pun Anda membuat aplikasi baru atau ingin memasang banner ini lagi, cukup ketik salah satu perintah berikut:

> 💬 **"Pasang komponen BcaPromoBanner dari folder 'iklan banner'"**  
> 💬 **"Pasang banner promo animasi 600k dan gacha BCA dari desktop/flashdisk saya"**

---

## 🚀 Cara Memasang Manual di Aplikasi Baru (3 Langkah Mudah)

### Langkah 1: Salin Gambar ke Proyek Baru
Salin 3 file gambar dari folder `drawable/` ke folder `app/src/main/res/drawable/` aplikasi baru Anda:
- `banner_base_static.png`
- `banner_fg_gacha.png`
- `banner_fg_600k.png`

### Langkah 2: Salin File Komponen
Salin file `BcaPromoBanner.kt` ke dalam folder kode UI aplikasi Anda (misal `com.example.app.ui.components`).  
*Catatan: Ubah baris pertama `package ...` dan import `R` sesuai dengan nama package aplikasi baru Anda jika berbeda.*

### Langkah 3: Panggil Komponen di Layar
Di layar mana pun (misal Dashboard / Home), panggil fungsi berikut:

```kotlin
import com.bca.mobile.ui.components.BcaPromoBanner // Sesuaikan package Anda

// Contoh pemanggilan di dalam Column / LazyColumn:
BcaPromoBanner(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    onClick = {
        // Aksi ketika banner diklik (misal navigasi ke halaman promo / info)
    }
)
```

---

## 🎬 Spesifikasi Animasi

1. **Rasio Tampilan:** `636 : 84` (responsif otomatis menyesuaikan lebar layar smartphone).
2. **Layer 1 (Statis):** Base frame gradien biru BCA + tombol interaktif `"Main & Dapatkan Hadiah >"`.
3. **Layer 2 (Outgoing Transisi):** Iklan lama mundur menciut mengecil ke belakang ($1.00 	o 0.50$, $750	ext{ ms}$).
4. **Layer 3 (Incoming Transisi):** Iklan baru masuk dari kecil ($0.50$), mengembang menyembul ke depan ($0.50 	o 1.025 	o 1.00$, $680	ext{ ms}$).
5. **Denyutan Kalem (2x Sembulan):** Zoom In `1.00` ➔ `1.035` ($2.000	ext{ ms}$) & Zoom Out `1.035` ➔ `1.00` ($2.000	ext{ ms}$) berturut-turut selama 8 detik sebelum berpindah iklan.
