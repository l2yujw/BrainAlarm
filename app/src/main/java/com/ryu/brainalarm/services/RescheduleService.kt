package com.ryu.brainalarm.services

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.ryu.brainalarm.database.AlarmRepository


class RescheduleService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val alarmRepository = AlarmRepository(application)

        //observer가 추가되면 이 observer는 항상 active 상태로 여겨지며, 따라서 변경에 대해 항상 notify 를 받을 수 있음
        alarmRepository.getAlarmsLiveData().observeForever { alarms ->
            for(alarm in alarms){
                if(alarm.started)
                    alarm.schedule(applicationContext)//초기 알람들 설정
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}