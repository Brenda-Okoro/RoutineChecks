package com.example.routinechecks.dagger

import com.example.routinechecks.activities.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/*
 * Any activity that needs injection should be added here
 */
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity
}