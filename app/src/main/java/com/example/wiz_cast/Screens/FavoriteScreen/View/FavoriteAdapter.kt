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


class FavoriteAdapter : ListAdapter<FavoriteLocation, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_favorite_list, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteLocation = getItem(position)
        holder.bind(favoriteLocation)
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFavTemp: TextView = itemView.findViewById(R.id.tvFavTemp)
        private val tvFavCity: TextView = itemView.findViewById(R.id.tvFavCity)
        private val imgFavIcon: ImageView = itemView.findViewById(R.id.imgFavIcon)

        fun bind(location: FavoriteLocation) {
            tvFavTemp.text = "${location.temperature}°"
            tvFavCity.text = location.name
            // Assuming you have a utility function to load icons
            imgFavIcon.setImageResource(getIconResource(location.weatherIcon))
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

    private class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteLocation>() {
        override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
            return oldItem.name == newItem.name // or any unique field
        }

        override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
            return oldItem == newItem
        }
    }
}