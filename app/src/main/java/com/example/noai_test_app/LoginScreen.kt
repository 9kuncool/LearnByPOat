package com.example.noai_test_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noai_test_app.ui.theme.NoAI_test_AppTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // App Header / Title
        Text(
            text = "เข้าสู่ระบบ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            textAlign = TextAlign.Center
        )
        Text(
            text = "ระบบบันทึกมิเตอร์น้ำ",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Mockup Credentials Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 ข้อมูลเข้าสู่ระบบทดสอบ (Mockup)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ชื่อผู้ใช้ (Username): user123\nรหัสผ่าน (Password): 123456",
                    fontSize = 13.sp,
                    color = Color(0xFF1565C0)
                )
            }
        }

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { input ->
                username = input
                errorMessage = null
            },
            label = { Text("ชื่อผู้ใช้ (Username)") },
            placeholder = { Text("กรอกชื่อผู้ใช้ เช่น user123") },
            singleLine = true,
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { input ->
                password = input
                errorMessage = null
            },
            label = { Text("รหัสผ่าน (Password)") },
            placeholder = { Text("กรอกรหัสผ่าน เช่น 123456") },
            singleLine = true,
            isError = errorMessage != null,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Text(
                    text = if (isPasswordVisible) "ซ่อน" else "แสดง",
                    color = Color.Blue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { isPasswordVisible = !isPasswordVisible }
                        .padding(horizontal = 12.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Error Message Display
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFB71C1C),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Login Button
        Button(
            onClick = {
                // ตรวจหาช่องว่างหรือค่าว่าง
                when {
                    username.isEmpty() || password.isEmpty() -> {
                        errorMessage = "⚠️ กรุณากรอกชื่อผู้ใช้และรหัสผ่านให้ครบถ้วน"
                    }
                    username.isBlank() || password.isBlank() -> {
                        errorMessage = "⚠️ กรุณากรอกข้อมูลโดยต้องไม่เป็นช่องว่าง"
                    }
                    username.contains(" ") || password.contains(" ") -> {
                        errorMessage = "⚠️ ชื่อผู้ใช้และรหัสผ่านต้องไม่มีช่องว่าง (Space)"
                    }
                    (username != "user123") || (password != "123456") -> {
                        errorMessage = "⚠️ ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง (ลองใช้ user123 / 123456)"
                    }
                    else -> {
                        errorMessage = null
                        onLoginSuccess()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(
                text = "เข้าสู่ระบบ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NoAI_test_AppTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
