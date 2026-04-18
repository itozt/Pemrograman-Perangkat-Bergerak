# Panduan Menjalankan Aplikasi To-Do List Personal di Android Studio

## ✅ Prasyarat
1. **Android Studio** sudah terinstall (versi terbaru recommended)
2. **JDK 11** atau lebih baru
3. **Android SDK** sudah terinstall (API level 24 - 35)
4. **Emulator Android** atau device fisik (dengan USB Debugging)

## 🚀 Langkah-Langkah Menjalankan Aplikasi

### Metode 1: Menggunakan Android Studio UI (Recommended)

#### 1. Buka Project di Android Studio
```
File → Open → D:\Kuliah\Semester 6\Pemrograman Perangkat Bergerak\UTS
```

#### 2. Sync Gradle Files
- Android Studio akan otomatis menawarkan untuk sync
- Jika tidak, klik: `File → Sync Now`
- Tunggu hingga proses selesai (biasanya 2-5 menit)

#### 3. Buat/Pilih Emulator
- Klik: `Tools → AVD Manager`
- Jika sudah ada emulator, pilih salah satu
- Jika tidak ada, klik `Create Virtual Device`:
  - Pilih device (misalnya: Pixel 5)
  - Pilih API Level (recommended: API 31-33)
  - Finish dan jalankan emulator

#### 4. Jalankan Aplikasi
- Tekan `Shift + F10` atau klik tombol `Run` (►)
- Pilih emulator atau device fisik
- Tunggu build selesai dan aplikasi launch di emulator

### Metode 2: Menggunakan Terminal/Command Prompt

#### 1. Masuk ke folder project
```bash
cd "D:\Kuliah\Semester 6\Pemrograman Perangkat Bergerak\UTS"
```

#### 2. Sync Gradle
```bash
gradlew.bat sync
```

#### 3. Build APK Debug
```bash
gradlew.bat build
```

#### 4. Install dan Jalankan di Emulator
Pastikan emulator sudah running, kemudian:
```bash
gradlew.bat installDebug
```

Atau langsung:
```bash
gradlew.bat run
```

## 🧪 Testing Aplikasi

### Test Case 1: Membuat Task Baru
1. Tekan tombol `+` (FAB) di bottom-right
2. Isi form:
   - Title: "Belajar Kotlin"
   - Category: Pilih "Work" atau buat baru
   - Due Date: Pilih tanggal hari ini
   - Due Time: Set waktu
   - Reminder: Aktifkan dan set waktu
   - Repeat: Pilih "Daily"
   - Notes: Tambahkan catatan
3. Tekan "Save"
4. Verifikasi task muncul di list Today

### Test Case 2: Membuat Kategori Baru
1. Buka menu "Groups" dari drawer (hamburger menu)
2. Tekan tombol "+ Create New Group"
3. Masukkan nama kategori (misal: "Hobi")
4. Tekan "Create"
5. Verifikasi kategori muncul di list

### Test Case 3: Filter Tasks by Kategori
1. Di menu Tasks, scroll kategori di bagian atas
2. Klik kategori yang berbeda
3. Verifikasi task list berubah sesuai filter

### Test Case 4: Toggle Task Completion
1. Klik checkbox di sebelah task
2. Verifikasi task pindah ke "Completed Today" section
3. Buka calendar view, verifikasi status juga update

### Test Case 5: Edit Task
1. Klik pada salah satu task di list
2. Ubah beberapa field
3. Tekan "Save"
4. Verifikasi perubahan tersimpan

### Test Case 6: Delete Task
1. Buka task detail (klik task di list)
2. Tekan tombol "Delete"
3. Verifikasi task hilang dari list

### Test Case 7: Calendar Navigation
1. Klik "Calendar" di bottom navigation
2. Klik tanggal berbeda di kalender
3. Verifikasi task list update untuk tanggal terpilih

### Test Case 8: Theme Switching
1. Buka drawer menu (hamburger icon)
2. Klik "Theme"
3. Pilih theme berbeda (Teal, Blue, Green)
4. Klik back
5. Verifikasi warna toolbar berubah

### Test Case 9: Dark Mode
1. Buka drawer menu
2. Klik "Settings"
3. Toggle "Dark Mode"
4. Verifikasi tampilan berubah ke dark mode

### Test Case 10: Notifications Toggle
1. Buka drawer menu
2. Klik "Settings"
3. Toggle "Notifications"
4. Verifikasi setting tersimpan

## 🐛 Troubleshooting

### Error: "Gradle sync failed"
**Solusi:**
- Buka `File → Settings → Appearance & Behavior → System Settings → Android SDK`
- Pastikan SDK dan Build Tools sudah terinstall
- Klik `Apply → OK`
- Coba sync lagi: `File → Sync Now`

### Error: "Emulator not found"
**Solusi:**
- Buka Android Studio
- Buka AVD Manager: `Tools → AVD Manager`
- Klik `Create Virtual Device`
- Ikuti wizard untuk membuat emulator baru

### Error: "Build Failed"
**Solusi:**
- Bersihkan build cache: `Build → Clean Project`
- Rebuild: `Build → Rebuild Project`
- Jika masih error, cek error message di Build window

### Aplikasi crash saat launch
**Solusi:**
- Cek logcat: `View → Tool Windows → Logcat`
- Cari error message (biasanya merah)
- Pastikan semua dependencies sudah installed di gradle

### Database tidak muncul data setelah buat task
**Solusi:**
- Data tersimpan di database lokal
- Coba buat beberapa task
- Jika masih tidak muncul, cek logcat untuk error database

## 📱 Menjalankan di Device Fisik

### Prasyarat:
- Device sudah connect ke PC via USB
- USB Debugging sudah diaktifkan di device

### Langkah:
1. Device akan muncul di dropdown device selector
2. Pilih device dari dropdown
3. Tekan tombol Run (atau Shift + F10)

## 🔧 Opsi Build Variants

Di Android Studio:
- **Debug** (default) - Untuk development dan testing
- **Release** - Untuk production (memerlukan signing key)

## 📊 Performance Tips
- Jika aplikasi lag, coba dengan API level lebih tinggi di emulator
- Berikan lebih banyak RAM ke emulator: `AVD Manager → Edit → Advanced Settings → RAM`
- Gunakan **Snapshots** untuk emulator (lebih cepat boot)

## 📝 Notes
- Database akan tersimpan di device storage
- Semua data persistent dan bertahan setelah close app
- Theme preferences disimpan di SharedPreferences
- Jika ingin reset data, hapus app dan reinstall

## 🎓 Learning Resources
- Android Lifecycle: https://developer.android.com/guide/components/activities/activity-lifecycle
- SQLite: https://developer.android.com/training/data-storage/sqlite
- RecyclerView: https://developer.android.com/guide/topics/ui/layout/recyclerview
- Material Design: https://material.io/design

---

**Selamat mencoba! Jika ada error, cek logcat dan build output untuk detail error message.**
