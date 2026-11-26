package com.example.noai_test_app.example2

// ข้อ 2_1
fun calcBill(lastMonth: Int, thisMonth: Int): Int {
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

fun main() {
    val result = calcBill(100,115)

    if (result == -1) {
        println("ผิด") // ถ้าเท่ากับ -1 ให้แสดงคำว่า ผิด
    } else {
        println("ต้องจ่าย $result บาท") // ถ้าปกติ ให้แสดงยอดเงิน
    }
}

