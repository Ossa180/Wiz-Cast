package com.example.wiz_cast.Screens.AlarmScreen.View

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wiz_cast.Model.Alarm.Alarm
import com.example.wiz_cast.Model.DataBase.WeatherDatabase
import com.example.wiz_cast.R
import com.example.wiz_cast.Utils.Alarm.AlarmReceiver
import com.example.wiz_cast.databinding.FragmentAlarmBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private lateinit var alarmManager: AlarmManager
    private var selectedTimeInMillis: Long = 0L // Store selected alarm time
    lateinit var alarmListAdapter: AlarmListAdapter
    private val alarmBroadcastReceiver = AlarmBroadcastReceiver()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setupRecyclerView()
        removeExpiredAlarmsAndLoad()

        binding.btnAddAlarm.setOnClickListener {
            showDatePicker()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        // Pass a lambda to handle clicks on alarms
        alarmListAdapter = AlarmListAdapter(mutableListOf()) { alarm ->
            showDeleteConfirmationDialog(alarm)
        }
        binding.recyclerViewAlarms.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAlarms.adapter = alarmListAdapter
    }

    private fun showDeleteConfirmationDialog(alarm: Alarm) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAlarm(alarm)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAlarm(alarm: Alarm) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = WeatherDatabase.getInstance(requireContext())
            db.alarmDao().deleteAlarm(alarm.id)
            val updatedAlarms = db.alarmDao().getAllAlarms()

            withContext(Dispatchers.Main) {
                alarmListAdapter.setData(updatedAlarms)
                Toast.makeText(requireContext(), "Alarm deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeExpiredAlarmsAndLoad() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = WeatherDatabase.getInstance(requireContext())
            val currentTime = System.currentTimeMillis()

            // Delete expired alarms
            db.alarmDao().deleteExpiredAlarms(currentTime)
            // Load alarms from database
            val alarmList = db.alarmDao().getAllAlarms()

            withContext(Dispatchers.Main) {
                alarmListAdapter.setData(alarmList)
            }
        }
    }

    private fun saveAlarmToDatabase(timeInMillis: Long) {
        val alarm = Alarm(timeInMillis = timeInMillis)
        lifecycleScope.launch(Dispatchers.IO) {
            val db = WeatherDatabase.getInstance(requireContext())
            val alarmId = db.alarmDao().insertAlarm(alarm).toInt()
            val alarmList = db.alarmDao().getAllAlarms()

            withContext(Dispatchers.Main) {
                alarmListAdapter.setData(alarmList)
            }
        }
    }

    private inner class AlarmBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            if (alarmId != -1) {
                // Delete the triggered alarm and refresh the RecyclerView
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = WeatherDatabase.getInstance(requireContext())
                    db.alarmDao().deleteAlarm(alarmId)
                    val updatedAlarms = db.alarmDao().getAllAlarms()

                    withContext(Dispatchers.Main) {
                        alarmListAdapter.setData(updatedAlarms)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register receiver with RECEIVER_NOT_EXPORTED to avoid SecurityException on Android 12+
        ContextCompat.registerReceiver(
            requireContext(),
            alarmBroadcastReceiver,
            IntentFilter("com.example.wiz_cast.ALARM_TRIGGERED"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(alarmBroadcastReceiver)
    }

    // Show Date Picker
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            showTimePicker(calendar)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        // Lock previous dates
        datePicker.datePicker.minDate = System.currentTimeMillis()

        datePicker.show()
    }

    // Show Time Picker
    private fun showTimePicker(calendar: Calendar) {
        val timePicker = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Store the selected time
            selectedTimeInMillis = calendar.timeInMillis
            checkAndRequestExactAlarmPermission(selectedTimeInMillis)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePicker.show()
    }

    private fun setAlarm(timeInMillis: Long) {
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        // Show a toast message to confirm alarm setting
        val date = Date(timeInMillis)
        Toast.makeText(requireContext(), "Alarm set for: $date", Toast.LENGTH_SHORT).show()

        // Save to database
        saveAlarmToDatabase(timeInMillis)
    }

    private fun checkAndRequestExactAlarmPermission(timeInMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt user to manually allow exact alarms
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also { intent ->
                    intent.data = Uri.parse("package:${requireContext().packageName}")
                    startActivity(intent)
                }
            } else {
                // Permission already granted; set the alarm
                setAlarm(timeInMillis)
            }
        } else {
            // API level < 31, no need to check for exact alarm permission
            setAlarm(timeInMillis)
        }
    }
}
