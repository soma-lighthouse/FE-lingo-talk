package com.lighthouse.lingo_talk.di

import com.lighthouse.lingo_talk.MainNavigatorImpl
import com.lighthouse.navigation.MainNavigator
import com.lighthouse.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation(): Navigator {
        return Navigator()
    }

    @Provides
    @Singleton
    fun provideNavigator(): MainNavigator {
        return MainNavigatorImpl()
    }
}