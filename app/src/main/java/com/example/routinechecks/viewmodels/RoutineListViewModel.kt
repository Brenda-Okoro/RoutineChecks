package com.example.routinechecks.viewmodels

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.routinechecks.activities.repository.RoutineRepository
import com.example.routinechecks.database.Routines
import com.example.routinechecks.viewmodels.BaseViewModel.Companion.STATE_LOADED
import com.example.routinechecks.viewmodels.BaseViewModel.Companion.STATE_LOADING
import javax.inject.Inject

class RoutineListViewModel (mApplication: Application, private val lifecycleOwner: LifecycleOwner): BaseViewModel(mApplication) {
    private val mRoutineMutableLiveData: MutableLiveData<Routines> = MutableLiveData()
    private val mRoutineListMutableLiveData: MutableLiveData<List<Routines>> = MutableLiveData()

    fun getRoutineObserver(): LiveData<Routines> {
        return mRoutineMutableLiveData
    }

    fun getRoutineListObserver(): LiveData<List<Routines>> {
        return mRoutineListMutableLiveData
    }

    private fun setRoutine(routines: Routines?) {
        setUiState(STATE_LOADED)
        mRoutineMutableLiveData.postValue(routines)
    }

    private fun setRoutineList(routineList: List<Routines>?) {
        setUiState(STATE_LOADED)
        mRoutineListMutableLiveData.postValue(routineList)
    }

    fun getRoutineById(routineId: Int) {
        setUiState(STATE_LOADING)
        mExecutors?.diskIO()?.execute {
            mDb?.dao?.getRoutineById(routineId)?.observe(lifecycleOwner, Observer {
                setRoutine(it)
            })
        }
    }

    fun getAllRoutines() {
        setUiState(STATE_LOADING)
        mExecutors?.diskIO()?.execute {
            mDb?.dao?.getAllRoutine()?.observe(lifecycleOwner, Observer {
                setRoutineList(it)
            })
        }
    }

    fun addRoutine(routines: Routines) {
        mExecutors?.diskIO()?.execute {
            mDb?.dao?.addRoutine(routines)
        }
    }

    fun editRoutine(routines: Routines) {
        mExecutors?.diskIO()?.execute {
            mDb?.dao?.updateRoutine(routines)
        }
    }

    fun deleteRoutine(routines: Routines) {
        mExecutors?.diskIO()?.execute {
            mDb?.dao?.deleteRoutine(routines)
        }
    }

}
