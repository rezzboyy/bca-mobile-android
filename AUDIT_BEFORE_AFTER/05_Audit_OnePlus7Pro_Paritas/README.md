# Audit Paritas UI 100% - OnePlus 7 Pro (`GM1910`)

## Ringkasan Audit dan Optimasi Presisi Tinggi

Berdasarkan permintaan user untuk mengaudit secara mendalam (100% detail meliputi ukuran, penempatan, perbandingan, jarak, dan paritas visual) pada **Menu Login Utama** (`BcaHubScreen.kt` & `MbcaLoginDialog.kt`) dan **Transfer Antar Bank** (`TransferAntarBankScreen.kt`), telah dilakukan audit mikroskopis terhadap target referensi asli BCA Mobile dan live screen capture pada perangkat fisik **OnePlus 7 Pro** (1440x3120, density 4.0x / 640 dpi, kanvas 360dp x 780dp).

---

## 1. Audit & Perbaikan Menu Login Utama (`BcaHubScreen.kt`)
- **Masalah Sebelumnya pada Layar OnePlus**:
  - Pada layar panjang 19.5:9 (780dp height), `Spacer(modifier = Modifier.weight(1f))` di bawah tombol Flazz menyerap seluruh sisa tinggi layar (+276dp), menyebabkan logo, 3-row menu card, dan 3 tombol glossy terdorong terlalu ke atas.
  - Baris ke-3 teks disclaimer regulasi (`BCA merupakan peserta penjaminan LPS.`) terdorong ke bawah navigation bar sistem Android sehingga tidak terlihat (terpotong).
  - Tinggi tombol `GlossyActionButton` sebelumnya 58dp dan `MenuRowCard` 89dp terlalu tinggi dibanding rasio target referensi asli (`hub_ref.png`).
- **Perbaikan yang Diterapkan**:
  - Mengubah distribusi vertikal menjadi proporsional harmonis: `Spacer(weight(0.7f))` di atas logo dan `Spacer(weight(1.0f))` di bawah Flazz.
  - Mengatur `MenuRowCard` height menjadi `76.dp`, ukuran ikon rounded tile `58.dp` (radius 12dp), teks SemiBold/Bold `20.sp`, spacer horizontal `24.dp`.
  - Mengatur `GlossyActionButton` height menjadi `48.dp`, corner radius `14.dp`, text `17.sp`, dan spacing vertikal `10.dp`.
  - Memperbaiki padding disclaimer dengan `.navigationBarsPadding().padding(bottom = 12.dp)` sehingga ke-3 baris disclaimer (`BCA berizin dan diawasi oleh`, `Otoritas Jasa Keuangan & Bank Indonesia.`, `BCA merupakan peserta penjaminan LPS.`) tampil 100% utuh dan presisi di atas tombol navigasi sistem.

---

## 2. Audit & Perbaikan Dialog Kode Akses (`MbcaLoginDialog.kt`)
- **Paritas Terhadap Target Referensi Asli (`ref_084537.png`)**:
  - Dialog width `284.dp` (80% lebar layar).
  - Background panel gradien halus `bg_clean_gradient_gray_panel`.
  - Judul `Kode Akses` bold 17sp `#004B87`.
  - Input box 42dp dengan border 1dp `#BDBDBD`, background putih, placeholder `Input 6 alphanum` 15sp `#9E9E9E`.
  - Tombol sejajar `[ Cancel ]` dan `[ Login ]` height 38dp gradien biru resmi BCA.

---

## 3. Audit & Perbaikan Transfer Antar Bank (`TransferAntarBankScreen.kt` & `BcaSubMenuTopBar.kt`)
- **Masalah Sebelumnya**:
  - Indikator jaringan di Top Bar berwarna biru saat pertama dibuka dan margin kanan Send button 14dp (terlalu jauh dari tepi kanan).
  - Send button di Top Bar sebelumnya berukuran 62x35dp, sedangkan referensi asli adalah 66x38dp dengan margin kanan 6dp.
  - Pada baris `Ke Rekening Tujuan` saat Bank belum dipilih, chevron panah kanan `>` muncul, padahal di target referensi asli BCA Mobile (`WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg`), baris tersebut **tidak memiliki chevron** dan teks judul berada di tengah vertikal baris.
  - Teks subtitle akun `0060387737` pada baris `Dari Rekening` terhimpit ke batas bawah kartu karena `Arrangement.Top` dengan padding tetap.
- **Perbaikan yang Diterapkan**:
  - **Top Bar Presisi**:
    - Top bar container height `76.dp`, padding `start = 12.dp, end = 6.dp`.
    - Indikator lampu jaringan 15x15dp (radius 3dp) hijau stabil (`#8CC63E` / `#75A932`).
    - Send button `66.dp x 38.dp`, corner radius `5.dp`, jarak ke lampu indikator `6.dp`.
  - **Form Card Row Paritas 100%**:
    - Mengubah `verticalArrangement` pada `FormCardRow` menjadi `Arrangement.Center` sehingga baris satu baris (seperti `Ke Rekening Tujuan` atau `Jumlah Uang` awal) terpusat vertikal secara sempurna.
    - Mengubah font judul menjadi `15.sp`, `FontWeight.SemiBold`, `#005CA9`.
    - Padding awal teks `8.dp`, padding akhir chevron `14.dp` (sesuai rasio 28px/656px pada referensi asli).
    - Baris `Ke Rekening Tujuan` kini dinamis: **tanpa chevron dan disabled** jika Bank masih `- PILIH -`, dan baru menampilkan chevron saat Bank telah dipilih (sesuai flow asli BCA).
    - Baris dinamis saat Bank & Rekening dipilih: `Layanan Transfer` (BI FAST >), `Biaya` (2.500 tanpa chevron), `Berita` (>), `Tujuan Transaksi` (>), dan `Dari Rekening` (0060387737).

---

## Daftar Berkas Papan Perbandingan Side-by-Side (Paritas 100%)

Semua foto perbandingan tersimpan di folder ini:
`c:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android\AUDIT_BEFORE_AFTER\05_Audit_OnePlus7Pro_Paritas\`

1. **`01_Perbandingan_Antar_Rekening_Full_Side_By_Side.png`**: Perbandingan layar penuh Transfer Antar Rekening.
2. **`02_Formulir_Transfer_Side_By_Side.png`**: Perbandingan crop formulir transfer antar rekening.
3. **`03_Dialog_Konfirmasi_Transfer_Side_By_Side.png`**: Perbandingan dialog konfirmasi transfer antar rekening.
4. **`04_Dialog_Logout_Side_By_Side.png`**: Perbandingan dialog logout m-BCA.
5. **`05_Kotak_Jumlah_Uang_Zoom_Side_By_Side.png`**: Zoom detail kotak split input nominal Rp.
6. **`06_Top_Bar_Header_Zoom_Side_By_Side.png`**: Zoom detail top bar m-Transfer, lampu sinyal, dan tombol Send.
7. **`07_Perbandingan_Hub_Login_Utama_Side_By_Side.png`**: **Perbandingan Menu Login Utama (Hub)**: Logo, 3-row menu card, 3 action buttons, dan 3 baris legal disclaimer.
8. **`08_Perbandingan_Antar_Bank_Side_By_Side.png`**: **Perbandingan Transfer Antar Bank (State Awal)**: Top bar lampu hijau, Send button 66x38dp, baris Bank (- PILIH - >), Ke Rekening Tujuan (tanpa chevron), Jumlah Uang (>), dan Dari Rekening.
9. **`09_Dialog_Konfirmasi_Antar_Bank_Side_By_Side.png`**: **Perbandingan Dialog Konfirmasi Transfer Antar Bank**: Judul, detail Dari/Ke/Nominal/Biaya/Layanan/Tujuan, dan tombol Cancel/OK.
10. **`10_Formulir_Antar_Bank_Terisi_Side_By_Side.png`**: **Perbandingan Formulir Antar Bank Saat Terisi**: SeaBank, Beni Suryawan, 200.000, BI FAST.
11. **`11_Dialog_Kode_Akses_Login_Side_By_Side.png`**: **Perbandingan Dialog Kode Akses Login**: Judul Kode Akses, kotak Input 6 alphanum, dan tombol Cancel/Login.
