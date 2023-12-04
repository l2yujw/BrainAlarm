package com.ryu.brainalarm.ui.listAlarm

import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ryu.brainalarm.R
import com.ryu.brainalarm.database.Alarm
import com.ryu.brainalarm.databinding.AlarmCardViewBinding


class AlarmViewHolder(binding : AlarmCardViewBinding, private val listener: OnToggleAlarmListener, private val itemClick : (alarm: Alarm) -> Unit)//Unit은 void와 같은 역할
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val alarmTime: TextView = binding.itemAlarmTime
    private val alarmRecurringDays: TextView = binding.itemAlarmRecurringDays
    private val alarmTitle: TextView = binding.itemAlarmTitle
    val alarmStarted : Switch = binding.itemAlarmStarted
    private lateinit var alarm : Alarm

    init {
        binding.root.setOnClickListener(this)
    }

    fun bind(alarm : Alarm){
        this.alarm = alarm
        val alarmText = String.format("%02d:%02d ${alarm.getMeridian()}", formatHour(alarm.getHour()), alarm.getMinute())
        alarmTime.text = alarmText
        alarmTitle.text = alarm.getTitle()
        alarmStarted.isChecked = alarm.started

        //반복 설정이 되어 있으면 이미지와 반복일들 표시
        if(alarm.isRecurring()){
            alarmRecurringDays.text = alarm.getRecurringDays()
        }
        //아니면 둘다 invisible
        else{
            alarmRecurringDays.text = ""
        }

        //alarm 토글버튼 여부 확인 해서 적용
        alarmStarted.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            listener.onToggle(alarm)
        }
    }

    //12시간 표기법 적용
    private fun formatHour(hour : Int) : Int{
        return when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
    }
    override fun onClick(view: View?) {
        itemClick(alarm)
    }
}