package com.ryu.brainalarm.ui.listAlarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// viewModel 개체를 인스턴스화 함
// 즉, ViewModelFactory는 ViewModel을 통해 전달되는 인자가 있을 때 사용

// ViewModelProvider.Factory를 확장함.
// 오버라이드 하면 아래와 같은 create 함수를 받을 수 있음.
class ListAlarmViewModelFactory(val application : Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListAlarmViewModel(application) as T
    }
}