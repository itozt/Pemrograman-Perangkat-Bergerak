package com.example.pertemuan12.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [MODEL / DATA LAYER]
 * Entity merepresentasikan satu tabel di dalam Room Database (SQLite).
 * Anotasi @Entity memberitahu Room bahwa kelas ini adalah tabel bernama "users".
 *
 * Dalam arsitektur MVVM:
 * - Model/Data Layer bertugas mengelola asal-usul data secara eksklusif.
 * - Entity mendefinisikan skema tabel SQLite (kolom: id, username, password).
 * - View (UI) dilarang keras mengakses database langsung.
 */
@Entity(tableName = "users")
data class User(
    // @PrimaryKey(autoGenerate = true) → id akan diisi otomatis oleh Room (auto-increment)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String
)
