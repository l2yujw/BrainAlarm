package com.ryu.brainalarm.ui.createAlarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateAlarmViewModelFactory(val application: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateAlarmViewModel(application) as T
    }
}