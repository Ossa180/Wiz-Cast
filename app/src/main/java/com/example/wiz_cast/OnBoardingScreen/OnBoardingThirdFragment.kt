package com.example.wiz_cast.OnBoardingScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wiz_cast.Screens.Main.ActivityHome
import com.example.wiz_cast.databinding.FragmentOnBoardingThirdBinding

class OnBoardingThirdFragment : Fragment() {
/*
    lateinit var binding: FragmentOnBoardingThirdBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingThirdBinding.inflate(layoutInflater, container, false)
        binding.tvFinish.setOnClickListener{
            findNavController().navigate(R.id.action_onBoardingFragment_to_home_nav)
        }
        return binding.root
    }

 */

    private lateinit var binding: FragmentOnBoardingThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingThirdBinding.inflate(inflater, container, false)

        binding.tvFinish.setOnClickListener {
            // Navigate to ActivityHome using an Intent
            val intent = Intent(requireContext(), ActivityHome::class.java)
            startActivity(intent)
            // Optionally, if you want to finish the current activity and remove it from the back stack
            activity?.finish()
        }

        return binding.root
    }
}