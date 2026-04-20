# To-Do Planner

<p align="center">
  <img src="app/src/main/res/drawable/task_planner_logo.png" alt="To-Do Planner Logo" width="250" style="background-color: white; padding: 60px 25%; border-radius: 12px;">
</p>

**To-Do Planner** adalah aplikasi pengelola tugas dan jadwal harian bergaya modern yang dirancang untuk mempermudah produktivitas Anda. Dibangun sepenuhnya menggunakan teknologi modern Android (**Jetpack Compose**), aplikasi ini menawarkan pengalaman navigasi dan interaksi antarmuka yang sangat responsif, mulus, dan intuitif.

## Fitur Utama

1. **Navigasi Mulus Berbasis Swipe**
   - Transisi menu antara **Daftar Tugas (Tasks)** dan **Kalender (Calendar)** dapat dilakukan dengan usapan (swipe) layar (menggunakan HorizontalPager).
   - Tampilan Kalender juga mendukung *swipe* ke kanan dan ke kiri untuk berpindah bulan secara instan.

2. **Daftar Tugas Cerdas (Smart Task List)**
   - Daftar tugas secara otomatis dikelompokkan berdasarkan kategori waktu: **Lewat Waktu, Hari Ini, Besok, Lusa, dan Tanpa Tenggat**.
   - Grup **Lewat Waktu** eksklusif hanya menampilkan tugas yang belum diselesaikan (melewati tenggat waktu).
   - Kategori **Tanpa Tenggat** terbuka secara otomatis (expanded by default) agar daftar tugas tak berwaktu bisa langsung terlihat.
   - Pilihan untuk menyembunyikan atau menampilkan daftar tugas yang sudah *Selesai*.

3. **Kalender Interaktif**
   - Tampilan kalender *grid* bulanan dengan indikator visual (titik penanda) pada hari-hari yang memiliki tugas.
   - Menampilkan daftar tugas spesifik pada tanggal yang dipilih.
   - Dilengkapi tombol penambahan instan (+) pada tampilan daftar kalender, yang otomatis mendeteksi tanggal yang sedang dipilih.

4. **Manajemen Tugas Lanjutan (Recurring Tasks)**
   - Mendukung rentang *tenggat waktu* berbasis Tanggal dan Jam secara spesifik.
   - **Tugas Berulang (Repeat)**: Opsi otomatisasi penyalinan tugas secara Harian, Mingguan, Bulanan, maupun **Hari Kustom** (misalnya setiap Senin & Rabu).
   - Kontrol kustom untuk batas waktu berulang (contoh: diulang sebanyak *n* kali, atau otomatis berakhir pada 31 Desember tahun ini).
   - Fitur edit untuk mengubah judul, rincian catatan, atau tanggal tanpa mengganggu perulangan sebelumnya.

5. **Interaksi Modern**
   - Mendukung gesture geser (swipe-to-delete) untuk menghapus tugas, lengkap dengan fitur pembatalan (*Undo*) via Snackbar.
   - Splash Screen estetik di awal peluncuran dengan logo eksklusif aplikasi.
   - Ukuran *Card* tugas lebih ringkas dan hemat ruang layar namun tetap menjaga estetika jarak *(spacing)* tipografi.

## Teknologi & Arsitektur (Tech Stack)

Aplikasi ini diimplementasikan menggunakan arsitektur **MVVM (Model-View-ViewModel)** dengan struktur lapisan (layering) yang optimal:

- **UI Layer**: Jetpack Compose, Material 3, Navigation & Foundation Pager.
- **ViewModel Layer**: State manajemen dinamis dengan StateFlow dan ViewModel.
- **Repository Layer**: Lapisan abstraksi data (TaskRepository).
- **Data Layer**: Room Database (SQLite) terintegrasi menggunakan Coroutines yang menangani pengelolaan database relasional secara lokal (Local Storage) dan aman.

## Persyaratan Sistem

- Diperlukan minimum versi **SDK 24 (Android 7.0 Nougat)**.
- Desain aplikasi dioptimalkan untuk bahasa antarmuka **Indonesia**.

---
*Dibuat untuk evaluasi ETS Pemrograman Perangkat Bergerak. Selamat mengatur jadwal dan tingkatkan produktivitas harian Anda.*
