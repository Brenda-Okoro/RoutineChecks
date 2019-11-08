package com.example.routinechecks.dagger

import androidx.lifecycle.ViewModel
import com.example.routinechecks.viewmodels.RoutineListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RoutineListViewModel::class)
    abstract fun bindPostViewModel(routineListViewModel: RoutineListViewModel): ViewModel

}