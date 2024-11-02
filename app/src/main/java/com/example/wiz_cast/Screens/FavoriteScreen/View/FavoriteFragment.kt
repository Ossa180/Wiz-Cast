package com.example.wiz_cast.Screens.FavoriteScreen.View

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
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
    }

    private fun observeFavoriteLocations() {
        lifecycleScope.launch {
            viewModel.favoriteLocationsState.collectLatest { favoriteList ->
                favoriteAdapter.submitList(favoriteList)
            }
        }
    }

    private fun onFavoriteItemClick(location: FavoriteLocation) {
        if (isNetworkAvailable) {
            // Navigate to DetailsFragment with lat/lon data
            val bundle = Bundle().apply {
                putDouble("LATITUDE", location.latitude)
                putDouble("LONGITUDE", location.longitude)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_detailsFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "No network connection. Please check your connection.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the connectivity receiver
        requireContext().unregisterReceiver(connectivityReceiver)
        _binding = null
    }
}

