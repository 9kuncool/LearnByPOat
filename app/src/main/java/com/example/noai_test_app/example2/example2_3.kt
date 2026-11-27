package com.example.noai_test_app.example2

enum class HouseStatus(val label: String) {
    RECORDED("จดแล้ว"),
    UNRECORDED("ยังไม่จด"),
    UNREADABLE("อ่านไม่ได้")
}

// 1. สร้าง data class สำหรับเก็บข้อมูลบ้าน
data class House(
    val houseNumber: String,             // บ้านเลขที่
    val ownerName: String,               // ชื่อเจ้าของบ้าน
    var status: HouseStatus,             // สถานะการจดมิเตอร์
    val lastMonthReading: Int = 100,     // เลขมิเตอร์เดือนก่อน
    var currentMonthReading: Int? = null, // เลขมิเตอร์เดือนนี้
    var calculatedBill: Int? = null,      // ยอดค่าน้ำที่คำนวณได้ (บาท)
    var note: String? = null              // หมายเหตุ (กรณีอ่านไม่ได้)
)

fun main() { //list
    // 2. สร้างรายการบ้าน 10 หลัง (คละบ้านเลขที่และสถานะการจด)
    val houses = listOf(
        House("105/2", "สมชาย ใจดี", HouseStatus.RECORDED, lastMonthReading = 100, currentMonthReading = 115, calculatedBill = 85),
        House("101/1", "สมหญิง รักเรียน", HouseStatus.UNRECORDED, lastMonthReading = 120),
        House("110/5", "วิชัย กล้าหาญ", HouseStatus.UNREADABLE, note = "มิเตอร์ชำรุด / มองไม่เห็นตัวเลข"),
        House("102/1", "สุดา มานะ", HouseStatus.UNRECORDED, lastMonthReading = 150),
        House("108/3", "กิตติ พัฒนา", HouseStatus.RECORDED, lastMonthReading = 200, currentMonthReading = 215, calculatedBill = 85),
        House("101/2", "นภา สดใส", HouseStatus.UNREADABLE, note = "สุนัขดุ / ไม่อนุญาตให้เข้าพื้นที่"),
        House("104/1", "ประเสริฐ มั่งมี", HouseStatus.RECORDED, lastMonthReading = 310, currentMonthReading = 325, calculatedBill = 85),
        House("109/1", "สมศักดิ์ ยินดี", HouseStatus.UNRECORDED, lastMonthReading = 140),
        House("103/4", "อารี สุขใจ", HouseStatus.RECORDED, lastMonthReading = 220, currentMonthReading = 230, calculatedBill = 50),
        House("106/2", "มานพ ขยันยิ่ง", HouseStatus.UNREADABLE, note = "บ้านปิด ไม่มีผู้อยู่อาศัย")
    )

    // --- 1. หาเฉพาะบ้านที่ยังไม่จด ---
    println("=== 1. รายชื่อบ้านที่ยังไม่จดมิเตอร์ ===")
    val unrecordedHouses = houses.filter { it.status == HouseStatus.UNRECORDED }
    unrecordedHouses.forEach {
        println("บ้านเลขที่: ${it.houseNumber} | เจ้าของ: ${it.ownerName}")
    }

    // --- 2. เรียงตามบ้านเลขที่ ---
    println("\n=== 2. เรียงลำดับตามบ้านเลขที่ ===")
    val sortedHouses = houses.sortedBy { it.houseNumber }
    sortedHouses.forEach {
        println("บ้านเลขที่: ${it.houseNumber} | เจ้าของ: ${it.ownerName} (${it.status.label})")
    }

    // --- 3. ค้นหาด้วยชื่อเจ้าของ ---
    val keyword = "สม"
    println("\n=== 3. ผลการค้นหาเจ้าของบ้านที่มีคำว่า '$keyword' ===")
    val searchResults = houses.filter { it.ownerName.contains(keyword) }
    
    if (searchResults.isEmpty()) {
        println("ไม่พบข้อมูลเจ้าของบ้านที่ค้นหา")
    } else {
        searchResults.forEach { 
            println("บ้านเลขที่: ${it.houseNumber} | เจ้าของ: ${it.ownerName}")
        }
    }
}
