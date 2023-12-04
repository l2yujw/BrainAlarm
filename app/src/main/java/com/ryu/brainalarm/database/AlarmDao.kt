package com.ryu.brainalarm.database

import androidx.lifecycle.LiveData
import androidx.room.*

// 데이터베이스에 접근할 때 DAO를 씀
@Dao
interface AlarmDao {
@Insert
fun insert(alarm : Alarm)

    @Query("delete from alarm_table")
    fun deleteAll()

    //LiveData의 ㅣifeCycle은 Observer의 LifeCycle을 따라가고 Observer는 해당하는 컴포넌트의 생명주기와 결합되기 때문에 해당 컴포넌트가 Destory 될 경우 메모리상에서 스스로 해제
    //LiveData는 Observer 패턴을 사용하여 자동으로 데이터가 업데이트 되기 때문에 추가적인 코드 없이 UI는 항상 최신화된 데이터 상태를 가질 수 있음
    //lifecycle은 코드가 실행되고 끝이 나는 과정을 주기로 나타낸 것

    //DB에 있는 모든 정보 가져오기
    @Query("select * from alarm_table order by created asc")
    fun getAlarms() : LiveData<List<Alarm>> // Query로 불러올 때 LiveData로 불러옴

    //DB에 있는 id가 일치하는 행 가져오기
    @Query("select * from alarm_table where alarmId == :alarmid")
    fun getAlarm(alarmid : Int) : Alarm

    @Update
    fun update(alarm : Alarm)

    @Delete
    fun delete(alarm: Alarm)
}