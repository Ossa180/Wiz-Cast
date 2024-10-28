package com.example.wiz_cast.Screens.FavoriteScreen.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.CardFavoriteListBinding


class FavoriteAdapter(
    private val onItemClick: (FavoriteLocation) -> Unit
) : ListAdapter<FavoriteLocation, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = CardFavoriteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class FavoriteViewHolder(private val binding: CardFavoriteListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: FavoriteLocation) {
            binding.tvFavTemp.text = "${location.temperature}°"
            binding.tvFavCity.text = location.name

            binding.imgFavIcon.setImageResource(getIconResource(location.weatherIcon))
            binding.root.setOnClickListener {
                onItemClick(location)
            }
        }

        private fun getIconResource(icon: String): Int {
            return when (icon) {
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

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteLocation>() {
            override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean =
                oldItem == newItem
        }
    }
}