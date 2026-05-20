package com.example.pertemuan13.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pertemuan13.data.Siswa
import com.example.pertemuan13.viewmodel.StudentViewModel

@Composable
fun MainScreen(viewModel: StudentViewModel) {

    // State untuk input form
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // State untuk mode edit (null = mode tambah)
    var editingSiswa by remember { mutableStateOf<Siswa?>(null) }

    // State untuk dialog konfirmasi hapus
    var siswaToDelete by remember { mutableStateOf<Siswa?>(null) }

    // Observasi daftar siswa dari ViewModel secara reaktif
    val siswaList by viewModel.siswaList.collectAsStateWithLifecycle()

    // ── Dialog Konfirmasi Hapus ──────────────────────────────────────────────
    siswaToDelete?.let { siswa ->
        AlertDialog(
            onDismissRequest = { siswaToDelete = null },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Hapus data siswa \"${siswa.nama}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hapusSiswa(siswa)
                        siswaToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { siswaToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // ── Konten Utama ─────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {

        // Header
        Text(
            text = "Registrasi Siswa",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Kelola data siswa",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Form Input (mendukung mode Tambah dan Edit)
        FormInput(
            nama = nama,
            email = email,
            onNamaChange = { nama = it },
            onEmailChange = { email = it },
            isEditing = editingSiswa != null,
            onCancelEdit = {
                // Batalkan mode edit, reset form
                editingSiswa = null
                nama = ""
                email = ""
            },
            onTambahClick = {
                // Validasi input
                if (nama.isBlank()) return@FormInput
                if (email.isBlank()) return@FormInput
                if (!email.contains("@")) return@FormInput

                if (editingSiswa != null) {
                    // Mode Edit: update data yang sudah ada
                    viewModel.editSiswa(
                        editingSiswa!!.copy(nama = nama, email = email)
                    )
                    editingSiswa = null
                } else {
                    // Mode Tambah: simpan siswa baru
                    viewModel.tambahSiswa(nama, email)
                }

                // Reset form setelah aksi
                nama = ""
                email = ""
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Label Daftar Siswa
        Text(
            text = "Daftar Siswa",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tampilkan pesan kosong atau daftar siswa
        if (siswaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada data siswa.\nTambahkan siswa pertama!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(
                    items = siswaList,
                    key = { it.id } // key untuk performa animasi LazyColumn
                ) { siswa ->
                    StudentItem(
                        siswa = siswa,
                        onDelete = {
                            // Tampilkan dialog konfirmasi sebelum hapus
                            siswaToDelete = siswa
                        },
                        onEdit = {
                            // Masuk mode edit: isi form dengan data siswa terpilih
                            editingSiswa = siswa
                            nama = siswa.nama
                            email = siswa.email
                        }
                    )
                }
            }
        }
    }
}
