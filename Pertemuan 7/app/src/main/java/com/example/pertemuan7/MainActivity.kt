package com.example.pertemuan7 // Ini adalah nama 'kardus' atau folder utama tempat kodemu disimpan

// Bagian 'import' ini seperti mengambil alat-alat yang kita butuhkan dari kotak perkakas Android.
// Kalau tidak di-import, Android Studio tidak tahu alat apa yang mau kita pakai.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pertemuan7.ui.theme.Pertemuan7Theme

// MainActivity adalah pintu gerbang utama saat aplikasi pertama kali dibuka di HP
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent adalah tempat kita menaruh semua tampilan layar
        setContent {
            Pertemuan7Theme { // Ini untuk menerapkan tema warna bawaan proyekmu

                // Surface ini ibarat kanvas gambar yang masih kosong
                Surface(
                    modifier = Modifier.fillMaxSize(), // Perintah untuk membuat kanvasnya memenuhi seluruh layar HP
                    color = MaterialTheme.colorScheme.background // Memberi warna latar belakang standar
                ) {
                    // Setelah kanvasnya siap, kita panggil fungsi LoginScreen untuk mulai 'menggambar' tampilannya
                    LoginScreen()
                }
            }
        }
    }
}

// Tanda @Composable menandakan bahwa blok kode di bawahnya adalah untuk membuat tampilan visual (UI)
@Composable
fun LoginScreen() {
    // Column berfungsi untuk menyusun elemen-elemen dari atas ke bawah (berbaris vertikal)
    Column(
        modifier = Modifier.fillMaxSize(), // Mengatur agar susunannya membentang penuh di layar
        verticalArrangement = Arrangement.Center, // Memposisikan semua elemen agar ngumpul di tengah-tengah layar (atas-bawah)
        horizontalAlignment = Alignment.CenterHorizontally // Memposisikan elemen agar rata tengah (kiri-kanan)
    ) {

        // 1. Bagian menampilkan gambar ilustrasi login
        Image(
            painter = painterResource(id = R.drawable.a), // Mengambil gambar bernama 'a.png' dari folder res/drawable
            contentDescription = "Login image", // Teks penjelas (biasanya berguna untuk fitur pembaca layar tunanetra)
            modifier = Modifier.size(200.dp) // Mengatur ukuran gambar menjadi 200x200
        )

        // 2. Bagian menampilkan teks sapaan utama
        Text(
            text = "Welcome Back",
            fontSize = 28.sp, // Ukuran hurufnya diperbesar menjadi 28
            fontWeight = FontWeight.Bold // Membuat hurufnya dicetak tebal (Bold)
        )

        // Spacer ini ibarat memberi spasi (jarak kosong) antar elemen supaya tidak menempel
        Spacer(modifier = Modifier.height(4.dp)) // Memberi jarak setinggi 4 ke bawah

        // 3. Menampilkan teks kecil di bawah tulisan Welcome Back
        Text(text = "Login to your account")

        Spacer(modifier = Modifier.height(16.dp)) // Jarak kosong lagi biar agak renggang

        // 4. Membuat kotak isian untuk memasukkan Email
        OutlinedTextField(
            value = "", // Isi teks saat ini (dikosongkan karena nunggu diketik orang)
            onValueChange = {}, // Tempat menaruh perintah saat teks diketik (dikosongkan dulu untuk tugas tampilan ini)
            label = { Text(text = "Email address") } // Teks petunjuk kecil di atas kotak
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Membuat kotak isian untuk memasukkan Password
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Membuat tombol "Login"
        Button(onClick = { /* Di sini nanti diisi perintah aksi kalau tombol ditekan */ }) {
            Text(text = "Login") // Tulisan yang muncul di dalam tombol
        }

        Spacer(modifier = Modifier.height(32.dp)) // Jarak kosong yang lebih jauh ke bawah

        // 7. Menampilkan teks tulisan Lupa Password
        Text(
            text = "Forgot Password?",
            // modifier.clickable ini perintah ajaib yang membuat teks biasa bisa ditekan layaknya tombol
            modifier = Modifier.clickable { /* Aksi kalau tulisan ini diklik */ }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 8. Teks pembatas "Atau masuk dengan..."
        Text(text = "Or sign in with")

        // Row berfungsi sebaliknya dari Column. Row menyusun elemen menyamping dari kiri ke kanan (berbaris horizontal)
        Row(
            modifier = Modifier
                .fillMaxWidth() // Mengatur baris ini agar membentang memenuhi lebar layar
                .padding(40.dp), // Memberi jarak 'napas' dari ujung kiri dan kanan layar sebesar 40
            horizontalArrangement = Arrangement.SpaceEvenly // Membagi jarak ruang kosong secara merata untuk ketiga ikon gambar di dalamnya
        ) {
            // Menampilkan Ikon Facebook
            Image(
                painter = painterResource(id = R.drawable.fb), // Mengambil gambar 'fb.png'
                contentDescription = "Facebook",
                modifier = Modifier.size(60.dp).clickable { /* Aksi klik FB */ } // Ukurannya 60x60 dan diatur supaya gambarnya bisa diklik
            )
            // Menampilkan Ikon Google
            Image(
                painter = painterResource(id = R.drawable.google), // Mengambil gambar 'google.png'
                contentDescription = "Google",
                modifier = Modifier.size(60.dp).clickable { /* Aksi klik Google */ }
            )
            // Menampilkan Ikon Twitter/X
            Image(
                painter = painterResource(id = R.drawable.twitter), // Mengambil gambar 'twitter.png'
                contentDescription = "Twitter",
                modifier = Modifier.size(60.dp).clickable { /* Aksi klik Twitter */ }
            )
        }
    }
}

// Blok Preview ini sangat membantu!
// Ini HANYA untuk menampilkan hasil rancangan di layar sebelah kanan Android Studio (tab Split/Design).
// Jadi kamu bisa melihat hasil gambaranmu tanpa harus repot menjalankan emulator HP yang memakan waktu.
@Preview(showBackground = true) // Memberikan latar belakang warna putih/terang di tampilan preview
@Composable
fun LoginScreenPreview() {
    Pertemuan7Theme {
        LoginScreen() // Kita panggil fungsi rancangan layar di atas untuk dimunculkan di layar preview
    }
}