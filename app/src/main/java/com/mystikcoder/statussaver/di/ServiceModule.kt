package com.mystikcoder.statussaver.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mystikcoder.statussaver.R
import com.mystikcoder.statussaver.framework.presentation.ui.activity.HomeActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @SuppressLint("InlinedApi")
    @ServiceScoped
    @Provides
    fun providesBaseNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, "Foreground Service Notification")
            .setSmallIcon(R.drawable.ic_status_splash)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    12313,
                    Intent(context, HomeActivity::class.java),
                    0 or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
}