package com.example.pertemuan12.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pertemuan12.data.local.dao.UserDao
import com.example.pertemuan12.data.local.entity.User

/**
 * [DATA LAYER - Database Class]
 * AppDatabase adalah "Penghubung Sentral" yang mendaftarkan seluruh Entity dan DAO
 * ke dalam sistem aplikasi.
 *
 * Dalam arsitektur MVVM:
 * - Database Class merupakan titik akses utama koneksi ke Room Database.
 * - @Database → mendaftarkan semua Entity dan menentukan versi skema database.
 * - Menggunakan pola Singleton (INSTANCE) agar hanya ada satu instance database
 *   di seluruh siklus hidup aplikasi, mencegah kebocoran memori.
 *
 * Komponen Model Layer: Room Database (lokal) sebagai lapisan abstraksi di atas SQLite.
 * Room hadir untuk menutupi kelemahan SQLite manual (rawan crash, banyak boilerplate).
 */
@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    // Fungsi abstrak yang menghubungkan Database dengan DAO-nya
    abstract fun userDao(): UserDao

    companion object {
        // @Volatile → memastikan nilai INSTANCE selalu up-to-date di semua thread
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Fungsi getDatabase() menerapkan pola Singleton:
         * Jika database belum ada → buat baru.
         * Jika sudah ada → kembalikan instance yang sudah ada.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "login_db"   // Nama file database SQLite di perangkat
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
