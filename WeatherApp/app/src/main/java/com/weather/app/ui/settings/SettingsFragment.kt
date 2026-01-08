package com.weather.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.weather.app.BuildConfig
import com.weather.app.R
import com.weather.app.databinding.FragmentSettingsBinding
import com.weather.app.util.TemperatureUnit

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupVersionInfo()
        setupTemperatureToggle()
        setupThemeToggle()
        setupLogout()
        observeViewModel()
    }
    
    private fun setupVersionInfo() {
        binding.textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }
    
    private fun setupTemperatureToggle() {
        binding.switchTempUnit.setOnCheckedChangeListener { _, isChecked ->
            val unit = if (isChecked) TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
            viewModel.setTemperatureUnit(unit)
        }
    }

    private fun setupThemeToggle() {
        when (com.weather.app.util.PreferencesManager.themeMode) {
            1 -> binding.rbLight.isChecked = true
            2 -> binding.rbDark.isChecked = true
            else -> binding.rbSystem.isChecked = true
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rbLight -> 1
                R.id.rbDark -> 2
                else -> 0
            }
            viewModel.setThemeMode(mode)
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
    
    private fun observeViewModel() {
        viewModel.temperatureUnit.observe(viewLifecycleOwner) { unit ->
            binding.switchTempUnit.isChecked = unit == TemperatureUnit.FAHRENHEIT
            binding.textCurrentUnit.text = when (unit) {
                TemperatureUnit.CELSIUS -> getString(R.string.celsius)
                TemperatureUnit.FAHRENHEIT -> getString(R.string.fahrenheit)
            }
        }

        viewModel.logoutEvent.observe(viewLifecycleOwner) { loggedOut ->
            if (loggedOut) {
                val intent = android.content.Intent(requireContext(), com.weather.app.ui.auth.AuthActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
