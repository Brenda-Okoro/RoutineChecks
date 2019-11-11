package com.example.routinechecks.dagger

import android.app.Application
import androidx.room.Room
import com.example.routinechecks.RoutineCheckApplication
import com.example.routinechecks.database.AppDatabase
import com.example.routinechecks.database.RoutineDao
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
internal class AppModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideApplication(application: RoutineCheckApplication): Application {
        return application
    }

    @Singleton
    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "routineApp_database")
//                .addMigrations()
            .build()
    }

//    @Singleton
//    @Provides
//    fun provideRoutineDao(appDatabase: AppDatabase): RoutineDao {
//        return appDatabase.routineDao()
//    }

    companion object {

        private val REQUEST_TIMEOUT = 60
    }
}