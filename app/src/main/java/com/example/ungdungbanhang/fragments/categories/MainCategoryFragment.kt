package com.example.ungdungbanhang.fragments.categories
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.activities.ShoppingActivity
import com.example.ungdungbanhang.adapters.BestDealsApdater
import com.example.ungdungbanhang.adapters.BestProudctApdapter
import com.example.ungdungbanhang.adapters.SpecialProductsApdater
import com.example.ungdungbanhang.databinding.FragmentMainCategoryBinding
import com.example.ungdungbanhang.util.Resource
import com.example.ungdungbanhang.util.showBottomNavigation
import com.example.ungdungbanhang.viewmodel.MainCategoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment:Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsApdater: SpecialProductsApdater
    private lateinit var bestDealsApdater: BestDealsApdater
    private lateinit var bestProudctApdapter: BestProudctApdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductsRv()
        setUpBestDealsRv()
        setUpBestProductsRv()

        // thiet lap dieu huong cho special
        specialProductsApdater.onclick = {
            val b = Bundle().apply { putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        // thiet lap dieu huong cho bestDeals
        bestDealsApdater.onclick = {
            val b = Bundle().apply { putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        // thiet lap dieu huong cho bestProduct
        bestProudctApdapter.onclick = {
            val b = Bundle().apply { putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        // phan trang
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsApdater.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                        binding.bestProductProgressbar.visibility = View.VISIBLE
                    }
                    is  Resource.Success -> {
                        bestDealsApdater.differ.submitList(it.data)
                        hideLoading()
                        binding.bestProductProgressbar.visibility = View.GONE
                    }
                    is  Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.bestProductProgressbar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.bestProductProgressbar.visibility = View.VISIBLE
                    }
                    is  Resource.Success -> {
                        bestProudctApdapter.differ.submitList(it.data)
                        binding.bestProductProgressbar.visibility = View.GONE
                    }
                    is  Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.bestProductProgressbar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
        // phan trang trang best product
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            // kiem tra cuon cau Nestl co bang chieu cao cong voi so luong cuong y hay khong
            if(v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProduct()
            }
        })
    }

    private fun setUpBestProductsRv() {
        bestProudctApdapter = BestProudctApdapter()
        binding.rvBestsProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProudctApdapter
        }
    }

    private fun setUpBestDealsRv() {
        bestDealsApdater = BestDealsApdater()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsApdater
        }
    }

    private fun setUpSpecialProductsRv() {
        specialProductsApdater = SpecialProductsApdater()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsApdater
        }
    }

    // hien bottom navigation
    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}