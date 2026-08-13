package com.example.echo.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

fun startEchoService(context: Context, url: String) {
    Log.d("EchoService", "startEchoService вызван")
    val intent = Intent(context, EchoService::class.java).apply {
        action = EchoService.ACTION_PLAY
        putExtra("url", url)
    }
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        context.startForegroundService(intent)
        Log.d("EchoService", "startForegroundService вызван")
    }
    else {
        context.startService(intent)
        Log.d("EchoService", "startService вызван")
    }
}