package com.example.roomvision.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDesignDao {
    @Query("SELECT * FROM room_designs ORDER BY updatedAt DESC")
    fun getAllDesigns(): Flow<List<RoomDesignEntity>>

    @Query("SELECT * FROM room_designs WHERE id = :id LIMIT 1")
    suspend fun getDesignById(id: String): RoomDesignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesign(design: RoomDesignEntity)

    @Update
    suspend fun updateDesign(design: RoomDesignEntity)

    @Query("DELETE FROM room_designs WHERE id = :id")
    suspend fun deleteDesignById(id: String)
}
