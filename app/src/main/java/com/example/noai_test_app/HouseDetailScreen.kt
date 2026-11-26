package com.example.noai_test_app

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus
import com.example.noai_test_app.example2.calcBill
import com.example.noai_test_app.ui.theme.NoAI_test_AppTheme


@Composable
fun HouseDetailScreen(
    house: House,
    onBackClick: () -> Unit,
    onSaveSuccess: (updatedHouse: House) -> Unit,
    viewModel: HouseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    // ใช้ rememberSaveable เพื่อจดจำค่าข้อความ และสถานะข้ามการหมุนจอ (Configuration Change)
    var thisMonthText by rememberSaveable {
        mutableStateOf(house.currentMonthReading?.toString() ?: "")
    }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var calculatedBillResult by rememberSaveable { mutableStateOf(house.calculatedBill) }
    val coroutineScope = rememberCoroutineScope()
    var isSaving by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // พื้นหลังหน้าสีขาว
    ) {
        // Top Header พร้อมปุ่มย้อนกลับ
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← ย้อนกลับ",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(end = 16.dp)
            )
            Text(
                text = "บันทึกมิเตอร์น้ำ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // การ์ดข้อมูลบ้าน
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "บ้านเลขที่: ${house.houseNumber}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "เจ้าของบ้าน: ${house.ownerName}",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val (statusText, statusTextColor, statusBgColor) = when (house.status) {
                        HouseStatus.RECORDED -> Triple("จดแล้ว", Color(0xFF1B5E20), Color(0xFFE8F5E9))
                        HouseStatus.UNRECORDED -> Triple("ยังไม่จด", Color(0xFF616161), Color(0xFFF5F5F5))
                        HouseStatus.UNREADABLE -> Triple("อ่านไม่ได้", Color(0xFFB71C1C), Color(0xFFFFEBEE))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "สถานะ: ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = statusBgColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            // ส่วนป้อนข้อมูลเลขมิเตอร์
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "กรอกข้อมูลมิเตอร์",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // แสดงเลขมิเตอร์เดือนก่อน
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "มิเตอร์เลขเดือนก่อน: ",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${house.lastMonthReading} หน่วย",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ตรวจสอบความถูกต้องว่าสามารถกดบันทึกได้หรือไม่
                    val thisMonthVal = thisMonthText.toIntOrNull()
                    val isInputValid = thisMonthText.isNotBlank() &&
                            thisMonthVal != null &&
                            thisMonthVal >= house.lastMonthReading &&
                            errorMessage == null

                    // กำหนดสีของ OutlinedTextField ตามสถานะความถูกต้อง
                    val textFieldColors = when {
                        errorMessage != null -> OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Red,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.Red,
                            cursorColor = Color.Red,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                        isInputValid -> OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2E7D32), // สีเขียวเหมือนปุ่ม
                            unfocusedBorderColor = Color(0xFF2E7D32), // สีเขียวเหมือนปุ่ม
                            focusedLabelColor = Color(0xFF2E7D32),
                            unfocusedLabelColor = Color(0xFF2E7D32),
                            cursorColor = Color(0xFF2E7D32),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                        else -> OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    }

                    // ช่องกรอกเลขมิเตอร์เดือนนี้
                    OutlinedTextField(
                        value = thisMonthText,
                        onValueChange = { input ->
                            thisMonthText = input
                            val valInt = input.toIntOrNull()
                            if (input.isBlank()) {
                                errorMessage = null
                            } else if (valInt == null) {
                                errorMessage = "⚠️ กรุณากรอกตัวเลขมิเตอร์ให้ถูกต้อง"
                            } else if (valInt < house.lastMonthReading) {
                                errorMessage = "⚠️ เลขมิเตอร์เดือนนี้ ($valInt) ต้องไม่น้อยกว่าเดือนก่อน (${house.lastMonthReading})"
                            } else {
                                errorMessage = null
                            }
                        },
                        label = { Text("ตัวเลขมิเตอร์เดือนนี้") },
                        placeholder = {
                            Text("เช่น 115", color = Color.Gray)
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        colors = textFieldColors,
                        singleLine = true,
                        isError = errorMessage != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // แสดงข้อความเตือนถ้ามีข้อผิดพลาด (เช่น เลขน้อยกว่าเดือนก่อน)
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ปุ่มบันทึกข้อมูล
                    Button(
                        onClick = {
                            if (thisMonthVal == null) return@Button

                            coroutineScope.launch {
                                isSaving = true
                                val result = viewModel.calculateAndSaveMeter(house, thisMonthVal)
                                if (result != null) {
                                    val (updatedHouse, bill) = result
                                    calculatedBillResult = bill
                                    onSaveSuccess(updatedHouse)
                                }
                                isSaving = false
                            }
                        },
                        enabled = isInputValid && !isSaving, // เปิด/ปิดปุ่มตามการตรวจสอบค่าเรียลไทม์
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32), // สีเขียวเมื่อเปิดใช้งาน
                            disabledContainerColor = Color(0xFFE0E0E0), // สีเทาเมื่อปิดใช้งาน
                            contentColor = Color.White,
                            disabledContentColor = Color(0xFF9E9E9E)
                        )
                    ) {
                        if (isSaving) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "กำลังบันทึก...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = when {
                                    thisMonthText.isBlank() -> "โปรดกรอกตัวเลขมิเตอร์เดือนนี้"
                                    !isInputValid -> "กรุณาแก้ไขเลขมิเตอร์ให้ถูกต้อง"
                                    else -> "บันทึกและคำนวณค่าน้ำ"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // แสดงผลลัพธ์การคำนวณค่าน้ำเมื่อบันทึกสำเร็จ
            if (calculatedBillResult != null && errorMessage == null) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "คำนวณค่าน้ำสำเร็จ! ✅",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ยอดค่าน้ำที่ต้องชำระ",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "$calculatedBillResult บาท",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HouseDetailScreenPreview() {
    NoAI_test_AppTheme {
        HouseDetailScreen(
            house = House(
                houseNumber = "105/2",
                ownerName = "สมชาย ใจดี",
                status = HouseStatus.UNRECORDED,
                lastMonthReading = 100
            ),
            onBackClick = {},
            onSaveSuccess = {}
        )
    }
}
