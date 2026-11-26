package com.example.noai_test_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus

@Entity(tableName = "houses")
data class HouseEntity(
    @PrimaryKey
    val houseNumber: String,
    val ownerName: String,
    val status: String, // Store HouseStatus name: RECORDED, UNRECORDED, UNREADABLE
    val lastMonthReading: Int,
    val currentMonthReading: Int?,
    val calculatedBill: Int?
) {
    fun toHouse(): House {
        return House(
            houseNumber = houseNumber,
            ownerName = ownerName,
            status = try { HouseStatus.valueOf(status) } catch (e: Exception) { HouseStatus.UNRECORDED },
            lastMonthReading = lastMonthReading,
            currentMonthReading = currentMonthReading,
            calculatedBill = calculatedBill
        )
    }

    companion object {
        fun fromHouse(house: House): HouseEntity {
            return HouseEntity(
                houseNumber = house.houseNumber,
                ownerName = house.ownerName,
                status = house.status.name,
                lastMonthReading = house.lastMonthReading,
                currentMonthReading = house.currentMonthReading,
                calculatedBill = house.calculatedBill
            )
        }
    }
}
