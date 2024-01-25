package com.example.ungdungbanhang.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.databinding.FragmentProductDetailsBinding
import com.example.ungdungbanhang.databinding.FragmentSearchBinding
import com.example.ungdungbanhang.util.hideBottomNavigation
import com.example.ungdungbanhang.viewmodel.DetailsViewModel
import com.example.ungdungbanhang.viewmodel.factory.SearchViewModel

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding
    //private val viewModel by viewModels<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // an bottom navigation
        hideBottomNavigation()
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }
}