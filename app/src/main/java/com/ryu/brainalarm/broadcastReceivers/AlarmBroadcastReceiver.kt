package com.ryu.brainalarm.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ryu.brainalarm.services.AlarmService
import com.ryu.brainalarm.services.RescheduleService
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG : String = "AlarmBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // 이 브로드캐스트는 부팅이 시작된 뒤 딱 한 번만 전달
        if(Intent.ACTION_BOOT_COMPLETED == intent.action){
            startRescheduleAlarmsService(context,intent)
        }
        else{
            //반복이 아니라면 1회 실행
            if(!intent.getBooleanExtra(RECURRING, false))
                startAlarmService(context, intent, false)
            else{
                //반복이면 반복되는 날짜 확인해서 실행
                if(alarmsIntent(intent))
                    startAlarmService(context, intent, true)
            }
        }
    }

    private fun alarmsIntent(intent: Intent) : Boolean{
        val calendar = Calendar.getInstance()

        return when(calendar.get(Calendar.DAY_OF_WEEK)){//오늘 날짜가 설정한 날인지 확인
            Calendar.MONDAY ->{
                intent.getBooleanExtra(MONDAY, false)
            }
            Calendar.TUESDAY ->{
                intent.getBooleanExtra(TUESDAY, false)
            }
            Calendar.WEDNESDAY ->{
                intent.getBooleanExtra(WEDNESDAY, false)
            }
            Calendar.THURSDAY ->{
                intent.getBooleanExtra(THURSDAY, false)
            }
            Calendar.FRIDAY ->{
                intent.getBooleanExtra(FRIDAY, false)
            }
            Calendar.SATURDAY ->{
                intent.getBooleanExtra(SATURDAY, false)
            }
            Calendar.SUNDAY ->{
                intent.getBooleanExtra(SUNDAY, false)
            }
            else -> false
        }
    }

    //alarm 시작
    private fun startAlarmService(context: Context, intent : Intent, recurring : Boolean){
        val serviceIntent = Intent(context, AlarmService::class.java)
        val alarmId = intent.getIntExtra(ALARMID, 0)
        serviceIntent.putExtra(ALARMID, alarmId)
        serviceIntent.putExtra(TITLE, intent.getStringExtra(TITLE))
        serviceIntent.putExtra(RECURRING, recurring)

        startServiceIntent(context, serviceIntent)
    }

    private fun startRescheduleAlarmsService(context: Context, @Suppress("UNUSED_PARAMETER")intent : Intent) {
        val rescheduleIntent = Intent(context, RescheduleService::class.java)

        startServiceIntent(context, rescheduleIntent)
    }

    //alarm 실행
    private fun startServiceIntent(context : Context, intent : Intent){
        context.startService(intent)
    }

    companion object{
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
        const val RECURRING = "RECURRING"
        const val TITLE = "TITLE"
        const val ALARMID = "ALARMID"
    }
}