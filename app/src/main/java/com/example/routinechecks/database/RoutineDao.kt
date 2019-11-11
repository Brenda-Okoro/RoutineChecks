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

    @Query("SELECT * FROM routine_occurrence WHERE alarm_id=:id")
    fun getRoutineOccurrenceByAlarmId(id: Int): RoutineOccurrence

    @Query("SELECT * FROM routine_occurrence WHERE routine_id = :id AND NOT status=0")
    fun getAllRoutineOccurrences(id: Int): LiveData<List<RoutineOccurrence>>

    @Insert
    fun addOccurrence(occurrence: RoutineOccurrence)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOccurrence(currentOccurrence: RoutineOccurrence)
}