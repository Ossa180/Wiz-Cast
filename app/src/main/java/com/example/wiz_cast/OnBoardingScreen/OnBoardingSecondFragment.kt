package com.example.wiz_cast.OnBoardingScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.wiz_cast.R
import com.example.wiz_cast.databinding.FragmentOnBoardingSecondBinding


class OnBoardingSecondFragment : Fragment() {

    lateinit var binding: FragmentOnBoardingSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingSecondBinding.inflate(layoutInflater, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)
        binding.tvNext.setOnClickListener{
            viewPager?.currentItem = 2 // move to the next fragment
        }
        return binding.root
    }


}