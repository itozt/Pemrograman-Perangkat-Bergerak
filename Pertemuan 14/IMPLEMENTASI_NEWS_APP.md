# LANGKAH-LANGKAH IMPLEMENTASI NEWS APP DI ANDROID STUDIO

Berikut adalah panduan lengkap untuk mengimplementasikan NEWS APP yang telah dibuat. Semua file dan konfigurasi sudah siap di project Anda.

---

## DAFTAR FILE YANG TELAH DIBUAT

### 1. **Konfigurasi Dependencies**
- `build.gradle.kts (Module: app)` - Berisi semua library yang diperlukan

### 2. **Data Models (Package: data)**
- `Article.kt` - Data class untuk artikel berita
- `NewsResponse.kt` - Data class untuk response API

### 3. **API Layer (Package: api)**
- `NewsApiService.kt` - Interface Retrofit untuk API endpoints
- `RetrofitInstance.kt` - Singleton untuk konfigurasi Retrofit

### 4. **Model & Repository (Package: model)**
- `NewsRepository.kt` - Class untuk mengambil data dari API

### 5. **ViewModel (Package: viewmodel)**
- `NewsViewModel.kt` - ViewModel dengan StateFlow untuk state management
  - Berisi sealed class `NewsUiState` (Loading, Success, Error)
  - Method: `loadTopHeadlines()`, `searchNews()`, `retryLoadNews()`

### 6. **UI Screens (Package: ui/screen)**
- `HomeNewsScreen.kt` - Halaman utama dengan list berita & pull-to-refresh
- `DetailNewsScreen.kt` - Halaman detail artikel lengkap
- `SearchNewsScreen.kt` - Halaman pencarian berita dengan search bar

### 7. **UI Components (Package: ui/component)**
- `NewsCard.kt` - Reusable card component untuk menampilkan artikel

### 8. **Navigation (Package: ui/navigation)**
- `NewsAppNavigation.kt` - NavHost dengan 3 routes:
  - Home → Detail
  - Home → Search → Detail
  - Menggunakan Gson untuk serialisasi Article saat navigation

### 9. **Activity & Manifest**
- `MainActivity.kt` - Updated dengan NavController dan NewsAppNavigation
- `AndroidManifest.xml` - Added INTERNET permission

---

## LANGKAH-LANGKAH UNTUK MENJALANKAN APLIKASI

### Step 1: Sinkronisasi Gradle
1. Di Android Studio, buka menu **File** → **Sync Now**
2. Tunggu hingga semua dependencies selesai diunduh
3. Pastikan tidak ada error di Gradle sync

### Step 2: Build Project
1. Tekan **Ctrl + F9** atau menu **Build** → **Make Project**
2. Tunggu hingga build selesai
3. Pastikan tidak ada error di Build Output

### Step 3: Run Aplikasi
**Option 1: Menggunakan Emulator**
1. Buka **Device Manager** di Android Studio (Tools → Device Manager)
2. Pilih atau buat emulator (minimum Android API 24)
3. Jalankan emulator
4. Tekan **Shift + F10** atau menu **Run** → **Run 'app'**
5. Pilih emulator yang sudah running

**Option 2: Menggunakan Physical Device**
1. Sambungkan device Android ke komputer via USB
2. Enable USB Debugging di device (Settings → Developer Options)
3. Tunggu device terdeteksi di Android Studio
4. Tekan **Shift + F10** atau menu **Run** → **Run 'app'**
5. Pilih device Anda

### Step 4: Testing Aplikasi
Setelah aplikasi terbuka, Anda bisa test fitur-fitur berikut:

1. **Home Screen**
   - Daftar berita terbaru sudah ter-load
   - Pull down untuk refresh (Pull-to-Refresh)
   - Klik icon search di top-right untuk buka halaman Search

2. **Detail Screen**
   - Klik salah satu artikel untuk melihat detail lengkap
   - Lihat gambar besar, judul, author, deskripsi, dan konten
   - Tekan tombol "Buka Artikel Lengkap" untuk buka di browser
   - Tekan back arrow untuk kembali

3. **Search Screen**
   - Ketik keyword untuk mencari berita
   - Hasil pencarian akan ter-update real-time
   - Klik artikel untuk melihat detail
   - Tekan back untuk kembali ke Home

4. **Loading & Error States**
   - Saat data dimuat, akan tampil loading indicator
   - Jika terjadi error, akan tampil pesan error dengan tombol Retry
   - Tekan Retry untuk mencoba memuat ulang

---

## ARSITEKTUR & FLOW DATA

```
User (UI Layer)
    ↓
HomeNewsScreen / SearchNewsScreen / DetailNewsScreen
    ↓
NewsViewModel (State Management)
    ↓
NewsRepository (Data Access)
    ↓
Retrofit (HTTP Client)
    ↓
NewsAPI.org (REST API Server)
```

### State Management Flow:
```
NewsViewModel.loadTopHeadlines()
    ↓
Repository.getTopHeadlines()
    ↓
RetrofitInstance.newsApiService.getTopHeadlines()
    ↓
Update MutableStateFlow<NewsUiState>
    ↓
UI re-render dengan data baru
```

---

## API ENDPOINTS YANG DIGUNAKAN

### 1. Get Top Headlines
```
Endpoint: GET /top-headlines
Parameters:
  - country: "us" (default)
  - apiKey: "d900e6bd60954e4aa84ffdc5314bfc30"
Response: NewsResponse dengan list Article
```

### 2. Search News
```
Endpoint: GET /everything
Parameters:
  - q: keyword pencarian
  - sortBy: "publishedAt" (default)
  - apiKey: "d900e6bd60954e4aa84ffdc5314bfc30"
Response: NewsResponse dengan list Article hasil pencarian
```

---

## FITUR YANG SUDAH DIIMPLEMENTASI

✅ HomeNews - Menampilkan daftar berita terbaru dari API
✅ DetailNews - Menampilkan detail berita lengkap
✅ SearchNews - Mencari berita berdasarkan keyword
✅ Pull To Refresh - Menarik layar untuk memuat ulang berita
✅ Loading State - Menampilkan indikator saat data dimuat
✅ Error State - Menampilkan pesan jika terjadi kesalahan
✅ Navigation - Perpindahan antar halaman dengan aman
✅ Material Design 3 - UI modern dan responsive

---

## TROUBLESHOOTING

### Build Error: "Unresolved reference"
- Tekan **Ctrl + F9** untuk rebuild project
- Pastikan Gradle sync berhasil
- Clean project: **Build** → **Clean Project**

### Runtime Error: Network Exception
- Pastikan Internet permission sudah ditambahkan di AndroidManifest.xml ✓
- Periksa koneksi internet Anda
- Pastikan API Key valid (sudah diupdate di NewsRepository.kt)

### Aplikasi Crash di DetailNews
- Periksa URL article valid
- Pastikan Gson serialization berjalan dengan baik
- Lihat Logcat untuk error detail (View → Tool Windows → Logcat)

### Loading tidak pernah selesai
- Periksa Logcat untuk melihat error dari API
- Pastikan API Key valid dan tidak expired
- Coba gunakan VPN jika API di-block oleh region

### Image tidak tampil
- Periksa URL image valid
- Coil library sudah di-import dengan benar
- Pastikan Internet permission sudah aktif

---

## VERSI & REQUIREMENTS

- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 9.0.1
- **Java Compatibility**: Java 11
- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 24 (Android 7.0)
- **Jetpack Compose BOM**: 2024.09.00

---

## CATATAN PENTING

1. **API Key**: Sudah dimasukkan di `NewsRepository.kt`. Jika ingin menggantinya, edit variable `apiKey` di class `NewsRepository`.

2. **Network Timeout**: Default timeout adalah 30 detik. Bisa diubah di `RetrofitInstance.kt` jika diperlukan.

3. **Pull-to-Refresh**: Hanya tersedia di Home screen untuk merefresh daftar berita terbaru.

4. **Search Functionality**: 
   - Ketika search field kosong, akan menampilkan daftar berita terbaru
   - Ketika ada keyword, akan mencari berita sesuai keyword

5. **Navigation Safety**: URL article di-encode dengan URLEncoder untuk safety saat navigation.

---

## NEXT STEPS (OPTIONAL ENHANCEMENTS)

Untuk fitur tambahan di masa depan:
1. Implementasi local database (Room) untuk caching
2. Tambahkan fitur "Save Article" ke favorit
3. Implementasi dark mode
4. Tambahkan kategori berita (Sports, Technology, dll)
5. Implementasi pagination untuk infinite scroll

---

Selamat! Aplikasi NEWS APP sudah siap untuk dijalankan di Android Studio!
