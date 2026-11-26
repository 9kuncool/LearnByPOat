package com.example.noai_test_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus
import com.example.noai_test_app.ui.theme.NoAI_test_AppTheme

class HouseListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoAI_test_AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White
                ) { innerPadding ->
                    MainApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ใช้ NavHost, navController และ ViewModel
@Composable
fun MainApp(modifier: Modifier = Modifier, viewModel: HouseViewModel = viewModel()) {
    val navController = rememberNavController()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    houses = viewModel.housesList,
                    onHouseClick = { house ->
                        val index = viewModel.housesList.indexOfFirst { it.houseNumber == house.houseNumber }
                        if (index != -1) {
                            navController.navigate("detail/$index")
                        }
                    }
                )
            }
            composable(
                route = "detail/{houseIndex}",
                arguments = listOf(navArgument("houseIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val houseIndex = backStackEntry.arguments?.getInt("houseIndex") ?: 0
                val house = viewModel.housesList.getOrNull(houseIndex)

                if (house != null) {
                    HouseDetailScreen(
                        house = house,
                        onBackClick = { navController.popBackStack() },
                        onSaveSuccess = { updatedHouse ->
                            // อัปเดตข้อมูลใน ViewModel แต่ไม่สั่ง popBackStack() อัตโนมัติ เพื่อให้ผู้ใช้เห็นผลลัพธ์การคำนวณก่อน
                            viewModel.updateHouse(updatedHouse)
                        }
                    )
                }
            }
        }
    }
}

// หน้าหลัก (รายการบ้าน)
@Composable
fun HomeScreen(
    houses: List<House>,
    onHouseClick: (House) -> Unit,
    modifier: Modifier = Modifier
) {
    // ใช้ rememberSaveable จำข้อความค้นหาเมื่อหมุนจอ
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredHouses = houses.filter { house ->
        house.houseNumber.contains(searchQuery, ignoreCase = true) ||
                house.ownerName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TextHeader(title = "รายชื่อบ้าน")
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(16.dp)
        )

        HouseList(
            houses = filteredHouses,
            onHouseClick = onHouseClick
        )
    }
}

@Composable
fun TextHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("ค้นหา...") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun HouseList(
    houses: List<House>,
    onHouseClick: (House) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(houses) { house ->
            HouseItem(house = house, onClick = { onHouseClick(house) })
        }
    }
}

@Composable
fun HouseItem(
    house: House,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "บ้านเลขที่: ${house.houseNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "เจ้าของ: ${house.ownerName}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (house.calculatedBill != null) {
                    Text(
                        text = "ค่าน้ำ: ${house.calculatedBill} บาท",
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            val (statusText, statusTextColor, statusBgColor) = when (house.status) {
                HouseStatus.RECORDED -> Triple("จดแล้ว", Color(0xFF1B5E20), Color(0xFFE8F5E9))
                HouseStatus.UNRECORDED -> Triple("ยังไม่จด", Color(0xFF616161), Color(0xFFF5F5F5))
                HouseStatus.UNREADABLE -> Triple("อ่านไม่ได้", Color(0xFFB71C1C), Color(0xFFFFEBEE))
            }

            Box(
                modifier = Modifier
                    .background(color = statusBgColor, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoAI_test_AppTheme {
        MainApp()
    }
}
