package com.example.ungdungbanhang.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.adapters.HomeViewpagerAdapter
import com.example.ungdungbanhang.databinding.FragmentHomeBinding
import com.example.ungdungbanhang.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator
import com.google.common.collect.Table

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // anh xa
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ShirtFragment(),
            ShoeFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            TrousersFragment(),
        )

        binding.viewpagerHome.isUserInputEnabled = false
        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        // phan trang
        TabLayoutMediator(binding.tabLayout,binding.viewpagerHome) {tab, position ->
            when(position){
                // dinh dang trang
                0 -> tab.text = "Giới thiệu"
                1 -> tab.text = "Áo"
                2 -> tab.text = "Giày"
                3 -> tab.text = "Ghế"
                4 -> tab.text = "Tủ"
                5 -> tab.text = "Bàn"
                6 -> tab.text = "Quần"
            }
        }.attach()
    }
}