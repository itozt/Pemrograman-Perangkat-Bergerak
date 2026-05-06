package com.example.pertemuan11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// =====================================================
// 1. DATA MODEL
// =====================================================
data class Product(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val price: String,
    val description: String,
    val category: String = "Umum"
)

// =====================================================
// 2. MAIN ACTIVITY
// =====================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarketSiswaTheme {
                MainScreen()
            }
        }
    }
}

// =====================================================
// 3. THEME — Google Material You palette
//    HCI: Konsistensi warna membangun kepercayaan dan
//    mempercepat pemahaman pengguna.
// =====================================================
@Composable
fun MarketSiswaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary            = Color(0xFF1A73E8), // Google Blue
            onPrimary          = Color.White,
            primaryContainer   = Color(0xFFD2E3FC),
            onPrimaryContainer = Color(0xFF0A3880),
            secondary          = Color(0xFF34A853), // Google Green
            onSecondary        = Color.White,
            secondaryContainer = Color(0xFFCCF0D7),
            onSecondaryContainer = Color(0xFF0D5C27),
            tertiary           = Color(0xFFFBBC04), // Google Yellow
            background         = Color(0xFFF8F9FA),
            surface            = Color.White,
            surfaceVariant     = Color(0xFFF1F3F4),
            onBackground       = Color(0xFF202124),
            onSurface          = Color(0xFF202124),
            onSurfaceVariant   = Color(0xFF5F6368),
            outline            = Color(0xFFDADCE0),
            error              = Color(0xFFD93025),
            onError            = Color.White,
        ),
        content = content
    )
}

// =====================================================
// 4. MAIN SCREEN
// =====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }
    val productList   = remember { mutableStateListOf<Product>() }
    val snackbarHost  = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()

    // Seed data awal
    LaunchedEffect(Unit) {
        if (productList.isEmpty()) {
            productList.add(Product(name = "Brownies Lumer",    price = "15000", description = "Cokelat melimpah, cocok jadi camilan!", category = "Makanan"))
            productList.add(Product(name = "Kaos Custom",       price = "85000", description = "Bahan adem, sablon awet, bisa request desain.", category = "Fashion"))
            productList.add(Product(name = "Jasa Desain Logo",  price = "50000", description = "Desain profesional, revisi 3x, format PNG & AI.", category = "Jasa"))
        }
    }

    Scaffold(
        // ── Custom Snackbar ──────────────────────────────────────────────
        // HCI: Feedback visual yang jelas setelah setiap aksi pengguna.
        snackbarHost = {
            SnackbarHost(snackbarHost) { data ->
                Snackbar(
                    modifier      = Modifier.padding(16.dp),
                    shape         = RoundedCornerShape(12.dp),
                    containerColor = Color(0xFF202124),
                    contentColor  = Color.White,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint   = Color(0xFF34A853),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(data.visuals.message, fontSize = 14.sp)
                    }
                }
            }
        },

        // ── Top App Bar ──────────────────────────────────────────────────
        topBar = {
            when (currentScreen) {
                "home"    -> HomeTopBar()
                "add"     -> AddProductTopBar(onBack = { currentScreen = "home" })
                "profile" -> ProfileTopBar()
            }
        },

        // ── Bottom Navigation ────────────────────────────────────────────
        // HCI: Navigasi yang konsisten dan mudah dijangkau jempol.
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick  = { currentScreen = "home" },
                    label    = { Text("Beranda", fontSize = 12.sp) },
                    icon = {
                        Icon(
                            if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Beranda"
                        )
                    },
                    colors = navBarItemColors()
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick  = { currentScreen = "profile" },
                    label    = { Text("Profil", fontSize = 12.sp) },
                    icon = {
                        Icon(
                            if (currentScreen == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profil"
                        )
                    },
                    colors = navBarItemColors()
                )
            }
        },

        // ── FAB ──────────────────────────────────────────────────────────
        // HCI: Aksi utama ditonjolkan agar mudah ditemukan.
        floatingActionButton = {
            if (currentScreen == "home") {
                ExtendedFloatingActionButton(
                    onClick        = { currentScreen = "add" },
                    containerColor = Color(0xFF1A73E8),
                    contentColor   = Color.White,
                    shape          = RoundedCornerShape(16.dp),
                    elevation      = FloatingActionButtonDefaults.elevation(2.dp, 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Jual Produk", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)

    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // ── Animated transitions antar screen ─────────────────────
            // HCI: Transisi halus mengurangi disorientasi pengguna.
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(220))
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    "home" -> HomeScreen(
                        products = productList,
                        onDeleteProduct = { product ->
                            productList.remove(product)
                            scope.launch { snackbarHost.showSnackbar("Produk dihapus") }
                        }
                    )
                    "add" -> AddProductScreen(
                        onProductAdded = { newProduct ->
                            productList.add(0, newProduct)
                            scope.launch {
                                currentScreen = "home"
                                snackbarHost.showSnackbar("Produk berhasil ditambahkan! 🎉")
                            }
                        }
                    )
                    "profile" -> ProfileScreen(productCount = productList.size)
                }
            }
        }
    }
}

// =====================================================
// 5. TOP BARS
// =====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1A73E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("M", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("MarketSiswa", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF202124))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Tambah Produk", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
        navigationIcon = {
            // HCI: Tombol back yang jelas & konsisten mencegah jebakan navigasi.
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF5F6368))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar() {
    TopAppBar(
        title = { Text("Profil Saya", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// =====================================================
// 6. HOME SCREEN
// =====================================================
@Composable
fun HomeScreen(products: List<Product>, onDeleteProduct: (Product) -> Unit) {
    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Hero / Greeting Card ─────────────────────────────────────
        item { GreetingCard(productCount = products.size) }
        item { Spacer(Modifier.height(20.dp)) }

        // ── Section header ───────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Semua Produk", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF202124))
                Text("${products.size} item", fontSize = 14.sp, color = Color(0xFF5F6368))
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Empty state ──────────────────────────────────────────────
        // HCI: Halaman kosong harus komunikatif & ajak aksi pengguna.
        if (products.isEmpty()) {
            item { EmptyStateCard() }
        } else {
            items(products, key = { it.id }) { product ->
                ProductCard(product = product, onDelete = { onDeleteProduct(product) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// =====================================================
// 7. GREETING CARD
// =====================================================
@Composable
fun GreetingCard(productCount: Int) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Halo, Siswa! 👋", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (productCount == 0) "Belum ada produk. Yuk mulai jual!"
                    else "Kamu punya $productCount produk aktif",
                    fontSize = 14.sp, color = Color(0xFFBDD7F8)
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

// =====================================================
// 8. EMPTY STATE
// =====================================================
@Composable
fun EmptyStateCard() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Belum ada produk", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF202124))
            Spacer(Modifier.height(8.dp))
            Text("Tap tombol + di bawah untuk mulai berjualan!", fontSize = 14.sp, color = Color(0xFF5F6368))
        }
    }
}

// =====================================================
// 9. PRODUCT CARD
//    HCI: Info diatur berdasarkan hierarki visual — nama
//    paling menonjol, harga highlight, deskripsi sekunder.
// =====================================================
@Composable
fun ProductCard(product: Product, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ── Konfirmasi hapus ─────────────────────────────────────────────
    // HCI: Aksi destruktif harus ada konfirmasi untuk cegah kesalahan.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Hapus Produk?", fontWeight = FontWeight.SemiBold) },
            text  = { Text("\"${product.name}\" akan dihapus dari daftar produkmu.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors  = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD93025))
                ) { Text("Hapus", fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Inisial produk sebagai avatar ──────────────────────
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFD2E3FC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    product.name.first().uppercaseChar().toString(),
                    fontSize    = 22.sp,
                    fontWeight  = FontWeight.ExtraBold,
                    color       = Color(0xFF1A73E8)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nama produk — level hierarki tertinggi
                Text(
                    product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = Color(0xFF202124),
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                // Deskripsi — level sekunder
                Text(
                    product.description,
                    color    = Color(0xFF5F6368),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    // Chip kategori
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFF1F3F4)) {
                        Text(
                            product.category,
                            fontSize = 11.sp,
                            color    = Color(0xFF5F6368),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    // Harga — ditonjolkan dengan warna primer
                    Text(
                        formatPrice(product.price),
                        color      = Color(0xFF1A73E8),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 15.sp
                    )
                }
            }

            // ── Tombol opsi ────────────────────────────────────────
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.MoreVert, contentDescription = "Opsi", tint = Color(0xFF9AA0A6), modifier = Modifier.size(20.dp))
            }
        }
    }
}

// =====================================================
// 10. ADD PRODUCT SCREEN
//     HCI: Form yang baik = label jelas, validasi real-time,
//     & feedback langsung sebelum submit.
// =====================================================
@Composable
fun AddProductScreen(onProductAdded: (Product) -> Unit) {
    var name             by remember { mutableStateOf("") }
    var price            by remember { mutableStateOf("") }
    var desc             by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Umum") }
    var isLoading        by remember { mutableStateOf(false) }
    val scope            = rememberCoroutineScope()
    val categories       = listOf("Makanan", "Fashion", "Jasa", "Elektronik", "Umum")

    // ── Validasi real-time ────────────────────────────────────────────
    // HCI: Error prevention — tunjukkan masalah sebelum pengguna submit.
    val nameError   = name.isNotBlank()  && name.length < 3
    val priceError  = price.isNotBlank() && price.toLongOrNull() == null
    val isFormValid = name.isNotBlank() && price.isNotBlank() && desc.isNotBlank()
            && !nameError && !priceError && name.length >= 3

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Info Banner ──────────────────────────────────────────────
        item {
            Card(
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color(0xFFD2E3FC)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1A73E8), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Isi detail produk dengan lengkap agar mudah ditemukan pembeli.", fontSize = 13.sp, color = Color(0xFF0A3880))
                }
            }
        }

        // ── Nama Produk ──────────────────────────────────────────────
        item {
            FieldLabel("Nama Produk", required = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { if (it.length <= 50) name = it },
                placeholder   = { Text("Contoh: Brownies Cokelat") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                isError       = nameError,
                supportingText = {
                    if (nameError) Text("Nama minimal 3 karakter", color = Color(0xFFD93025))
                    else           Text("${name.length}/50", color = Color(0xFF9AA0A6))
                },
                singleLine = true,
                colors     = fieldColors()
            )
        }

        // ── Harga ────────────────────────────────────────────────────
        item {
            FieldLabel("Harga", required = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = price,
                onValueChange = { price = it.filter { c -> c.isDigit() } },
                placeholder   = { Text("Contoh: 15000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                isError       = priceError,
                leadingIcon   = {
                    Text("Rp", color = Color(0xFF5F6368), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 14.dp))
                },
                supportingText = {
                    when {
                        priceError        -> Text("Masukkan angka yang valid", color = Color(0xFFD93025))
                        price.isNotBlank() -> Text(formatPrice(price), color = Color(0xFF1A73E8))
                    }
                },
                singleLine = true,
                colors     = fieldColors()
            )
        }

        // ── Kategori ─────────────────────────────────────────────────
        // HCI: Chip lebih cepat dipilih daripada dropdown untuk pilihan kecil.
        item {
            FieldLabel("Kategori", required = false)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick  = { selectedCategory = category },
                        label    = { Text(category, fontSize = 13.sp) },
                        shape    = RoundedCornerShape(20.dp),
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD2E3FC),
                            selectedLabelColor     = Color(0xFF1A73E8),
                        )
                    )
                }
            }
        }

        // ── Deskripsi ────────────────────────────────────────────────
        item {
            FieldLabel("Deskripsi", required = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value          = desc,
                onValueChange  = { desc = it },
                placeholder    = { Text("Ceritakan tentang produkmu...") },
                modifier       = Modifier.fillMaxWidth(),
                shape          = RoundedCornerShape(14.dp),
                minLines       = 4,
                maxLines       = 6,
                supportingText = { Text("${desc.length} karakter", color = Color(0xFF9AA0A6)) },
                colors         = fieldColors()
            )
        }

        // ── Tombol Simpan ────────────────────────────────────────────
        // HCI: Disabled state yang jelas menunjukkan syarat yang belum terpenuhi.
        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        delay(1000) // Simulasi loading (FR-04)
                        onProductAdded(
                            Product(
                                name        = name.trim(),
                                price       = price,
                                description = desc.trim(),
                                category    = selectedCategory
                            )
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && !isLoading,
                shape   = RoundedCornerShape(14.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor        = Color(0xFF1A73E8),
                    disabledContainerColor = Color(0xFFDADCE0),
                    disabledContentColor  = Color(0xFF9AA0A6)
                )
            ) {
                AnimatedContent(targetState = isLoading, label = "btn_loading") { loading ->
                    if (loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text("Menyimpan...", fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Simpan Produk", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

// =====================================================
// 11. PROFILE SCREEN
// =====================================================
@Composable
fun ProfileScreen(productCount: Int) {
    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Avatar + Info ────────────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar inisial
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD2E3FC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JS", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A73E8))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("John Siswa", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFCCF0D7)) {
                        Text(
                            "XII RPL 1",
                            fontSize  = 13.sp,
                            color     = Color(0xFF137333),
                            fontWeight = FontWeight.SemiBold,
                            modifier  = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // ── Stats ────────────────────────────────────────────────────
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), label = "Produk",  value = "$productCount", icon = Icons.Default.Inventory2)
                StatCard(modifier = Modifier.weight(1f), label = "Terjual", value = "0",            icon = Icons.Default.ShoppingCart)
            }
        }

        // ── Menu ─────────────────────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ProfileMenuItem(Icons.Default.Edit,          "Edit Profil",        "Ubah nama dan foto profil")
                    MenuDivider()
                    ProfileMenuItem(Icons.Default.Notifications, "Notifikasi",         "Atur preferensi notifikasi")
                    MenuDivider()
                    ProfileMenuItem(Icons.Default.Help,          "Bantuan",            "Pusat bantuan & FAQ")
                    MenuDivider()
                    ProfileMenuItem(Icons.Default.Info,          "Tentang Aplikasi",   "MarketSiswa v1.0.0")
                }
            }
        }

        // ── Tombol Keluar ────────────────────────────────────────────
        item {
            OutlinedButton(
                onClick   = { /* TODO */ },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD93025)),
                border    = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD93025))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Keluar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// =====================================================
// 12. HELPER COMPOSABLES
// =====================================================
@Composable
fun FieldLabel(text: String, required: Boolean) {
    Row {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF202124))
        if (required) Text(" *", color = Color(0xFFD93025), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1A73E8), modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF202124))
            Text(label, fontSize = 12.sp, color = Color(0xFF5F6368))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F3F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF5F6368), modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color(0xFF202124))
            Text(subtitle, fontSize   = 12.sp,             color    = Color(0xFF9AA0A6))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFDADCE0))
    }
}

@Composable
fun MenuDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F3F4))
}

@Composable
fun navBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = Color(0xFF1A73E8),
    selectedTextColor   = Color(0xFF1A73E8),
    indicatorColor      = Color(0xFFD2E3FC),
    unselectedIconColor = Color(0xFF5F6368),
    unselectedTextColor = Color(0xFF5F6368)
)

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF1A73E8),
    unfocusedBorderColor = Color(0xFFDADCE0),
    focusedLabelColor    = Color(0xFF1A73E8),
    cursorColor          = Color(0xFF1A73E8),
    errorBorderColor     = Color(0xFFD93025)
)

// =====================================================
// 13. UTILITY
// =====================================================
fun formatPrice(price: String): String {
    val number = price.toLongOrNull() ?: return "Rp $price"
    val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${format.format(number)}"
}