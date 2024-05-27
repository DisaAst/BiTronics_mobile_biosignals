package com.bitronics.bitronicsmobilebiosignals.di

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.bitronics.bitronicsmobilebiosignals.MainActivity.Companion.activity
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import com.bitronics.bluetooth.EnableBluetooth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideEnableBluetooth(activity: Activity): EnableBluetooth {
        return EnableBluetooth.Base(activity)
    }

    @Provides
    @Singleton
    fun provideActivity(): Activity {
        return activity
    }


    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val manager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideMainRepository(@ApplicationContext context: Context): MainRepository {
        return MainRepository(context)
    }


    @Provides
    @Singleton
    fun provideBioSignalProcessor(): BioSignalProcessor{
        return BioSignalProcessor()
    }

}