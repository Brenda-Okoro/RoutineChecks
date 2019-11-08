package com.example.routinechecks.dagger

import com.example.routinechecks.fragments.DialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    internal abstract fun contributeDialogFragment(): DialogFragment

}