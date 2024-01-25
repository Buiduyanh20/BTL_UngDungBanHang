package com.example.ungdungbanhang.fragments.shopping

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ungdungbanhang.BuildConfig
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.activities.LoginRegisterActivity
import com.example.ungdungbanhang.databinding.FragmentProfileBinding
import com.example.ungdungbanhang.util.Resource
import com.example.ungdungbanhang.util.showBottomNavigation
import com.example.ungdungbanhang.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // click chinh sua profile
        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        // click su kien vao don hang
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_OrderFragment)
        }

        // click su kien theo doi don hang

        // click su kien thanh toan
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray(),
            )
            findNavController().navigate(action)
        }


        /*binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }*/

        // click su kien logout
        binding.linearLogOut.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Đăng xuất")
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?")

            // Positive button (OK)
            builder.setPositiveButton("Đăng xuất") { dialog, which ->

                viewModel.logout()

                // Start the LoginRegisterActivity
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)


                requireActivity().finish()
            }


            builder.setNegativeButton("Hủy") { dialog, which ->
            }

            // Show the AlertDialog
            val dialog = builder.create()
            dialog.show()
        }
        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(
                            ColorDrawable(
                            Color.BLACK)
                        ).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.lastName} ${it.data.fistName} "
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}