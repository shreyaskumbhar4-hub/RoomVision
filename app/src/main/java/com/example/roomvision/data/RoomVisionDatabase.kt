package com.example.roomvision.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomDesignEntity::class], version = 1, exportSchema = false)
abstract class RoomVisionDatabase : RoomDatabase() {
    abstract fun roomDesignDao(): RoomDesignDao

    companion object {
        @Volatile
        private var INSTANCE: RoomVisionDatabase? = null

        fun getDatabase(context: Context): RoomVisionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomVisionDatabase::class.java,
                    "roomvision_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
