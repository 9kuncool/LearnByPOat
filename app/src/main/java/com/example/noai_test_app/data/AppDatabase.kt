package com.example.noai_test_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noai_test_app.example2.House
import com.example.noai_test_app.example2.HouseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [HouseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "house_database"
                )
                    .addCallback(HouseDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class HouseDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.houseDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: HouseDao) {
            val initialHouses = listOf(
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
            dao.insertAll(initialHouses.map { HouseEntity.fromHouse(it) })
        }
    }
}
