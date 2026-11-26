package com.example.noai_test_app.example2

// ฟังก์ชันคิดค่าน้ำ (ตัวหลัก)
fun calcWaterBill(lastMonth: Int, thisMonth: Int): Int {
    if (thisMonth < lastMonth) {
        return -1 // คืนค่า -1 เพื่อบอกว่า "ข้อมูลผิดพลาด"
    }

    val unitsUsed = thisMonth - lastMonth
    var money = 0

    if (unitsUsed <= 10) {
        money = unitsUsed * 5
    } else if (unitsUsed <= 20) {
        money = (10 * 5) + ((unitsUsed - 10) * 7)
    } else {
        money = (10 * 5) + (10 * 7) + ((unitsUsed - 20) * 9)
    }

    return money
}

// ฟังก์ชันช่วยตรวจคำตอบ (ถ้าตรงแสดงคำว่า ผ่าน)
fun checkTest(caseName: String, actual: Int, expected: Int) {
    if (actual == expected) {
        println("✅ ผ่าน | $caseName -> ได้ $actual บาท")
    } else {
        println("❌ พลาด | $caseName -> ได้ $actual บาท (ที่ถูกคือ $expected บาท)")
    }
}

fun main() {
    // แบบที่ 1: ไม่ได้ใช้น้ำเลย (0 หน่วย) -> ต้องจ่าย 0 บาท
    checkTest("ไม่ได้ใช้น้ำ (100 -> 100)", calcWaterBill(100, 100), 0)

    // แบบที่ 2: ช่วงที่ 1 (1-10 หน่วย) ทดสอบที่ขอบเขต 10 หน่วย -> 10 * 5 = 50 บาท
    checkTest("ใช้ 10 หน่วยพอดี (100 -> 110)", calcWaterBill(100, 110), 50)

    // แบบที่ 3: ช่วงที่ 2 (11-20 หน่วย) ตามโจทย์ 15 หน่วย -> (10*5) + (5*7) = 85 บาท
    checkTest("ใช้ 15 หน่วย (100 -> 115)", calcWaterBill(100, 115), 85)

    // แบบที่ 4: ช่วงที่ 2 ขอบเขตบน 20 หน่วยพอดี -> (10*5) + (10*7) = 120 บาท
    checkTest("ใช้ 20 หน่วยพอดี (100 -> 120)", calcWaterBill(100, 120), 120)

    // แบบที่ 5: ช่วงที่ 3 (21 หน่วยขึ้นไป) ทดสอบ 25 หน่วย -> 50 + 70 + (5*9) = 165 บาท
    checkTest("ใช้ 25 หน่วย (100 -> 125)", calcWaterBill(100, 125), 165)

    // แบบที่ 6: ข้อมูลผิดพลาด เลขเดือนนี้น้อยกว่าเดือนก่อน -> ต้องคืนค่า -1
    checkTest("เลขมิเตอร์ผิด (150 -> 140)", calcWaterBill(150, 140), 0)
}