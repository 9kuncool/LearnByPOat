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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus
import com.example.noai_test_app.ui.theme.NoAI_test_AppTheme


@Composable
fun HouseDetailScreen(
    house: House,
    onBackClick: () -> Unit,
    onSaveSuccess: (updatedHouse: House) -> Unit,
    viewModel: HouseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    var thisMonthText by rememberSaveable {
        mutableStateOf(house.currentMonthReading?.toString() ?: "")
    }
    var selectedNote by rememberSaveable {
        mutableStateOf(house.note ?: "ไม่มีหมายเหตุ")
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val noteOptions = listOf(
        "ไม่มีหมายเหตุ",
        "มิเตอร์ชำรุด / มองไม่เห็นตัวเลข",
        "สุนัขดุ / ไม่อนุญาตให้เข้าพื้นที่",
        "บ้านปิด ไม่มีผู้อยู่อาศัย"
    )

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var calculatedBillResult by rememberSaveable { mutableStateOf(house.calculatedBill) }
    val coroutineScope = rememberCoroutineScope()
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var isSaveSuccess by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "บันทึกมิเตอร์น้ำ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← ย้อนกลับ",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onBackClick() }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // House Info Card
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

                    if (!house.note.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "หมายเหตุ: ${house.note}",
                            fontSize = 14.sp,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Meter input & Note selection card (ทุกสถานะมีเหมือนกัน)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "กรอกข้อมูลมิเตอร์และหมายเหตุ",
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

                    val thisMonthVal = thisMonthText.toIntOrNull()
                    val isInputValid = thisMonthText.isNotBlank() &&
                            thisMonthVal != null &&
                            thisMonthVal >= house.lastMonthReading &&
                            errorMessage == null

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
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color(0xFF2E7D32),
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
                            isSaveSuccess = false
                            if (input.isNotBlank()) {
                                selectedNote = "ไม่มีหมายเหตุ"
                            }
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

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- ปุ่มหมายเหตุ (อยู่ระหว่างช่องกรอกเดือนนี้ กับ ปุ่มบันทึกและคำนวณค่าน้ำ) ---
                    Text(
                        text = "หมายเหตุ (เลือกหากมีปัญหาในการอ่านมิเตอร์)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val isMeterFilled = thisMonthText.isNotBlank()
                    val noteBoxColor = if (isMeterFilled) Color(0xFFE0E0E0) else Color(0xFFF5F5F5)
                    val noteTextColor = if (isMeterFilled) Color.Gray else if (selectedNote == "ไม่มีหมายเหตุ") Color.Gray else Color(0xFFB71C1C)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(noteBoxColor, RoundedCornerShape(8.dp))
                            .then(
                                if (!isMeterFilled) {
                                    Modifier.clickable { expanded = true }
                                } else {
                                    Modifier
                                }
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMeterFilled) "กรอกเลขมิเตอร์เดือนนี้แล้ว" else selectedNote,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = noteTextColor
                            )
                            if (!isMeterFilled) {
                                Text(text = "▼", fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        if (!isMeterFilled) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                noteOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = Color.Black) },
                                        onClick = {
                                            selectedNote = option
                                            isSaveSuccess = false
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ปุ่มบันทึกและคำนวณค่าน้ำ
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSaving = true
                                isSaveSuccess = false
                                val finalNote = if (selectedNote == "ไม่มีหมายเหตุ") null else selectedNote
                                val valInt = thisMonthText.toIntOrNull()

                                if (valInt != null && valInt >= house.lastMonthReading) {
                                    val result = viewModel.calculateAndSaveMeter(house, valInt)
                                    if (result != null) {
                                        val (updatedHouse, bill) = result
                                        val finalHouse = if (finalNote != null) updatedHouse.copy(note = finalNote) else updatedHouse
                                        if (finalNote != null) {
                                            viewModel.updateHouse(finalHouse)
                                        }
                                        calculatedBillResult = bill
                                        isSaveSuccess = true
                                        onSaveSuccess(finalHouse)
                                    }
                                } else if (finalNote != null) {
                                    kotlinx.coroutines.delay(2000)
                                    val updatedHouse = house.copy(
                                        status = HouseStatus.UNREADABLE,
                                        currentMonthReading = null,
                                        calculatedBill = null,
                                        note = finalNote
                                    )
                                    viewModel.updateHouse(updatedHouse)
                                    isSaveSuccess = true
                                    onSaveSuccess(updatedHouse)
                                }
                                isSaving = false
                            }
                        },
                        enabled = (isInputValid || selectedNote != "ไม่มีหมายเหตุ") && !isSaving,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            disabledContainerColor = Color(0xFFE0E0E0),
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
                                    isSaveSuccess -> "บันทึกเสร็จสิ้น"
                                    selectedNote != "ไม่มีหมายเหตุ" && !isInputValid -> "บันทึกหมายเหตุ (อ่านไม่ได้)"
                                    !isInputValid -> "โปรดกรอกตัวเลขหรือเลือกหมายเหตุ"
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

            if (isSaveSuccess && errorMessage == null) {
                Spacer(modifier = Modifier.height(20.dp))
                if (calculatedBillResult != null) {
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
                } else if (selectedNote != "ไม่มีหมายเหตุ") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "บันทึกเสร็จสิ้น ✅",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "บันทึกหมายเหตุเรียบร้อยแล้ว",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = selectedNote,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C)
                            )
                        }
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
        HouseDetailSectionPreviewContent()
    }
}

@Composable
fun HouseDetailSectionPreviewContent() {
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
