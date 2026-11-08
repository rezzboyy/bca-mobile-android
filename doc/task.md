# Roadmap & Task Breakdown (BCA Mobile Android Native)

Dokumen ini memuat daftar tugas terperinci, tahapan eksekusi, dan pelacakan progres pengembangan aplikasi **BCA Mobile Android** dengan teknologi **Kotlin + Jetpack Compose** (Android Native murni).

---

## Status Progres
- [ ] Belum Dimulai
- [/] Sedang Dikerjakan
- [x] Selesai

> [!NOTE]
> Proyek ini dibangun sebagai konversi native murni dari prototipe web dan referensi resmi BCA Mobile.
> Pembaruan tanggal **08 Nov 2026** memfokuskan pada penyempurnaan menyeluruh modul **m-Transfer** dan penambahan layar-layar baru yang belum ada berdasarkan 5 foto referensi resmi terbaru (*WhatsApp Images*).

---

## 📸 Referensi Screenshot Baru (08 Nov 2026) & Analisis Kebutuhan

| No | File Screenshot | Tampilan / Fitur Nyata | Status di Proyek | Action Plan |
| :---: | :--- | :--- | :--- | :--- |
| **1** | `WhatsApp Image 2026-11-08 at 18.00.21.jpeg` | **Daftar Transfer -> Antar Rekening**<br>• Header: `m-Transfer`, lampu hijau, tombol `Send`<br>• Subtitle luar: `No.Rekening Tujuan`<br>• Card 3 baris: `Rekening 1` >, `Rekening 2` >, `Rekening 3` > | **Belum Ada** (sebelumnya hanya dialog pop-up) | Buat layar baru `DaftarTransferAntarRekeningScreen.kt` lengkap dengan alur input 3 rekening, tombol Send, konfirmasi, PIN, dan simpan ke database. |
| **2** | `WhatsApp Image 2026-11-08 at 18.00.21 (1).jpeg` | **Transfer -> Antar Rekening**<br>• Header: `m-Transfer`, lampu hijau, tombol `Send`<br>• Baris 1: `Dari Rekening` (`0060387737`)<br>• Baris 2: `Ke Rekening` (`- PILIH -` >)<br>• Baris 3: `Jumlah Uang` (Split: `Rp` > \| >)<br>• Baris 4: `Berita` > | **Belum Sesuai** (sebelumnya keliru menampilkan Rekening 1,2,3 di form transfer ini) | Rekonstruksi `TransferAntarRekeningScreen.kt` agar 100% presisi sesuai gambar referensi asli. |
| **3** | `WhatsApp Image 2026-11-08 at 18.00.20.jpeg` | **Pemilih Rekening Tujuan (Account Picker)**<br>• Header: `m-Transfer`, lampu hijau<br>• Toggle: `Rekening Sendiri` vs `Daftar Transfer`<br>• Search bar: `Search` dengan icon 🔍<br>• Daftar Kontak: Nama kapital biru tebal + Nomor rekening hitam | **Belum Ada** (sebelumnya hanya dialog pop-up sederhana) | Buat layar pemilih rekening `TransferRecipientPickerScreen.kt` dengan filter realtime dan dataset asli (`ABDILLAH`, `ACHMAD RIZAL`, dll). |
| **4** | `WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg` & `(2).jpeg` | **Transfer -> Antar Bank**<br>• Header: `m-Transfer`, lampu hijau, tombol `Send`<br>• Baris 1: `Bank` (`- PILIH -` >)<br>• Baris 2: `Ke Rekening Tujuan`<br>• Baris 3: `Jumlah Uang` ><br>• Baris 4: `Dari Rekening` (`0060387737`) | **Perlu Penyesuaian** (baris Dari Rekening sebelumnya terjejal saldo panjang dan urutan perlu disesuaikan) | Sempurnakan `TransferAntarBankScreen.kt` agar 4 baris tampil rapi dan presisi sesuai gambar. |
| **5** | *Pelengkap Sistem Transfer* | **Daftar Transfer -> Antar Bank**<br>• Pendaftaran nomor rekening bank lain baru ke daftar transfer sebelum transfer antar bank dilakukan | **Belum Ada** (sebelumnya hanya dialog alert) | Buat layar `DaftarTransferAntarBankScreen.kt` lengkap dengan pemilih bank, input rekening, Send, PIN, dan penyimpanan. |

---

## 🎯 Task Breakdown Baru: Refactoring & Pemenuhan Modul m-Transfer (Milestone 8)

### Sub-Fase 8.1: Penyiapan Mock Data & Logika Repository (`BcaRepository.kt`)
- [x] **Task 8.1.1**: Update default `accountNumber` pengirim menjadi `0060387737` agar 100% identik dengan foto referensi.
- [x] **Task 8.1.2**: Perbarui daftar kontak `recipients` default dengan nama & nomor rekening dari foto Image 2:
  - `ABDILLAH` - `2181475538` (BCA)
  - `ACHMAD RIZAL` - `2481574694` (BCA)
  - `ADE FERY PERMANA` - `6520643140` (BCA)
  - `ADE JAJANG NURJAMAN` - `2990889042` (BCA)
  - `BUDI SANTOSO` - `0881234567` (BCA)
  - Bank Lain: `HENDRA WIJAYA` - `1400012345678` (MANDIRI), `DEWI LESTARI` - `001201998877501` (BRI).
- [x] **Task 8.1.3**: Tambahkan metode `registerBcaRecipients(list: List<String>)` dan `registerBankRecipient(bank: String, accNo: String, name: String)` di `BcaRepository`.

### Sub-Fase 8.2: Implementasi Layar Pemilih Rekening Tujuan (`TransferRecipientPickerScreen.kt`)
- [x] **Task 8.2.1**: Buat Composable `TransferRecipientPickerScreen` sesuai Image 2 (`WhatsApp Image 2026-11-08 at 18.00.20.jpeg`):
  - Top Bar m-Transfer dengan lampu hijau.
  - Segmented control: `Rekening Sendiri` (unselected) dan `Daftar Transfer` (selected biru BCA).
  - Search Box dengan icon kaca pembesar dan filter live query.
  - Kartu putih rounded dengan list nama (Biru tebal kapital) dan nomor rekening (Hitam).
  - Callback pemilihan rekening untuk dikembalikan ke form transfer.

### Sub-Fase 8.3: Implementasi Layar Pendaftaran Rekening Baru (`DaftarTransferAntarRekeningScreen.kt`)
- [x] **Task 8.3.1**: Buat Composable `DaftarTransferAntarRekeningScreen` sesuai Image 5 (`WhatsApp Image 2026-11-08 at 18.00.21.jpeg`):
  - Top Bar m-Transfer dengan lampu indikator hijau dan tombol glossy 3D `Send`.
  - Teks judul luar: `No.Rekening Tujuan` (warna biru BCA).
  - Kartu putih 3 baris input: `Rekening 1` >, `Rekening 2` >, `Rekening 3` >.
  - Dialog input nomor rekening saat baris ditekan (atau input langsung).
  - Validasi & integrasi tombol `Send` -> Dialog Konfirmasi -> Dialog PIN m-BCA -> Pendaftaran sukses.

### Sub-Fase 8.4: Restrukturisasi & Penyempurnaan Layar Transfer Antar Rekening (`TransferAntarRekeningScreen.kt`)
- [x] **Task 8.4.1**: Rombak struktur form di `TransferAntarRekeningScreen.kt` sesuai Image 3 (`WhatsApp Image 2026-11-08 at 18.00.21 (1).jpeg`):
  - Row 1: `Dari Rekening` -> `0060387737`.
  - Row 2: `Ke Rekening` -> Menampilkan `- PILIH -` dengan chevron `>`, klik membuka `TransferRecipientPickerScreen`.
  - Row 3: `Jumlah Uang` -> Split item: container kiri abu-abu bertuliskan `Rp` dengan chevron `>`, dan kolom kanan nominal uang dengan chevron `>`. Klik membuka `BcaAmountInputDialog`.
  - Row 4: `Berita` -> Baris berita transfer dengan chevron `>`.
  - Tombol `Send` -> Validasi data -> Dialog Konfirmasi transfer -> Dialog PIN 6-digit -> Bukti Transfer Digital Resmi (`BcaReceiptDialog`).

### Sub-Fase 8.5: Penyempurnaan Layar Transfer Antar Bank (`TransferAntarBankScreen.kt`)
- [x] **Task 8.5.1**: Perbarui layout `TransferAntarBankScreen.kt` sesuai Image 1 & 4 (`WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg`):
  - Row 1: `Bank` -> `- PILIH -` > (membuka `BcaBankSelectionDialog`).
  - Row 2: `Ke Rekening Tujuan` -> Membuka daftar rekening bank penerima.
  - Row 3: `Jumlah Uang` > -> Membuka `BcaAmountInputDialog`.
  - Row 4: `Dari Rekening` -> Menampilkan `0060387737` (bersih tanpa format saldo bertumpuk).
  - Tombol `Send` -> Konfirmasi -> PIN -> Resi Transfer resmi.

### Sub-Fase 8.6: Implementasi Layar Pendaftaran Rekening Bank Lain (`DaftarTransferAntarBankScreen.kt`)
- [x] **Task 8.6.1**: Buat Composable `DaftarTransferAntarBankScreen`:
  - Form input: Pilih Bank tujuan (`- PILIH -` >) dan input `No.Rekening Tujuan`.
  - Tombol `Send` -> Konfirmasi nama penerima mock -> Dialog PIN -> Simpan ke database daftar transfer bank lain.

### Sub-Fase 8.7: Integrasi Navigasi & Routing (`MTransferMenuScreen.kt` & `MainActivity.kt`)
- [x] **Task 8.7.1**: Hubungkan menu `Daftar Transfer` -> `Antar Rekening` ke `DaftarTransferAntarRekeningScreen`.
- [x] **Task 8.7.2**: Hubungkan menu `Daftar Transfer` -> `Antar Bank` ke `DaftarTransferAntarBankScreen`.
- [x] **Task 8.7.3**: Hubungkan menu `Transfer` -> `Antar Rekening` dan `Antar Bank` dengan layar pemilih rekening penerima baru.
- [x] **Task 8.7.4**: Daftarkan seluruh rute layar baru di sealed class `Screen` pada `MainActivity.kt`.

### Sub-Fase 8.8: Verifikasi, Audit Kompilasi & Build Release
- [x] **Task 8.8.1**: Jalankan unit test dan compile Android (`./gradlew testDebugUnitTest` / `./gradlew assembleDebug`).
- [x] **Task 8.8.2**: Pastikan 0 lint error dan seluruh alur interaktif bekerja mulus.

---

## Riwayat Fase Sebelumnya (Fondasi & Rilis)

### Fase 1: Inisialisasi Proyek & Fondasi Arsitektur
- [x] Inisialisasi Jetpack Compose, Material 3, Clean Architecture, Gradle 8.x + JDK 17.

### Fase 2: Migrasi Aset Visual & Design System
- [x] Wallpaper starburst navy, icon m-BCA, KlikBCA, myBCA, palet warna resmi BCA.

### Fase 3: Layar Utama Launcher (Hub Screen)
- [x] Container adaptive layout, tombol 3 baris (m-BCA, KlikBCA, myBCA), tombol 3D glossy Buka Rekening & Flazz.

### Fase 4: Dashboard m-BCA & Modul Finansial Awal
- [x] Dialog Kode Akses alfanumerik 6 karakter.
- [x] Header online indicator, greeting Dadang Mubarok, banner promo anti-flicker, grid 11 menu, 5-tab Bottom Nav.
- [x] Modul m-Info (Info Saldo 1 baris presisi, Mutasi Rekening, Info Kurs).
- [x] Modul Cardless (Tarik tunai ATM 6-digit & countdown 60 menit).
- [x] Modul Notifikasi & Akun Saya.

### Fase 5: Modul Flazz & Simulasi Chip NFC
- [x] Radar scanning animation, simulasi sensor NFC, pengecekan saldo aktif & mutasi tap Flazz.

### Fase 6: Keamanan Data Lokal & Enkripsi
- [ ] Hardware Keystore & EncryptedSharedPreferences.
- [ ] Idle session auto-logout timer.

### Fase 7: Pengujian & Infrastruktur Distribusi OTA
- [x] Keystore rilis permanen `bca_release.jks` (27+ tahun).
- [x] `GitHubUpdateManager` & `BcaUpdateDialog` auto-update Over-The-Air via GitHub Releases.
- [x] Skrip rilis 1-klik `release.ps1` dan publikasi v1.0.1 & v1.0.2.
