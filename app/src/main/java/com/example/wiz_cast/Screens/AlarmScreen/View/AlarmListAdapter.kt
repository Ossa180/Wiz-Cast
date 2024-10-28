package com.example.wiz_cast.Screens.AlarmScreen.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wiz_cast.Model.Alarm.Alarm
import com.example.wiz_cast.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// mutable to be dynamically updated
class AlarmListAdapter(private var alarmList: MutableList<Alarm>) : RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTimeTextView: TextView = view.findViewById(R.id.date_time_alarm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_alarm_list, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarmList[position]

        // Format date and time for display
        val date = Date(alarm.timeInMillis)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.dateTimeTextView.text = dateFormat.format(date)
    }

    override fun getItemCount(): Int = alarmList.size

    // Method to update data and notify the adapter
    fun setData(newAlarmList: List<Alarm>) {
        alarmList.clear()
        alarmList.addAll(newAlarmList)
        notifyDataSetChanged()
    }
}
