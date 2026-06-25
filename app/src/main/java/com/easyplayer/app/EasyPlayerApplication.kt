package com.easyplayer.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import com.easyplayer.app.core.common.di.ApplicationScope
import com.easyplayer.app.core.data.repository.PreferencesRepository
import com.easyplayer.app.crash.CrashActivity
import com.easyplayer.app.crash.GlobalExceptionHandler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

@HiltAndroidApp
class EasyPlayerApplication : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(applicationContext, CrashActivity::class.java))
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader
}
