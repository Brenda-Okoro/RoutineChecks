package com.example.routinechecks.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE id=:id")
    fun getRoutineById(id: Int): LiveData<Routines>

    @Query("SELECT * FROM routines WHERE id=:id")
    fun getRoutineByIdAsync(id: Int): Routines

    @Query("SELECT * FROM routines")
    fun getAllRoutine(): LiveData<List<Routines>>

    @Insert
    fun addRoutine(routine: Routines)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRoutine(routine: Routines)

    @Delete
    fun deleteRoutine(routine: Routines)
}