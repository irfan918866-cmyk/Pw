package com.example

import android.app.Application
import android.os.Build
import android.webkit.WebView

class PWApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val currentProcess = getProcessName()
                if (packageName != currentProcess) {
                    WebView.setDataDirectorySuffix(currentProcess)
                }
            } catch (_: Exception) {}
        }
    }
}
