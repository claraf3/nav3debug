package com.example.basenav3debugsetup

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.basenav3debugsetup.ui.theme.BaseNav3DebugSetupTheme

object Home: NavKey

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), importance).apply {
                description = getString(R.string.description)
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        setContent {
            BaseNav3DebugSetupTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = listOf(Home),
                        entryProvider = entryProvider {
                            entry<Home> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = { sendNotification(
                                            this@MainActivity,
                                            NOTIFICATION_TITLE,
                                            NOTIFICATION_CONTENT
                                        )}
                                    ) {
                                        Text("Send notification for video")
                                    }
                                }

                            }
                        }
                    )
                }
            }
        }
    }
}

private fun sendNotification(
    activity: ComponentActivity,
    title: String,
    content: String,
) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setData(DEEPLINK_URL.toUri())
    intent.setPackage(DEEPLINK_URL_PACKAGE)

    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    val pendingIntent = PendingIntent.getActivity(
        activity,
        0,
        intent,
        flags
    )

    val builder = NotificationCompat.Builder(activity, CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(activity)) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission(activity)
            return@with
        }
        // notificationId is a unique int for each notification that you must define.
        notify(NOTIFICATION_ID, builder.build())
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun requestNotificationPermission(context: ComponentActivity) {
    ActivityCompat.requestPermissions(
        context,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        PERMISSION_REQUEST_CODE);
}

private const val CHANNEL_ID = "channel_1"
private const val NOTIFICATION_ID = 123
private const val PERMISSION_REQUEST_CODE = 100

private const val DEEPLINK_URL = "https://www.youtube.com/shorts/yQdGAAPvsJI"
private const val DEEPLINK_URL_PACKAGE = "com.google.android.youtube"

private const val NOTIFICATION_TITLE = "New Video Available"
private const val NOTIFICATION_CONTENT = "Check out this new short on Deep Linking " +
        "in Navigation 3"

