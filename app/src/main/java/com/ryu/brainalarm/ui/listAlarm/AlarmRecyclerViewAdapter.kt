package com.ryu.brainalarm.ui.listAlarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryu.brainalarm.database.Alarm
import com.ryu.brainalarm.databinding.AlarmCardViewBinding

class AlarmRecyclerViewAdapter(private val listener: OnToggleAlarmListener, private val itemClick : (alarm: Alarm) -> Unit)
    : RecyclerView.Adapter<AlarmViewHolder>() {

    private var alarms : List<Alarm> = listOf()

    // 레이아웃을 하나씩 적용해가면서 연결해 줌
    // 토글버튼 정보와 alarm 정보를 순차적으로 보내서 값을 저장시키기 위함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = AlarmCardViewBinding.inflate(view, parent, false)
        return AlarmViewHolder(binding, listener, itemClick)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position] //눌린 아이템 번호의 alarm을 인식시킴
        holder.bind(alarm) //해당 포지션의 알람의 레이아웃을 설정
    }

    override fun getItemCount() = alarms.size

    fun setAlarms(_alarms : List<Alarm>){
        alarms = _alarms //바뀐 알람 정보를 저장해주고
        notifyDataSetChanged() //변화를 인식시켜줌
    }

    override fun onViewRecycled(holder: AlarmViewHolder) {
        super.onViewRecycled(holder)
        holder.alarmStarted.setOnCheckedChangeListener(null)
    }
}