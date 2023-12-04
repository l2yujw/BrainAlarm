package com.ryu.brainalarm.ui.createAlarm

import android.app.Application
import androidx.lifecycle.ViewModel
import com.ryu.brainalarm.database.Alarm
import com.ryu.brainalarm.database.AlarmRepository

class CreateAlarmViewModel(application  :Application) : ViewModel() {
    private var alarmRepository : AlarmRepository = AlarmRepository(application)

    fun insert(alarm : Alarm){
        alarmRepository.insert(alarm)
    }//데이터 전달

    fun update(alarm: Alarm){
        alarmRepository.update(alarm)
    }

    fun delete(alarm: Alarm){
        alarmRepository.delete(alarm)
    }//데이터 전달해서 삭제요청

}