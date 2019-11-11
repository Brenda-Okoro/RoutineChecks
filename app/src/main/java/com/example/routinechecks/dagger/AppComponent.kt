package com.example.routinechecks.dagger

import com.example.routinechecks.RoutineCheckApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: RoutineCheckApplication): Builder

        fun build(): AppComponent
    }

    fun inject(application: RoutineCheckApplication)
}