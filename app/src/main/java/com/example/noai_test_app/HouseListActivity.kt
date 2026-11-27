package com.example.noai_test_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
                    containerColor = Color.White,
                ) { innerPadding ->
                    HouseListApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HouseListApp(modifier: Modifier = Modifier, viewModel: HouseViewModel = viewModel()) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedHouseNumber by rememberSaveable { mutableStateOf<String?>(null) }

    val houses = viewModel.housesList
    val selectedHouse = houses.find { (houseNumber) -> houseNumber == selectedHouseNumber }

    if (selectedHouse != null) {
        HouseDetailScreen(
            house = selectedHouse,
            onBackClick = { selectedHouseNumber = null },
            onSaveSuccess = { _ -> },
            viewModel = viewModel,
            modifier = modifier,
        )
    } else {
        val filteredHouses = houses.filter { house ->
            house.houseNumber.contains(searchQuery, ignoreCase = true) ||
                    house.ownerName.contains(searchQuery, ignoreCase = true)
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "รายการบ้านและจดมิเตอร์น้ำ",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ค้นหาบ้านเลขที่, ชื่อเจ้าของ...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // House List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
            ) {
                items(filteredHouses) { house ->
                    HouseItem(
                        house = house,
                    ) {
                        selectedHouseNumber = house.houseNumber
                    }
                }
            }
        }
    }
}

@Composable
fun HouseItem(house: House, onClick: () -> Unit) {
    val (statusText, statusTextColor, statusBgColor) = when (house.status) {
        HouseStatus.RECORDED -> Triple("จดแล้ว", Color(0xFF1B5E20), Color(0xFFE8F5E9))
        HouseStatus.UNRECORDED -> Triple("ยังไม่จด", Color(0xFF616161), Color(0xFFF5F5F5))
        HouseStatus.UNREADABLE -> Triple("อ่านไม่ได้", Color(0xFFB71C1C), Color(0xFFFFEBEE))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "บ้านเลขที่: ${house.houseNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = statusBgColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "เจ้าของบ้าน: ${house.ownerName}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "มิเตอร์เดือนก่อน: ${house.lastMonthReading} หน่วย",
                fontSize = 13.sp,
                color = Color.Gray
            )
            val isRecorded = (house.status == HouseStatus.RECORDED) && (house.calculatedBill != null)
            if (isRecorded) {
                Text(
                    text = "ยอดค่าน้ำ: ${house.calculatedBill} บาท",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            } else if (!house.note.isNullOrBlank()) {
                Text(
                    text = "หมายเหตุ: ${house.note}",
                    fontSize = 13.sp,
                    color = Color(0xFFB71C1C)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HouseListAppPreview() {
    NoAI_test_AppTheme {
        HouseListApp()
    }
}
