package com.example.pertemuan13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pertemuan13.data.Siswa
import com.example.pertemuan13.data.SiswaDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(private val dao: SiswaDao) : ViewModel() {

    // Mengubah Flow dari Room menjadi StateFlow agar UI reaktif
    val siswaList = dao.getAllSiswa()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun tambahSiswa(nama: String, email: String) {
        viewModelScope.launch {
            dao.insertSiswa(Siswa(nama = nama, email = email))
        }
    }

    fun hapusSiswa(siswa: Siswa) {
        viewModelScope.launch {
            dao.deleteSiswa(siswa)
        }
    }

    fun editSiswa(siswa: Siswa) {
        viewModelScope.launch {
            dao.updateSiswa(siswa)
        }
    }

    // Factory untuk membuat ViewModel dengan parameter (SiswaDao)
    companion object {
        fun factory(dao: SiswaDao): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudentViewModel(dao) as T
                }
            }
        }
    }
}
