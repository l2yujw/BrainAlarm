package com.ryu.brainalarm.ui.createAlarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.ryu.brainalarm.R
import com.ryu.brainalarm.database.Alarm
import com.ryu.brainalarm.databinding.FragmentCreateAlarmBinding
import java.util.*

class CreateAlarmFragment : Fragment() {
    private lateinit var binding : FragmentCreateAlarmBinding
    private lateinit var viewModel : CreateAlarmViewModel
    val args : CreateAlarmFragmentArgs by navArgs()
    private var alarmArg : Alarm? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this, CreateAlarmViewModelFactory(requireActivity().application))
            .get(CreateAlarmViewModel::class.java)

        return inflater.inflate(R.layout.fragment_create_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateAlarmBinding.bind(view)

        //alarm이 null이 아니면 기존에 있던 알람이 눌린 것이므로 alarm 값 적용
        if(args.alarm != null){
            alarmArg = args.alarm!!
            setView()
        }

        //반복버튼이 눌렸는지에 따라 (레이아웃 자체 명임)옵션 보여줌
        binding.recurringCheckbox.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            if(isChecked)
                binding.recurringOptions.visibility = View.VISIBLE
            else
                binding.recurringOptions.visibility = View.GONE
        }

        //재설정 후 돌아감
        binding.scheduleAlarm.setOnClickListener {
            scheduleAlarm()
            navigate(it)
        }

        //삭제
        binding.trash.setOnClickListener {
            viewModel.delete(alarmArg!!)
            navigate(it)
        }

    }

    private fun setView(){
        binding.trash.visibility = View.VISIBLE

        binding.timePicker.currentHour = alarmArg!!.getHour()
        binding.timePicker.currentMinute = alarmArg!!.getMinute()

        binding.title.setText(alarmArg!!.getTitle())

        //반복 체크박스가 눌리면 체크여부 확인
        if(alarmArg!!.isRecurring()){
            binding.recurringCheckbox.isChecked = true
            binding.recurringOptions.visibility = View.VISIBLE
            binding.checkMonday.isChecked = alarmArg!!.getMonday()
            binding.checkTuesday.isChecked = alarmArg!!.getTuesday()
            binding.checkWednesday.isChecked = alarmArg!!.getWednesday()
            binding.checkThursday.isChecked = alarmArg!!.getThursday()
            binding.checkFriday.isChecked = alarmArg!!.getFriday()
            binding.checkSaturday.isChecked = alarmArg!!.getSaturday()
            binding.checkSunday.isChecked = alarmArg!!.getSunday()
        }

    }

    //다시 메인으로 돌아감
    private fun navigate(view: View)
            = Navigation.findNavController(view).navigate(R.id.action_createAlarmFragment_to_listAlarmFragment)

    private fun scheduleAlarm(){
        //기존꺼면 기존꺼 삭제
        if(alarmArg != null) {
            alarmArg!!.cancelAlarm(requireContext())
            viewModel.delete(alarmArg!!)
        }

        val alarm = getAlarm()//선택한 내용 전달
        viewModel.insert(alarm)//추가

        alarm.schedule(requireContext())
    }

    private fun getAlarm(alarmId : Int = Random().nextInt(Integer.MAX_VALUE)) : Alarm{
        return Alarm(
            alarmId,
            binding.timePicker.currentHour,
            binding.timePicker.currentMinute,
            binding.title.text.toString().capitalize(Locale.ROOT),
            System.currentTimeMillis(),
            true,
            binding.recurringCheckbox.isChecked,
            binding.checkMonday.isChecked,
            binding.checkTuesday.isChecked,
            binding.checkWednesday.isChecked,
            binding.checkThursday.isChecked,
            binding.checkFriday.isChecked,
            binding.checkSaturday.isChecked,
            binding.checkSunday.isChecked
        )
    }
}