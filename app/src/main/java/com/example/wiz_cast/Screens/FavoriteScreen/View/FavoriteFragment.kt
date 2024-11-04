package com.example.wiz_cast.Screens.FavoriteScreen.View

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Model.DataBase.WeatherDatabase
import com.example.wiz_cast.Model.DataBase.WeatherLocalDataSourceImpl
import com.example.wiz_cast.Model.Repository.WeatherRepository
import com.example.wiz_cast.Network.RetrofitHelper
import com.example.wiz_cast.Network.WeatherRemoteDataSourceImpl

import com.example.wiz_cast.R
import com.example.wiz_cast.Screens.FavoriteScreen.ViewModel.FavoriteViewModel
import com.example.wiz_cast.Screens.FavoriteScreen.ViewModel.FavoriteViewModelFactory
import com.example.wiz_cast.Utils.ConnectivityReceiver
import com.example.wiz_cast.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var connectivityReceiver: ConnectivityReceiver
    private var isNetworkAvailable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        setupViewModel()
        setupRecyclerView()
        observeFavoriteLocations()

        // Initialize and register the connectivity receiver
        connectivityReceiver = ConnectivityReceiver {
            isNetworkAvailable = true // Update network availability
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(connectivityReceiver, filter)
        binding.btnGoToMap.setOnClickListener {

            findNavController().navigate(R.id.action_favoriteFragment_to_mapFragment)
        }
    }

    private fun setupViewModel() {
        // Initialize database, DAO, and ViewModel
        val database = WeatherDatabase.getInstance(requireContext())
        val weatherDao = database.weatherDao()
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.apiService)
        val localDataSource = WeatherLocalDataSourceImpl(weatherDao)
        val repository = WeatherRepository(remoteDataSource, localDataSource)
        val factory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter { favoriteLocation ->
            onFavoriteItemClick(favoriteLocation)
        }
        binding.rvFav.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Set up ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the position of the item to delete
                val position = viewHolder.adapterPosition
                val locationToDelete = favoriteAdapter.currentList[position]

                // Remove the item from ViewModel
                viewModel.removeFavoriteLocation(locationToDelete.latitude, locationToDelete.longitude)
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
        })

        // Attach ItemTouchHelper to the RecyclerView to delete items
        itemTouchHelper.attachToRecyclerView(binding.rvFav)
    }
    private fun checkNetworkAvailability(): Boolean {
        // Manually check network availability to ensure it’s up-to-date
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun observeFavoriteLocations() {
        lifecycleScope.launch {
            viewModel.favoriteLocationsState.collectLatest { favoriteList ->
                favoriteAdapter.submitList(favoriteList)
            }
        }
    }

    private fun onFavoriteItemClick(location: FavoriteLocation) {
        // Check network availability
        isNetworkAvailable = checkNetworkAvailability()

        if (isNetworkAvailable) {
            // Navigate to DetailsFragment with lat/lon data
            val bundle = Bundle().apply {
                putDouble("LATITUDE", location.latitude)
                putDouble("LONGITUDE", location.longitude)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_detailsFragment, bundle)
        } else {
            // Show Snackbar with action to open Wi-Fi settings
            Snackbar.make(binding.root, "No network connection", Snackbar.LENGTH_LONG)
                .setAction("Enable Wi-Fi") {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the connectivity receiver
        requireContext().unregisterReceiver(connectivityReceiver)
        _binding = null
    }
}

