package com.ryu.brainalarm.ui.listAlarm

import com.ryu.brainalarm.database.Alarm

interface OnToggleAlarmListener {
    fun onToggle(alarm : Alarm)
}//체크박스 확인