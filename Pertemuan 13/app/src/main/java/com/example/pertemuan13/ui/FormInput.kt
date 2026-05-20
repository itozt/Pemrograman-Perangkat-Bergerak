package com.example.pertemuan13.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormInput(
    nama: String,
    email: String,
    onNamaChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onTambahClick: () -> Unit,
    isEditing: Boolean = false,
    onCancelEdit: (() -> Unit)? = null
) {
    Column {
        OutlinedTextField(
            value = nama,
            onValueChange = onNamaChange,
            label = { Text("Nama") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            // Mode Edit: tampilkan tombol Batal dan Simpan Perubahan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onCancelEdit?.invoke() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = onTambahClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simpan")
                }
            }
        } else {
            // Mode Tambah: tampilkan tombol Tambah Siswa
            Button(
                onClick = onTambahClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Siswa")
            }
        }
    }
}
