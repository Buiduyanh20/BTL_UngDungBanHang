package com.example.ungdungbanhang.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ungdungbanhang.data.Address
import com.example.ungdungbanhang.databinding.FragmentAddressBinding
import com.example.ungdungbanhang.util.Resource
import com.example.ungdungbanhang.util.hideBottomNavigation
import com.example.ungdungbanhang.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment: Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        // hien thanh loading
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation()
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // xu ly su kien click quay ve
        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        // su kien button luu dia chi
        binding.apply {
            buttonSave.setOnClickListener {
                // anh xa
                val addressTitle = edAddressTitle.text.trim().toString()
                val fullName = edFullName.text.trim().toString()
                val phoneNumber = edPhone.text.trim().toString()
                val street = edStreet.text.trim().toString()
                val city = edCity.text.trim().toString()
                val state = edState.text.trim().toString()

                val address = Address(addressTitle, fullName, phoneNumber, street, city, state)
                viewModel.addAddress(address)
            }
        }
    }
}