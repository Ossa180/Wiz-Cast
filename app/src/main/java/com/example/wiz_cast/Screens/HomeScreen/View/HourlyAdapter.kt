package com.example.wiz_cast.Screens.HomeScreen.View

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wiz_cast.Model.Pojo.Item0
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.RecyclerHoursBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyAdapter(private val hourlyList: List<Item0>) :
    RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    inner class HourlyViewHolder(val binding: RecyclerHoursBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding = RecyclerHoursBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyData = hourlyList[position]

        // Format the hour from dt_txt (which is in "yyyy-MM-dd HH:mm:ss" format)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(hourlyData.dt_txt)
        val hour = outputFormat.format(date)

        holder.binding.tvHour.text = hour
        holder.binding.tvHourTemp.text = "${hourlyData.main.temp}°"

        // Set weather icon using a helper method
        val weatherIconResId = getCustomIconForWeather(hourlyData.weather[0].icon)
        holder.binding.imgdesc.setImageResource(weatherIconResId)
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    // This is your helper method for mapping weather icon codes to drawable resources
    private fun getCustomIconForWeather(iconCode: String): Int {
        return when (iconCode) {
            "01d", "01n" -> R.drawable.ic_clear_sky
            "02d", "02n" -> R.drawable.ic_few_cloud
            "03d", "03n" -> R.drawable.ic_scattered_clouds
            "04d", "04n" -> R.drawable.ic_broken_clouds
            "09d", "09n" -> R.drawable.ic_shower_rain
            "10d", "10n" -> R.drawable.ic_rain
            "11d", "11n" -> R.drawable.ic_thunderstorm
            "13d", "13n" -> R.drawable.ic_snow
            "50d", "50n" -> R.drawable.ic_mist
            else -> R.drawable.ic_clear_sky
        }
    }
}
