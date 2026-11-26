package com.example.noai_test_app

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus
import com.example.noai_test_app.example2.calcBill
import kotlinx.coroutines.delay

class HouseViewModel : ViewModel() {
    private val _housesList = mutableStateListOf(
        House("105/2", "สมชาย ใจดี", HouseStatus.RECORDED, lastMonthReading = 100, currentMonthReading = 115, calculatedBill = 85),
        House("101/1", "สมหญิง รักเรียน", HouseStatus.UNRECORDED, lastMonthReading = 120),
        House("110/5", "วิชัย กล้าหาญ", HouseStatus.UNREADABLE, lastMonthReading = 95),
        House("102/1", "สุดา มานะ", HouseStatus.UNRECORDED, lastMonthReading = 150),
        House("108/3", "กิตติ พัฒนา", HouseStatus.RECORDED, lastMonthReading = 200, currentMonthReading = 215, calculatedBill = 85),
        House("101/2", "นภา สดใส", HouseStatus.UNREADABLE, lastMonthReading = 80),
        House("104/1", "ประเสริฐ มั่งมี", HouseStatus.RECORDED, lastMonthReading = 310, currentMonthReading = 325, calculatedBill = 85),
        House("109/1", "สมศักดิ์ ยินดี", HouseStatus.UNRECORDED, lastMonthReading = 140),
        House("103/4", "อารี สุขใจ", HouseStatus.RECORDED, lastMonthReading = 220, currentMonthReading = 230, calculatedBill = 50),
        House("106/2", "มานพ ขยันยิ่ง", HouseStatus.UNREADABLE, lastMonthReading = 175)
    )
    val housesList: List<House> get() = _housesList

    fun updateHouse(updatedHouse: House) {
        val index = _housesList.indexOfFirst { it.houseNumber == updatedHouse.houseNumber }
        if (index != -1) {
            _housesList[index] = updatedHouse
        }
    }

    fun getHouseByNumber(houseNumber: String): House? {
        return _housesList.find { it.houseNumber == houseNumber }
    }

    // Suspend function สำหรับคำนวณและบันทึกข้อมูลพร้อมหน่วงเวลา
    suspend fun calculateAndSaveMeter(house: House, thisMonthVal: Int): Pair<House, Int>? {
        delay(2000)

        val bill = calcBill(house.lastMonthReading, thisMonthVal)
        if (bill != -1 && thisMonthVal >= house.lastMonthReading) {
            val updatedHouse = house.copy(
                status = HouseStatus.RECORDED,
                currentMonthReading = thisMonthVal,
                calculatedBill = bill
            )
            updateHouse(updatedHouse)
            return Pair(updatedHouse, bill)
        }
        return null
    }
}
