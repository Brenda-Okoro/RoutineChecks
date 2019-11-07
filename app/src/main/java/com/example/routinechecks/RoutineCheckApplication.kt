package com.example.routinechecks

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.routinechecks.dagger.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class RoutineCheckApplication: Application(), HasAndroidInjector {
    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

}
val Activity.app get() = application as RoutineCheckApplication

val AndroidViewModel.app get() = getApplication<RoutineCheckApplication>()