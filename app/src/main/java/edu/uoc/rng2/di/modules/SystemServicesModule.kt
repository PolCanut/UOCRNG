package edu.uoc.rng2.di.modules

import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SystemServicesModule {
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun provideLocationManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}