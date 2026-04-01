package com.example.kalkulatorsederhana // Pastikan ini sesuai dengan proyekmu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalkulatorsederhana.ui.theme.KalkulatorSederhanaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalkulatorSederhanaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    // State untuk input dan hasil
    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Fungsi untuk menghitung hasil dengan pembulatan
    fun calculate(operator: String) {
        errorMessage = ""
        result = ""
        val num1 = input1.toDoubleOrNull()
        val num2 = input2.toDoubleOrNull()

        if (num1 != null && num2 != null) {
            when (operator) {
                "+" -> {
                    val res = num1 + num2
                    // Perbaikan: Format hasil hingga 6 desimal, lalu bersihkan nol di akhir
                    result = if (res % 1.0 == 0.0) res.toInt().toString() else "%.6f".format(res).trimEnd('0').trimEnd('.')
                }
                "-" -> {
                    val res = num1 - num2
                    // Perbaikan: Format hasil hingga 6 desimal, lalu bersihkan nol di akhir
                    result = if (res % 1.0 == 0.0) res.toInt().toString() else "%.6f".format(res).trimEnd('0').trimEnd('.')
                }
                "*" -> {
                    val res = num1 * num2
                    // Perbaikan: Format hasil hingga 6 desimal, lalu bersihkan nol di akhir
                    result = if (res % 1.0 == 0.0) res.toInt().toString() else "%.6f".format(res).trimEnd('0').trimEnd('.')
                }
                "/" -> {
                    if (num2 != 0.0) {
                        val res = num1 / num2
                        // Perbaikan: Format hasil hingga 6 desimal, lalu bersihkan nol di akhir
                        result = if (res % 1.0 == 0.0) res.toInt().toString() else "%.6f".format(res).trimEnd('0').trimEnd('.')
                    } else {
                        errorMessage = "Tidak bisa dibagi dengan nol!"
                    }
                }
            }
        } else {
            errorMessage = "Masukkan kedua angka dengan benar!"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Area Judul
        Text(
            text = "Kalkulator Sederhana",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Area Hasil yang Diperbaiki
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Hasil:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.End
                    )
                } else {
                    // PERBAIKAN: Gunakan maxLines dan modifier tambahan untuk perataan
                    Text(
                        text = result,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(), // Tambahkan ini agar perataan kanan berfungsi
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1, // Pastikan teks tidak menumpuk secara vertikal
                        overflow = TextOverflow.Visible // Biarkan memanjang jika perlu, tapi karena pembulatan, itu tidak akan terjadi
                    )
                }
            }
        }

        // Form Input
        OutlinedTextField(
            value = input1,
            onValueChange = { input1 = it.filter { char -> char.isDigit() || char == '.' } },
            label = { Text("Angka Pertama") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = input2,
            onValueChange = { input2 = it.filter { char -> char.isDigit() || char == '.' } },
            label = { Text("Angka Kedua") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // Tombol Operasi
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OpButton("+") { calculate("+") }
            OpButton("-") { calculate("-") }
            OpButton("*") { calculate("*") }
            OpButton("/") { calculate("/") }
        }

        // Tombol Kontrol (Hapus dan Kosongkan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (input1.isNotEmpty()) input1 = input1.dropLast(1)
                    errorMessage = ""
                    result = ""
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text("Hapus", color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Button(
                onClick = {
                    input1 = ""
                    input2 = ""
                    result = ""
                    errorMessage = ""
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text("Kosongkan", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
fun OpButton(symbol: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        contentPadding = PaddingValues(0.dp) // Untuk memastikan tombol tetap bulat
    ) {
        Text(
            text = symbol,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}