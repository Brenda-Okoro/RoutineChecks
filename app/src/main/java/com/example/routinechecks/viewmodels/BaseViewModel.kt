package com.example.routinechecks.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.routinechecks.database.AppDatabase
import com.example.routinechecks.utils.App
import com.example.routinechecks.utils.AppExecutors

/**
 *
 * This is the Base Class for the Application ViewModels
 *
 * @property mApplication is the Application instance of the Caller or the LifeCycle owner
 * that's instantiating the ViewModel
 *
 * @property mDb is the app database reference
 * @property mExecutors is the instance of the Threading provider class
 * @property uiState gives the current state of the UI
 *
 *
 * */
open class BaseViewModel(mApplication: Application) : AndroidViewModel(mApplication) {

    protected var mDb: AppDatabase? = AppDatabase.getInstance(mApplication)
    protected var mExecutors: AppExecutors? = App.getsExecutors()

    private val uiState = MutableLiveData<Int>()

    protected fun setUiState(state: Int) {
        uiState.postValue(state)
    }

    fun getUiStateObserver(): LiveData<Int> {
        return uiState
    }

    companion object {
        val STATE_LOADING = 0
        val STATE_LOADED = 1
    }
}