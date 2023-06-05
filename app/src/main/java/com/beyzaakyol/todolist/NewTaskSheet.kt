package com.beyzaakyol.todolist


import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.beyzaakyol.todolist.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalTime

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTime: LocalTime? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (taskItem != null)
        {
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            if(taskItem!!.dueTime != null){
                dueTime = taskItem!!.dueTime!!
                updateTimeButtonText()
            }
        }
        else
        {
            binding.taskTitle.text = "New Task"
        }

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener{
            saveAction()
        }
        binding.timePickerButton.setOnClickListener{
            openTimePicker()
        }
    }


    private fun openTimePicker() {
        if(dueTime == null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dueTime = LocalTime.now()
            }
        val listener = TimePickerDialog.OnTimeSetListener{ _, selectedHour, selectedMinute ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dueTime = LocalTime.of(selectedHour,selectedMinute)
            }
            updateTimeButtonText()
        }
        val dialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TimePickerDialog(activity, listener, dueTime!!.hour, dueTime!!.minute, true)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        dialog.setTitle("Task Due")
        dialog.show()
    }

    private fun updateTimeButtonText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.timePickerButton.text = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun saveAction()
    {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()

        if(taskItem == null)
        {
            val newTask= TaskItem(name,desc,dueTime,null)
            taskViewModel.addTaskItem(newTask)
        }
        else
        {
            taskViewModel.updateTaskItem(taskItem!!.id, name, desc, dueTime)
        }

        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

}