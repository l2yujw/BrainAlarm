package com.ryu.brainalarm.ui.listAlarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ryu.brainalarm.database.Alarm
import com.ryu.brainalarm.database.AlarmRepository

//UI를 위한 데이터를 준비하는 역할 & 화면 전환시 데이터의 소멸을 방지할 수 있음( ViewModel )
class ListAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository : AlarmRepository = AlarmRepository(application)
    private val alarmsLiveData : LiveData<List<Alarm>> = alarmRepository.getAlarmsLiveData()

    fun update(alarm : Alarm){
        alarmRepository.update(alarm)
    }

    fun getAlarmsLiveData() = alarmsLiveData //alarmRepository와 같음

    fun getAlarm(alarmId : Int) = alarmRepository.getAlarm(alarmId)//alarmRepository와 같음
}