package com.beyzaakyol.todolist


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.beyzaakyol.todolist.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var datePickerButton: Button
    private var dueTime: LocalTime? = null
    private var dueDate: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()


        if (taskItem != null)
        {
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            if(taskItem!!.dueTime() != null){
                dueTime = taskItem!!.dueTime()!!
                updateTimeButtonText()
            }
            if (taskItem!!.completedDate() != null){
                dueDate = taskItem!!.completedDate()!!
                updateDateButtonText()
            }
        }
        else
        {
            binding.taskTitle.text = "New Task"
        }

        datePickerButton = binding.datePickerButton
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }




        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener{
            saveAction()
        }
        binding.timePickerButton.setOnClickListener{
            openTimePicker()
        }
    }







    private fun showDatePickerDialog() {
        var selectedDate: String? = null

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                dueDate = LocalDate.of(year, month + 1, dayOfMonth)
                val formattedDate = "${dueDate?.dayOfMonth}.${dueDate?.monthValue}.${dueDate?.year}"
                datePickerButton.text = formattedDate
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
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


    private fun updateDateButtonText() {
        val formattedDate = "${dueDate?.dayOfMonth}.${dueDate?.monthValue}.${dueDate?.year}"
        binding.datePickerButton.text = formattedDate
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
        val dueTimeString = if(dueTime == null) null else TaskItem.timeFormatter.format(dueTime)
        val completedDateString = if(dueDate == null) null else TaskItem.dateFormatter.format(dueDate)
        if(taskItem == null)
        {
            val newTask= TaskItem(name,desc,dueTimeString, null)
            taskViewModel.addTaskItem(newTask)
        }
        else
        {
            taskItem!!.name = name
            taskItem!!.desc = desc
            taskItem!!.dueTimeString =dueTimeString
            taskItem!!.completedDateString = null
            taskViewModel.updateTaskItem(taskItem!!)
        }

        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

}