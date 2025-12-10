package com.sdk.universalpay.context

import android.content.Context

object ContextProvider {
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getContext(): Context = appContext
        ?: throw IllegalStateException(
            "ContextProvider not initialized. Call ContextProvider.initialize(context) first"
        )
}
