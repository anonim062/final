package com.weather.app.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.weather.app.R
import com.weather.app.databinding.FragmentForecastBinding
import com.weather.app.util.Resource

class ForecastFragment : Fragment() {
    
    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ForecastViewModel by viewModels { ForecastViewModel.Factory() }
    private val adapter = DailyForecastAdapter()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerForecast.adapter = adapter
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
    }
    
    private fun observeViewModel() {
        viewModel.forecast.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textError.visibility = View.GONE
                    binding.recyclerForecast.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    showError(resource.message ?: getString(R.string.error_loading_forecast))
                }
            }
        }
        
        viewModel.dailyForecast.observe(viewLifecycleOwner) { dailyForecasts ->
            adapter.submitList(dailyForecasts)
        }
        
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE
        binding.recyclerForecast.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.VISIBLE
        binding.textError.text = message
        binding.recyclerForecast.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
