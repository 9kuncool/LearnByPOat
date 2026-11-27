// 1. สร้าง data class สำหรับเก็บข้อมูลบ้าน
data class House(
    val houseNumber: String, // บ้านเลขที่
    val ownerName: String,   // ชื่อเจ้าของบ้าน
    val status: String       // สถานะ ("จดแล้ว" หรือ "ยังไม่จด")
)

fun main() {
    // 2. ทำรายการบ้าน 10 หลัง
    val houses = listOf(
        House("105/2", "สมชาย ใจดี", "จดแล้ว"),
        House("101/1", "สมหญิง รักเรียน", "ยังไม่จด"),
        House("110/5", "วิชัย กล้าหาญ", "ยังไม่จด"),
        House("102/1", "สุดา มานะ", "ยังไม่จด"),
        House("108/3", "กิตติ พัฒนา", "จดแล้ว"),
        House("101/2", "นภา สดใส", "ยังไม่จด"),
        House("104/1", "ประเสริฐ มั่งมี", "จดแล้ว"),
        House("109/1", "สมศักดิ์ ยินดี", "ยังไม่จด"),
        House("103/4", "อารี สุขใจ", "จดแล้ว"),
        House("106/2", "มานพ ขยันยิ่ง", "ยังไม่จด")
    )

    // --------------------------------------------------
    // 1. หาเฉพาะบ้านที่ยังไม่จด
    // --------------------------------------------------
    println("=== 1. บ้านที่ยังไม่จด ===")
    val unrecordedHouses = houses.filter { it.status == "ยังไม่จด" }
    unrecordedHouses.forEach { house ->
        println("บ้านเลขที่: ${house.houseNumber} | เจ้าของ: ${house.ownerName}")
    }

    // --------------------------------------------------
    // 2. เรียงตามบ้านเลขที่
    // --------------------------------------------------
    println("\n=== 2. เรียงลำดับตามบ้านเลขที่ ===")
    val sortedHouses = houses.sortedBy { it.houseNumber }
    sortedHouses.forEach { house ->
        println("บ้านเลขที่: ${house.houseNumber} | เจ้าของ: ${house.ownerName} (${house.status})")
    }

    // --------------------------------------------------
    // 3. ค้นหาด้วยชื่อเจ้าของ
    // --------------------------------------------------
    val keyword = "สม"
    println("\n=== 3. ค้นหาเจ้าของบ้านที่มีคำว่า '$keyword' ===")
    val searchResults = houses.filter { it.ownerName.contains(keyword) }
    searchResults.forEach { house ->
        println("บ้านเลขที่: ${house.houseNumber} | เจ้าของ: ${house.ownerName}")
    }
}