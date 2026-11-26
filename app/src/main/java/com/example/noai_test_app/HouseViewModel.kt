package com.example.noai_test_app

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noai_test_app.data.AppDatabase
import com.example.noai_test_app.data.HouseEntity
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus
import com.example.noai_test_app.example2.calcBill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HouseViewModel(application: Application) : AndroidViewModel(application) {
    private val houseDao = AppDatabase.getDatabase(application).houseDao()

    private val _housesList = mutableStateListOf<House>()
    val housesList: List<House> get() = _housesList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (houseDao.getCount() == 0) {
                AppDatabase.populateInitialData(houseDao)
            }
            houseDao.getAllHouses().collectLatest { entities ->
                withContext(Dispatchers.Main) {
                    _housesList.clear()
                    _housesList.addAll(entities.map { it.toHouse() })
                }
            }
        }
    }

    fun updateHouse(updatedHouse: House) {
        viewModelScope.launch(Dispatchers.IO) {
            houseDao.updateHouse(HouseEntity.fromHouse(updatedHouse))
        }
    }

    suspend fun getHouseByNumber(houseNumber: String): House? {
        return withContext(Dispatchers.IO) {
            houseDao.getHouseByNumber(houseNumber)?.toHouse()
        }
    }

    // Suspend function สำหรับคำนวณและบันทึกข้อมูลพร้อมหน่วงเวลา
    suspend fun calculateAndSaveMeter(house: House, thisMonthVal: Int): Pair<House, Int>? {
        kotlinx.coroutines.delay(2000)

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
