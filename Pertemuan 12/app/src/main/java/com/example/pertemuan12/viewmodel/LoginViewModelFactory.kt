package com.example.pertemuan12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pertemuan12.data.repository.UserRepository

/**
 * [LOGIC LAYER - ViewModel Factory]
 * ViewModelFactory diperlukan karena LoginViewModel membutuhkan parameter (repository)
 * saat dibuat. Factory ini berperan sebagai "pabrik" yang tahu cara membuat ViewModel
 * dengan dependensi yang tepat.
 *
 * Dalam arsitektur MVVM:
 * - Ini adalah bentuk sederhana dari Dependency Injection (DI) manual.
 * - Pada proyek yang lebih besar, biasanya digantikan oleh library Hilt atau Koin
 *   yang mengotomatiskan proses injeksi ini dan menghilangkan boilerplate code.
 */
class LoginViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Validasi bahwa factory ini hanya untuk LoginViewModel
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
