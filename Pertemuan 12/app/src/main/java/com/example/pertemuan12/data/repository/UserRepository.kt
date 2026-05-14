package com.example.pertemuan12.data.repository

import com.example.pertemuan12.data.local.dao.UserDao
import com.example.pertemuan12.data.local.entity.User

/**
 * [DATA LAYER - Repository Pattern]
 * Repository adalah lapisan eksklusif yang menyembunyikan asal-usul data
 * dari bagian aplikasi lainnya (terutama ViewModel).
 *
 * Dalam arsitektur MVVM:
 * - ViewModel tidak perlu tahu apakah data berasal dari API atau cache lokal Room.
 * - Repository menjadi "Single Source of Truth" — pusat kontrol sumber data.
 * - Repository menyembunyikan kompleksitas DAO dari ViewModel.
 * - Mempermudah unit testing dengan mock data (karena ViewModel hanya tahu Repository).
 *
 * Alur data:
 * ViewModel → Repository → DAO → Room → SQLite
 */
class UserRepository(
    private val dao: UserDao
) {
    // Meneruskan perintah insert ke DAO
    suspend fun insert(user: User) {
        dao.insert(user)
    }

    // Meneruskan perintah login ke DAO, mengembalikan User atau null
    suspend fun login(username: String, password: String): User? {
        return dao.login(username, password)
    }
}
