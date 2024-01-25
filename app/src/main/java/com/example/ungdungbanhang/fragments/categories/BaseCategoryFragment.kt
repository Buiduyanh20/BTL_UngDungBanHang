package com.example.ungdungbanhang.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.adapters.BestProudctApdapter
import com.example.ungdungbanhang.databinding.FragmentBaseCategoryBinding
import com.example.ungdungbanhang.util.showBottomNavigation
import com.example.ungdungbanhang.viewmodel.CategoyViewModel
import com.example.ungdungbanhang.viewmodel.MainCategoryViewModel

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerApdater: BestProudctApdapter by lazy { BestProudctApdapter() }
    protected val bestProudctApdapter: BestProudctApdapter by lazy { BestProudctApdapter() }
    private val viewModel by viewModels<CategoyViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTopOfferRv()
        seUpBestProductRv()

        // dieu huong offrAdapter qua chi tiet san pham khi click
        offerApdater.onclick = {
            val b  =Bundle().apply {
                putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        // dieu huong bestProudctApdapter qua chi tiet san pham khi click
        bestProudctApdapter.onclick = {
            val b = Bundle().apply { putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.rvOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(dy != 0 && recyclerView.canScrollHorizontally(1)){
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                onBestProductsPagingRequest()
            }
        })
    }

    // phant trang offer
    open fun onOfferPagingRequest(){
        //viewModel.fetchOfferProduct()
    }

    // phant trang bestProduct
    open fun onBestProductsPagingRequest(){
        //viewModel.fetchBestProduct()
    }

    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    fun hideBestProductLoangding()
    {
        binding.bestProductsProgressBar.visibility = View.GONE
    }

    fun showBestProductLoading(){
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }
    private fun seUpBestProductRv() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProudctApdapter
        }
    }

    private fun setUpTopOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = offerApdater
        }
    }

    // hien bottom navigation
    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}