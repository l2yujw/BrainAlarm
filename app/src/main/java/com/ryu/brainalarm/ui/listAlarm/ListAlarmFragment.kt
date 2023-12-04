package com.ryu.brainalarm.ui.listAlarm

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ryu.brainalarm.R
import com.ryu.brainalarm.database.Alarm
import com.ryu.brainalarm.databinding.FragmentListAlarmBinding

class ListAlarmFragment : Fragment(), OnToggleAlarmListener {
    private lateinit var binding : FragmentListAlarmBinding
    private lateinit var viewModel : ListAlarmViewModel

    //뷰와 연결 후 반환
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(!Settings.canDrawOverlays(requireContext())){
                requestPermission()
            }
        }
        return inflater.inflate(R.layout.fragment_list_alarm, container, false)
    }

    //view의 초기화면 설정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListAlarmBinding.bind(view)

        val alarmRecyclerViewAdapter = AlarmRecyclerViewAdapter(this){
            navigate(view, it)
        }
        val recyclerView = binding.recylerView
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = alarmRecyclerViewAdapter

        // ViewModel에 Observe를 활용하여 AlarmViewModel에 getAlarmsLiveData()가 바뀌었을때 캐치하여, adapter에서 만들어준 setAlarms함수를 통해
        // 바뀐데이터를 UI에 업데이트
        viewModel = ViewModelProvider(this, ListAlarmViewModelFactory(requireActivity().application))
            .get(ListAlarmViewModel::class.java)
        viewModel.getAlarmsLiveData().observe(viewLifecycleOwner, { alarms ->
            alarmRecyclerViewAdapter.setAlarms(alarms)
        })

        // addAlarm버튼이 눌릴시 알람 생성 화면으로 전환
        binding.addAlarm.setOnClickListener {
            navigate(view, null)
        }
    }

    //CreateAlarmFragment로 넘어가는 동작 실행
    private fun navigate(view : View, alarm : Alarm?){
        val action = ListAlarmFragmentDirections.actionListAlarmFragmentToCreateAlarmFragment(alarm)
        Navigation.findNavController(view).navigate(action)
    }

    //토글이 상태를 체크해서 알람 설정 여부 결정
    override fun onToggle(alarm: Alarm) {
        if(alarm.started)
            alarm.cancelAlarm(requireContext())
        else
            alarm.schedule(requireContext())
        viewModel.update(alarm)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity?.packageName))
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}