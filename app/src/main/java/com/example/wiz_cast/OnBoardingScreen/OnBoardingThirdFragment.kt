package com.example.wiz_cast.OnBoardingScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.FragmentOnBoardingThirdBinding

class OnBoardingThirdFragment : Fragment() {

    lateinit var binding: FragmentOnBoardingThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingThirdBinding.inflate(layoutInflater, container, false)
        binding.tvFinish.setOnClickListener{
            findNavController().navigate(R.id.action_onBoardingFragment_to_homeFragment)
        }
        return binding.root
    }

}