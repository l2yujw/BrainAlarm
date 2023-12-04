package com.ryu.brainalarm.database

import android.app.Application
import androidx.lifecycle.LiveData


class AlarmRepository(val application: Application) {
    private var alarmDao : AlarmDao
    private var alarmsLiveData : LiveData<List<Alarm>>

    //db에 있는 모든 데이터 가져오기
    init {
        val alarmDb = AlarmDatabase.getDatabase(application)
        alarmDao = alarmDb!!.getAlarmDao()
        alarmsLiveData = alarmDao.getAlarms()
    }

    //Alarm 해제
    fun stopAlarm(alarmId : Int) {
        AlarmDatabase.databaseWriteExecutor.execute{
            //alarmid 와 일치하는 행 저장
            val alarm = alarmDao.getAlarm(alarmId)
            //그 결과의 행의 속성 started 값을 false로 바꿈
            alarm.started = false
            //alarm반영
            alarmDao.update(alarm)
        }
    }

    //Alarm에 반영되어 있는 값 DB에 저장
    fun insert(alarm : Alarm){
        AlarmDatabase.databaseWriteExecutor.execute {
            alarmDao.insert(alarm)
        }

    }

    fun update(alarm: Alarm){
        AlarmDatabase.databaseWriteExecutor.execute {
            alarmDao.update(alarm) //DB 업데이트
        }
    }

    //현재 alarm에 있는 데이터 호출
    fun getAlarmsLiveData() = alarmsLiveData

    //alarmid와 일치하는 행의 데이터 호출
    fun getAlarm(alarmId : Int) = alarmDao.getAlarm(alarmId)

    //DB에 alarm과 일치한 행 제거
    fun delete(alarm: Alarm){
        AlarmDatabase.databaseWriteExecutor.execute{
            alarmDao.delete(alarm)
        }
    }
}