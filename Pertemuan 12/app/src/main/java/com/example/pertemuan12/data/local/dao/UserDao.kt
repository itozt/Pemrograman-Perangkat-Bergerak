package com.example.pertemuan12.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pertemuan12.data.local.entity.User

/**
 * [DATA LAYER - DAO (Data Access Object)]
 * DAO adalah "gerbang akses" atau jembatan instruksi SQL ke database.
 *
 * Dalam arsitektur MVVM:
 * - DAO merupakan bagian dari Model/Data Layer.
 * - Bertugas menyediakan instruksi SQL yang dicek saat compile (bukan runtime),
 *   sehingga jauh lebih aman dibanding SQLite manual yang rawan crash.
 * - Keyword `suspend` → fungsi ini berjalan secara asynchronous (Kotlin Coroutine),
 *   agar operasi database tidak membekukan/memblokir Main UI Thread.
 */
@Dao
interface UserDao {

    // @Insert → Room otomatis generate SQL INSERT tanpa boilerplate
    @Insert
    suspend fun insert(user: User)

    // @Query → SQL yang dicek validitasnya saat compile, bukan saat runtime
    // Mencari user berdasarkan username DAN password yang cocok
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): User?
}
