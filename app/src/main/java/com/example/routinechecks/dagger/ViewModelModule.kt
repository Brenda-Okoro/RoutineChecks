package com.example.routinechecks.dagger

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.routinechecks.viewmodels.RoutineListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
class ViewModelModule {

    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

    @Provides
    internal fun getViewModelFactory(providerMap: Map<Class<out ViewModel>, Provider<ViewModel>>): ViewModelFactory {
        return ViewModelFactory(providerMap)
    }

    @Binds
    @IntoMap
    @ViewModelKey(RoutineListViewModel::class)
    fun getRoutineListViewModel(application: Application, lifecycleOwner: LifecycleOwner): ViewModel {
        return RoutineListViewModel(application, lifecycleOwner)
    }

//    @Provides
//    @IntoMap
//    @ViewModelKey(RoutineDetailOccurrenceListViewModel::class)
//    fun getRoutineOccurenceListViewModel(application: Application, lifecycleOwner: LifecycleOwner): ViewModel {
//        return RoutineDetailOccurrenceListViewModel(application, lifecycleOwner)
//    }
}