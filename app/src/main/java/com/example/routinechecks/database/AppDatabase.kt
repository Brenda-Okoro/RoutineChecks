package com.example.routinechecks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Creating the AppDatabase
@Database(entities = [Routines:: class, RoutineOccurrence::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private val LOCK = Any()
        private val DATABASE_NAME = "routineApp_database"
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder<AppDatabase>(context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME)
                        .build()
                }
            }

            return sInstance
        }
    }

    abstract val dao: RoutineDao
}