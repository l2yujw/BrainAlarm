package com.ryu.brainalarm.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.viewbinding.BuildConfig
import com.ryu.brainalarm.R
import com.ryu.brainalarm.activities.RingActivity
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.ALARMID
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.RECURRING
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TITLE
import com.ryu.brainalarm.database.AlarmRepository


class AlarmService : Service() {
    private lateinit var vibrator: Vibrator
    private lateinit var ringtone: MediaPlayer
    private lateinit var notificationManager: NotificationManager

   private val PRIMARY_CHANNEL_ID: String = "com.ryu.brainalarm" + "PRIMARY_CHANNEL_ID"
    private val NOTIFICATION_ID = 0

    private val TAG :String = "AlarmService"

    override fun onCreate() {
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator//진동

        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)//알람
        if(alarmUri == null)
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)//알림

        ringtone = MediaPlayer.create(this, alarmUri)//효과음
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ringIntent = Intent(this, RingActivity::class.java)
        val id = intent?.getIntExtra(ALARMID, 1)!!
        val title = intent?.getStringExtra(TITLE)
        val recurring = intent?.getBooleanExtra(RECURRING, true)

        //토글버튼 상태 끔으로 변경
        if (recurring != null) {
            setAlarmCheckedOff(id, recurring)
        }

        //알람 진동 시작
        ringNVibrate()

        createNotificationChannel()

        ringIntent.putExtra(TITLE, title)
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val notification = getNotification(ringIntent, title)
        notificationManager.notify(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
        vibrator.cancel()
        notificationManager.cancel(NOTIFICATION_ID)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun setAlarmCheckedOff(id: Int, recurring : Boolean) {
        if(!recurring){
            val alarmRepository = AlarmRepository(application)
            alarmRepository.stopAlarm(id)
        }
    }

    private fun ringNVibrate() {
        val pattern = longArrayOf(0, 100, 1000)
        vibrator.vibrate(pattern, 0)
        ringtone.isLooping = true
        ringtone.start()
    }

    //알림 생성
    private fun createNotificationChannel(){
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Alarm Notification",
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Notification from Alarm"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotification(intent : Intent, title : String?): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var message = "물 한잔과 함께 하루를 시작해보는건 어떨까요?"
        if(title != "")
            message = title!!
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("일어날 시간이에요~!~!")
            .setContentText(message)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.alarm_notification))
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .build()
    }


}