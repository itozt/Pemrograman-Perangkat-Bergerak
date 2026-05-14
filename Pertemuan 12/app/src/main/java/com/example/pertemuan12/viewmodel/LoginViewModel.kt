package com.example.pertemuan12.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertemuan12.data.local.entity.User
import com.example.pertemuan12.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * [LOGIC LAYER - ViewModel]
 * ViewModel adalah "otak" aplikasi yang menjembatani UI (View) dengan Data (Model).
 *
 * Dalam arsitektur MVVM:
 * - ViewModel bertugas: menyimpan state UI, menjalankan logika bisnis,
 *   dan bersifat lifecycle-aware (data tetap aman saat rotasi layar).
 * - ViewModel TIDAK boleh menyimpan referensi ke Context atau Activity
 *   untuk menghindari memory leak.
 * - Terintegrasi dengan Coroutine (viewModelScope) untuk proses asinkron,
 *   sehingga operasi database berjalan di background thread tanpa membekukan UI.
 *
 * State Management:
 * - `mutableStateOf` → memungkinkan Compose UI memantau perubahan state secara real-time.
 *   Setiap kali loginState berubah, Compose otomatis melakukan recomposition (gambar ulang).
 *
 * Alur Reactive Loop:
 * 1. User klik Login (View/Compose)
 * 2. ViewModel.login() dipanggil → memicu Coroutine (viewModelScope)
 * 3. Repository.login() → DAO.login() → Room → SQLite
 * 4. Hasil dikembalikan → ViewModel update loginState
 * 5. Compose mendeteksi perubahan state → Recomposition otomatis
 */
class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // State yang dipantau oleh Compose UI secara real-time
    // `private set` → hanya ViewModel yang bisa mengubah nilai ini
    var loginState by mutableStateOf("")
        private set

    /**
     * Fungsi login() menjalankan proses autentikasi secara asinkron.
     * viewModelScope.launch → memastikan operasi database berjalan di background thread,
     * agar animasi UI tetap berjalan smooth (60fps+) tanpa ANR (App Not Responding).
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            // Update state berdasarkan hasil query dari Room/SQLite
            loginState = if (user != null) {
                "Login Berhasil ✓"
            } else {
                "Username atau Password Salah ✗"
            }
        }
    }

    /**
     * Fungsi untuk menyisipkan data dummy (admin/12345) ke Room Database.
     * Dipanggil sekali saat aplikasi pertama kali dibuka (LaunchedEffect di MainActivity).
     */
    fun insertDummyUser() {
        viewModelScope.launch {
            repository.insert(
                User(
                    username = "admin",
                    password = "12345"
                )
            )
        }
    }
}
