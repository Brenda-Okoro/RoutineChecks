package com.example.routinechecks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.routinechecks.Routines

// Creating the AppDatabase
@Database(entities = [Routines:: class], version = 1)
abstract class AppDatabase : RoomDatabase() {
//    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "routineApp_database")
            .build()
    }
}