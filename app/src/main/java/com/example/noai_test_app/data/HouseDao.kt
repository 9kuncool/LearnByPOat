package com.example.noai_test_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseDao {
    @Query("SELECT * FROM houses")
    fun getAllHouses(): Flow<List<HouseEntity>>

    @Query("SELECT * FROM houses WHERE houseNumber = :houseNumber")
    suspend fun getHouseByNumber(houseNumber: String): HouseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouse(house: HouseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(houses: List<HouseEntity>)

    @Update
    suspend fun updateHouse(house: HouseEntity)

    @Query("SELECT COUNT(*) FROM houses")
    suspend fun getCount(): Int
}
