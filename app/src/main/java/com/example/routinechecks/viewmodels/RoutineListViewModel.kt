package com.example.routinechecks.viewmodels

import androidx.lifecycle.ViewModel
import com.example.routinechecks.activities.repository.RoutineRepository
import javax.inject.Inject

class RoutineListViewModel @Inject constructor (private val routineRepository: RoutineRepository): ViewModel() {

}
