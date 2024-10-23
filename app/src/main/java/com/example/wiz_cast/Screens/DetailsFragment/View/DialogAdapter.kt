package com.example.wiz_cast.Screens.DetailsFragment.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wiz_cast.Model.Pojo.Item0
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.MoreDaysCardBinding
import java.text.SimpleDateFormat
import java.util.*

class DialogAdapter(private val forecastList: List<Item0>) :
    RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val binding = MoreDaysCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        val dayForecast = forecastList[position]
        holder.bind(dayForecast)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class DialogViewHolder(private val binding: MoreDaysCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: Item0) {
            val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val date = dateFormat.format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(forecast.dt_txt))

            binding.tvCardDate.text = date
            binding.tvCardTemp.text = "${forecast.main.temp}°C"

            val weatherIconResId = getCustomIconForWeather(forecast.weather[0].icon)
            binding.imgCardIcon.setImageResource(weatherIconResId)
        }
    }

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

