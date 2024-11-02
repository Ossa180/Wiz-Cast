package com.example.wiz_cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wiz_cast.Utils.PreferencesHelper
import com.example.wiz_cast.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        preferencesHelper = PreferencesHelper(requireContext())

        // Set initial selection based on saved preferences
        when (preferencesHelper.getUnits()) {
            "metric" -> binding.radioCelsius.isChecked = true
            "imperial" -> binding.radioFahrenheit.isChecked = true
            else -> binding.radioKelvin.isChecked = true
        }

        when (preferencesHelper.getLanguage()) {
            "en" -> binding.radioEnglish.isChecked = true
            "ar" -> binding.radioArabic.isChecked = true
        }

        // Set listeners for changes
        binding.radioGroupUnits.setOnCheckedChangeListener { _, checkedId ->
            val unit = when (checkedId) {
                R.id.radioCelsius -> "metric"
                R.id.radioFahrenheit -> "imperial"
                else -> ""
            }
            preferencesHelper.setUnits(unit)
        }

        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.radioEnglish -> "en"
                R.id.radioArabic -> "ar"
                else -> "en"
            }
            preferencesHelper.setLanguage(language)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}