# Task Plan: Implementasi dan Audit Paritas Fitur BCA Mobile

Berdasarkan 4 gambar referensi yang diunggah (`uploaded_media_0` s/d `uploaded_media_3`):

## Daftar Pekerjaan (Tasks)

- [x] **1. Persiapan & Komponen Dialog Presisi (`BcaConfirmationDialog.kt`)**
  - Buat komponen dialog seragam dengan styling 100% identik dengan dialog resmi BCA:
    - Card rounded (`RoundedCornerShape(8.dp)`) dengan border halus (`#CBD5E1`) dan dimensi `280.dp` x `minHeight 320.dp`.
    - Dua tombol aksi berdampingan: **Cancel** dan **OK / Logout** dengan gradient glossy resmi BCA (`#48A5E0` -> `#3B9DDE` -> `#277ABE` -> `#1962A7` -> `#0C4C93`) dan teks putih tebal.
    - Tipografi warna teks biru resmi BCA (`#004B87`).

- [x] **2. Item 1: Dialog Konfirmasi Logout pada Dashboard (`uploaded_media_0`)**
  - Modifikasi event klik ikon logout pada top bar `MbcaDashboardScreen.kt`.
  - Tampilkan dialog konfirmasi:
    - Judul: **Logout** (Tengah, bold biru).
    - Pesan: **Anda akan logout dari BCA mobile** (Tengah, biru).
    - Spacer weight(1f) meninggalkan area kosong tengah yang presisi sesuai referensi asli.
    - Tombol: **Cancel** (menutup dialog) & **Logout** (logout dan kembali ke Hub).
  - Audit visual: Render dialog dan bandingkan dengan `uploaded_media_0` (`audit_board_dialog_logout.png`).

- [x] **3. Item 2: Layar Pemilihan "Layanan Transfer" (`uploaded_media_1`)**
  - Buat screen baru: `LayananTransferScreen.kt`.
  - Top bar: `m-Transfer` teks biru di tengah, indikator hijau di kanan, background metallic.
  - Header: **Layanan Transfer** (Bold biru, 18sp).
  - Card 1: **BI FAST**
    - Minimal Transaksi: `Rp 10,000.00`
    - Biaya: `Rp 2,500.00`
    - Waktu Layanan: `24 Jam`
  - Card 2: **Realtime Online**
    - Minimal Transaksi: `Rp 10,000.00`
    - Biaya: `Rp 6,500.00`
    - Waktu Layanan: `24 Jam`
  - Kolom nilai rata kiri pada kolom kedua (offset ~53% dari lebar card).
  - Interaksi: Memilih opsi akan menyimpan pilihan dan biaya ke `TransferAntarBankScreen`.
  - Audit visual: Render screen dan bandingkan dengan `uploaded_media_1` (`audit_board_layanan_transfer.png`).

- [x] **4. Item 3: Formulir & Dialog Konfirmasi Transfer Antar Bank (`uploaded_media_2`)**
  - Perbarui form `TransferAntarBankScreen.kt` agar urutan dan 8 field-nya identik:
    1. `Bank` (misal: SEABANK >)
    2. `Ke Rekening Tujuan` (misal: 901717125251 - BENI SURYAWAN >)
    3. `Jumlah Uang` (misal: 200,000.00 >)
    4. `Layanan Transfer` (BI FAST >) -> membuka `LayananTransferScreen`
    5. `Biaya` (2.500,00)
    6. `Berita` (opsional >)
    7. `Tujuan Transaksi` (Lainnya >)
    8. `Dari Rekening` (0060387737)
  - Perbarui dialog konfirmasi Transfer Antar Bank:
    - Header: `m-Transfer:`
    - `Dari 0060387737`
    - `Ke Rekening Tujuan`
    - `[BANK]`
    - `[NO_REKENING]`
    - `[NAMA_PENERIMA]`
    - (Spasi)
    - `NOMINAL TRANSFER Rp`
    - `[NOMINAL]` (format `200,000.00`)
    - `BIAYA Rp [BIAYA]` (format `Rp 2,500.00`)
    - `LAYANAN [LAYANAN]`
    - `TUJUAN TRANSAKSI [TUJUAN]`
    - Tombol: **Cancel** & **OK** (keduanya blue gradient).
  - Audit visual: Render form dan dialog, bandingkan dengan `uploaded_media_2` (`audit_board_transfer_antar_bank.png`).

- [x] **5. Item 4: Dialog Konfirmasi Transfer Antar Rekening (`uploaded_media_3`)**
  - Perbarui dialog konfirmasi di `TransferAntarRekeningScreen.kt`:
    - Dimensi: `dialogWidth = 260.dp`, `minHeight = 365.dp`.
    - Judul header dialog: **m-Transfer** (Tengah, bold biru).
    - `m-Transfer:`
    - `[DD/MM/YYYY HH:mm:ss]` (Timestamp otomatis saat transaksi)
    - `Dari [NO_REK_PENGIRIM]`
    - `Ke [NO_REK_TUJUAN]`
    - `[NAMA_PENERIMA]`
    - `Rp [NOMINAL]` (format `Rp 50.000,00`)
    - (Spasi)
    - Disclaimer text:
      `Pastikan nominal dan nomor rekening yang dimasukkan sudah sesuai.`
      `Jika terdapat kesalahan data, maka sepenuhnya menjadi tanggung jawab nasabah.`
    - Tombol: **Cancel** & **OK** (keduanya blue gradient).
  - Audit visual: Render dialog dan bandingkan dengan `uploaded_media_3` (`audit_board_transfer_antar_rekening.png`).

- [x] **6. Audit Visual Akhir & Eksekusi Build / Rilis**
  - Buat script verifikasi before/after side-by-side untuk ke-4 layar/dialog (`generate_audit_boards.py`).
  - Laporan audit visual lengkap diterbitkan pada `audit_transfer_dialogs.md`.
  - Jalankan `./gradlew testDebugUnitTest` (passed).
  - Publikasikan rilis baru via `release.ps1`.
