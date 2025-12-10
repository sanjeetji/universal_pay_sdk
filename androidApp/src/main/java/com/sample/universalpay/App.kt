package com.sample.universalpay

import android.app.Application
import com.sdk.universalpay.context.ContextProvider

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextProvider.initialize(this)
    }
}