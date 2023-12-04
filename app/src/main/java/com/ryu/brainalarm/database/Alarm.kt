package com.ryu.brainalarm.database

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.ALARMID
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.FRIDAY
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.MONDAY
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.RECURRING
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.SATURDAY
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.SUNDAY
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.THURSDAY
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TITLE
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TUESDAY
import com.ryu.brainalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.WEDNESDAY
import java.io.Serializable
import java.util.*

//Room database가 이 dataclass가 entity라는걸 인식 & 데이터를 불러올 떄 테이블의 이름을 alarm_table로 정함
@Entity(tableName = "alarm_table")

//Alarm 클래스에 아래 변수들이 열로 만들어지게 됌 (속성)
data class Alarm(
    @PrimaryKey private var alarmId: Int,
    @NonNull
    private var hour: Int,
    private var minute: Int,
    private var title : String,
    private var created: Long,
    var started: Boolean,
    private var recurring: Boolean,
    private var monday: Boolean,
    private var tuesday: Boolean,
    private var wednesday: Boolean,
    private var thursday: Boolean,
    private var friday: Boolean,
    private var saturday: Boolean,
    private var sunday: Boolean,
) : Serializable { // 데이터들을 파일 저장 혹은 데이터 통신에서 파싱 할 수 있는 유의미한 데이터를 만들기 위함

    fun schedule(context : Context){
        //alarmmManager App이 꺼져있어도 Notification이 알리는 작업을 가능하게 해줌
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Broadcast receiver로 알람 적용 준비
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(RECURRING, recurring)
        intent.putExtra(MONDAY, monday)
        intent.putExtra(TUESDAY, tuesday)
        intent.putExtra(WEDNESDAY, wednesday)
        intent.putExtra(THURSDAY, thursday)
        intent.putExtra(FRIDAY, friday)
        intent.putExtra(SATURDAY, saturday)
        intent.putExtra(SUNDAY, sunday)
        intent.putExtra(ALARMID, alarmId)
        intent.putExtra(TITLE, title)

        //Intent 를 당장 수행하진 않고 특정 시점에 수행하도록 함, 보통 해당 앱이 구동되고 있지 않은 시점
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if(calendar.timeInMillis < System.currentTimeMillis())
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)

        //남은 시간 계산
        val timeDifference = calendar.timeInMillis - System.currentTimeMillis()

//        Toast.makeText(context, calculateHoursAndMinutes(timeDifference), Toast.LENGTH_SHORT).show()

        //https://developer.android.com/training/scheduling/alarms?hl=ko
        //https://developer.android.com/reference/android/app/AlarmManager#setAlarmClock(android.app.AlarmManager.AlarmClockInfo,%20android.app.PendingIntent)
        //https://developer.android.com/reference/android/app/AlarmManager.AlarmClockInfo
        //현재 시간에 남은시간을 더해 알람시간을 설정해서 알려준다 pendingIntent에는 알람 id가 들어감
        //기본 알람이 트리거 되는시간, 알람 시계의 세부 정보를 표시하거나 편집하는 데 사용할 수 있는 인텐트
        //알람이 울릴 때 사용자에게 알리는 데 사용할 알람 시계를 나타내는 알람을 예약
        // 반복이 아닐 경우
        if(recurring == false){
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(System.currentTimeMillis() + timeDifference, pendingIntent),
                pendingIntent
            )
        }
        // 반복일 경우
        else{
            alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeDifference,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        started = true
    }

    //시간 확인 용 테스트 문구
    fun calculateHoursAndMinutes(millis : Long) : String{
        var hour = (millis/(1000*60*60))
        var minutes = (millis/(1000*60))%60 + 1
        if(minutes >= 60) hour += 1; minutes = 0;

        val timeString = "$hour 시간 $minutes 분 후 알람"
        return timeString
    }

    //알람 취소 (토글)
    fun cancelAlarm(context : Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager.cancel(pendingIntent)
        started = false
    }

    //반복 될 날짜들 텍스트로 이어 붙힘
    fun getRecurringDays() : String?{
        if (!recurring) {
            return null
        }

        var days = ""
        if (monday) {
            days += "Mo "
        }
        if (tuesday) {
            days += "Tu "
        }
        if (wednesday) {
            days += "We "
        }
        if (thursday) {
            days += "Th "
        }
        if (friday) {
            days += "Fr "
        }
        if (saturday) {
            days += "Sa "
        }
        if (sunday) {
            days += "Su "
        }

        return days
    }

    fun getAlarmId() = alarmId
    fun getTitle() = title
    fun getHour() = hour
    fun getMinute() = minute
    fun isRecurring() = recurring
    fun getCreated() = created
    fun getMonday() = monday
    fun getTuesday() = tuesday
    fun getWednesday() = wednesday
    fun getThursday() = thursday
    fun getFriday() = friday
    fun getSaturday() = saturday
    fun getSunday() = sunday
    fun getMeridian() : String{
        return if(hour >= 12) "PM"
        else
            "AM"
    } // 오전 오후 확인

    private fun formatHour(hour : Int) : Int{
        return when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
    }
}