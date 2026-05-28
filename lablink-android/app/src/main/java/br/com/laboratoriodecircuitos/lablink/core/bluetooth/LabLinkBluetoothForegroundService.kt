package br.com.laboratoriodecircuitos.lablink.core.bluetooth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import br.com.laboratoriodecircuitos.lablink.MainActivity
import br.com.laboratoriodecircuitos.lablink.R

class LabLinkBluetoothForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            BluetoothForegroundServiceConfig.ACTION_STOP -> {
                stopForegroundService()
                return START_NOT_STICKY
            }

            else -> {
                startAsForegroundService()
            }
        }

        return START_STICKY
    }

    private fun startAsForegroundService() {
        val notification = buildNotification(
            title = "LabLink Bluetooth ativo",
            text = "Preparando conexão persistente com o módulo Bluetooth.",
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                BluetoothForegroundServiceConfig.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE,
            )
        } else {
            startForeground(
                BluetoothForegroundServiceConfig.NOTIFICATION_ID,
                notification,
            )
        }
    }

    private fun stopForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            BluetoothForegroundServiceConfig.NOTIFICATION_CHANNEL_ID,
            BluetoothForegroundServiceConfig.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Mantém a conexão Bluetooth ativa enquanto você usa o LabLink."
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(
        title: String,
        text: String,
    ): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val stopIntent = Intent(this, LabLinkBluetoothForegroundService::class.java).apply {
            action = BluetoothForegroundServiceConfig.ACTION_STOP
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(
            this,
            BluetoothForegroundServiceConfig.NOTIFICATION_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Encerrar",
                stopPendingIntent,
            )
            .build()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LabLinkBluetoothForegroundService::class.java).apply {
                action = BluetoothForegroundServiceConfig.ACTION_START
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, LabLinkBluetoothForegroundService::class.java).apply {
                action = BluetoothForegroundServiceConfig.ACTION_STOP
            }

            context.startService(intent)
        }
    }
}
