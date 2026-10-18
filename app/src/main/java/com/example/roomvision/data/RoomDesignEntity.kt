package com.example.roomvision.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_designs")
data class RoomDesignEntity(
    @PrimaryKey val id: String,
    val name: String,
    val roomType: String,
    val furnitureJson: String,
    val wallDesignJson: String,
    val floorDesignJson: String,
    val estimatedBudget: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)
