package com.example.wiz_cast.OnBoardingScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    lateinit var  binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Handler(Looper.getMainLooper()).postDelayed({
            // after 3 seconds navigate to the fragment
           // findNavController().navigate(R.id.navigate_splashFragment_to_homeFragment)
            findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
        }, 5000)
//        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)


        // for animation of the splash screen
        val animTop = AnimationUtils.loadAnimation(context, R.anim.from_top)
        val animBot = AnimationUtils.loadAnimation(context, R.anim.from_bot)
        binding.lottieSuncloud.animation = animTop
        binding.lottieraindrop.animation = animBot
        binding.textView.animation = animBot

        return binding.root
    }
}