package com.instaconnect.android.dagger

import android.app.Application
import com.google.android.datatransport.runtime.dagger.BindsInstance
import com.google.android.datatransport.runtime.dagger.Component
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.dagger.builder.ActivityBuilder
import com.instaconnect.android.dagger.builder.FragmentBuilder
import com.instaconnect.android.dagger.module.AppModule
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilder::class, FragmentBuilder::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application?): Builder?
        fun build(): AppComponent?
    }

    fun inject(app: InstaConnectApp?)
}