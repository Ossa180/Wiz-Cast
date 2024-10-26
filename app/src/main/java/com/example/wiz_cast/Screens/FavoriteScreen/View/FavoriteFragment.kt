package com.example.wiz_cast.Screens.FavoriteScreen.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wiz_cast.Model.DataBase.WeatherDatabase
import com.example.wiz_cast.Model.DataBase.WeatherLocalDataSourceImpl
import com.example.wiz_cast.Model.Repository.WeatherRepository
import com.example.wiz_cast.Network.RetrofitHelper
import com.example.wiz_cast.Network.WeatherRemoteDataSourceImpl

import com.example.wiz_cast.R
import com.example.wiz_cast.Screens.FavoriteScreen.ViewModel.FavoriteViewModel
import com.example.wiz_cast.Screens.FavoriteScreen.ViewModel.FavoriteViewModelFactory
import com.example.wiz_cast.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database and DAO
        val database = WeatherDatabase.getInstance(requireContext())
        val weatherDao = database.weatherDao()

        // Set up data sources and repository
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.apiService)
        val localDataSource = WeatherLocalDataSourceImpl(weatherDao) // initialize with your DAO
        val repository = WeatherRepository(remoteDataSource, localDataSource)

        // Set up ViewModel
        val factory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        setupRecyclerView()
        observeFavoriteLocations()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

