package com.weather.app.ui.cities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weather.app.R
import com.weather.app.data.local.entity.CityEntity
import com.weather.app.data.remote.api.RetrofitClient
import com.weather.app.databinding.FragmentCitiesBinding
import com.weather.app.util.LocationHelper
import com.weather.app.util.Resource
import com.weather.app.BuildConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CitiesFragment : Fragment() {
    
    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CitiesViewModel by viewModels { CitiesViewModel.Factory() }
    
    private lateinit var searchAdapter: SearchResultsAdapter
    private lateinit var citiesAdapter: SavedCitiesAdapter
    private lateinit var locationHelper: LocationHelper
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(
                requireContext(),
                R.string.error_location_permission,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        locationHelper = LocationHelper(requireContext())
        
        setupAdapters()
        setupSearch()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupAdapters() {
        searchAdapter = SearchResultsAdapter { geocodingResponse ->
            viewModel.addCity(geocodingResponse)
            binding.editSearch.text?.clear()
        }
        binding.recyclerSearchResults.adapter = searchAdapter
        
        citiesAdapter = SavedCitiesAdapter(
            onCityClick = { city ->
                viewModel.setDefaultCity(city.id)
                Toast.makeText(requireContext(), R.string.set_as_default, Toast.LENGTH_SHORT).show()
            },
            onCityLongClick = { /* Show delete button */ },
            onDeleteClick = { city ->
                showDeleteConfirmation(city)
            }
        )
        binding.recyclerCities.adapter = citiesAdapter
    }
    
    private fun setupSearch() {
        binding.editSearch.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            
            if (query.isNotEmpty()) {
                binding.imageClearSearch.visibility = View.VISIBLE
                binding.recyclerSearchResults.visibility = View.VISIBLE
                binding.textSavedCitiesHeader.visibility = View.GONE
                binding.recyclerCities.visibility = View.GONE
                viewModel.searchCities(query)
            } else {
                binding.imageClearSearch.visibility = View.GONE
                binding.recyclerSearchResults.visibility = View.GONE
                binding.textSavedCitiesHeader.visibility = View.VISIBLE
                binding.recyclerCities.visibility = View.VISIBLE
                viewModel.clearSearchResults()
            }
        }
        
        binding.imageClearSearch.setOnClickListener {
            binding.editSearch.text?.clear()
        }
    }
    
    private fun setupButtons() {
        binding.buttonCurrentLocation.setOnClickListener {
            checkLocationPermission()
        }
    }
    
    private fun observeViewModel() {
        viewModel.cities.observe(viewLifecycleOwner) { cities ->
            citiesAdapter.submitList(cities)
            binding.emptyStateLayout.visibility = if (cities.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.citiesWeather.observe(viewLifecycleOwner) { weatherMap ->
            citiesAdapter.updateWeather(weatherMap)
        }
        
        viewModel.searchResults.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressSearch.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressSearch.visibility = View.GONE
                    searchAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressSearch.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.addCityResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), R.string.city_added, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.location_permission_rationale)
                    .setMessage(R.string.location_permission_rationale)
                    .setPositiveButton(R.string.grant_permission) { _, _ ->
                        requestLocationPermission()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
            else -> {
                requestLocationPermission()
            }
        }
    }
    
    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    private fun getCurrentLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            locationHelper.getCurrentLocation().collectLatest { location ->
                if (location != null) {
                    // Reverse geocode to get city name
                    try {
                        val response = RetrofitClient.geocodingApi.reverseGeocode(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            limit = 1,
                            apiKey = BuildConfig.WEATHER_API_KEY
                        )
                        
                        if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                            val geoResult = response.body()!!.first()
                            viewModel.addCurrentLocation(
                                name = geoResult.name,
                                country = geoResult.country,
                                lat = location.latitude,
                                lon = location.longitude
                            )
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            R.string.error_location_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_location_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun showDeleteConfirmation(city: CityEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete)
            .setMessage("Remove ${city.name} from saved cities?")
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.removeCity(city)
                Toast.makeText(requireContext(), R.string.city_removed, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
