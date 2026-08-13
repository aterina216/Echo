package com.example.echo.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.echo.EchoApplication
import com.example.echo.MainActivity
import com.example.echo.R
import com.example.echo.data.player.EchoPlayer
import com.example.echo.domain.player.MusicPlayer
import javax.inject.Inject

class EchoService: Service() {

    @Inject
    lateinit var musicPlayer: EchoPlayer

    private var currentUrl: String = ""

    private val notificationId = 101

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("EchoService", "onStartCommand, action = ${intent?.action}")
        (application as EchoApplication).appComponent.inject(this)
        Log.d("EchoService", "Инжект прошёл, echoPlayer = ${::musicPlayer.isInitialized}")

        intent?.getStringExtra("url")?.let { currentUrl = it }

        when(intent?.action) {
            ACTION_PLAY -> {

                if(!musicPlayer.isPlaying() && currentUrl.isNotEmpty()) {
                    musicPlayer.play(currentUrl)
                }

                val notification = buildNotification(true)
                startForeground(notificationId, notification)
                Log.d("EchoService", "startForeground выполнен")
            }
            ACTION_PAUSE -> {
                musicPlayer.pause()
                val notification = buildNotification(false)
                startForeground(notificationId, notification)
            }
            ACTION_STOP -> {
                musicPlayer.pause()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun updateNotification() {
        Log.d("EchoService", "updateNotification вызван")
        val notification = buildNotification(isPlaying = true)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
        Log.d("EchoService", "notify выполнен")
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(R.drawable.rounded_pause_circle_24, "Pause", getPauseIntent())
        }
        else {
            NotificationCompat.Action(R.drawable.rounded_play_circle_24, "Play", getPlayIntent())
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Echo")
            .setContentText(if (isPlaying) "Сейчас играет" else "На паузе")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(
                android.R.drawable.ic_menu_close_clear_cancel, "Stop", getStopIntent()
            ))
            .setOngoing(true)
            .build()
    }

    private fun getPlayIntent(): PendingIntent {
        val intent = Intent(this, EchoService::class.java).apply { action = ACTION_PLAY }
        return PendingIntent.getService(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPauseIntent(): PendingIntent {
        val intent = Intent(this, EchoService::class.java).apply { action = ACTION_PAUSE }
        return PendingIntent.getService(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getStopIntent(): PendingIntent {
        val intent = Intent(this, EchoService::class.java).apply { action = ACTION_STOP }
        return PendingIntent.getService(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object{
        const val CHANNEL_ID = "echo_playback"
        const val ACTION_PLAY = "com.example.echo.action.PLAY"
        const val ACTION_PAUSE = "com.example.echo.action.PAUSE"
        const val ACTION_STOP = "com.example.echo.action.STOP"
    }
}