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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.adapters.ColorsAdapter
import com.example.ungdungbanhang.adapters.SizesAdapter
import com.example.ungdungbanhang.adapters.ViewPager2Images
import com.example.ungdungbanhang.data.CartProduct
import com.example.ungdungbanhang.databinding.FragmentProductDetailsBinding
import com.example.ungdungbanhang.helper.formatPriceVN
import com.example.ungdungbanhang.helper.getProductPrice
import com.example.ungdungbanhang.util.Resource
import com.example.ungdungbanhang.util.hideBottomNavigation
import com.example.ungdungbanhang.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizeApdater by lazy { SizesAdapter() }
    private val colorAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // an bottom navigation
        hideBottomNavigation()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setUpSizesRv()
        seUpColorsRv()
        sepUpViewPager()

        // su kien close
        binding.imageClose.setOnClickListener {
            requireActivity().onBackPressed()
            //findNavController().navigateUp()
        }

        // ghi nhan su lua chon mau
        colorAdapter.onItemClick = {
            selectedColor = it
        }

        // ghi nhan su lua chon size
        sizeApdater.onItemClick = {
            selectedSize = it
        }

        binding.buttonAddToCart.setOnClickListener {
          // kiem tra nguoi duoc co chon mau sac hay kich thuoc khong
            if(selectedColor != null && selectedSize != null){
                // khi chon roi thuc hien them san pham vao gio hang
                viewModel.addUpdateProductInCar(CartProduct(product,1,selectedColor,selectedSize))
            }else{
                // khi chua chon k thuc hien them san pham vao gio hang
                Toast.makeText(context,"Vui lòng chọn màu sắc và kích thước",Toast.LENGTH_SHORT).show()
            }
        }

        // luong them san pham vao gio hang
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                        Toast.makeText(context,"Thêm sản phẩm vào giỏ hàng thành công.",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        binding.buttonAddToCart.startAnimation()
                        Toast.makeText(context,"Thêm sản phẩm vào giỏ hàng thất bại.",Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // do du lieu thong tin san pham
        binding.apply {
            val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
            tvProductName.text = product.name
            // kiem tra xem offPercentage co ton tai khong neu co thi gan gia sau khi thay doi
            if(product.offerPercentage == null)
                tvProductPrice.text = formatPriceVN(product.price.toDouble())
            tvProductPrice.text = formatPriceVN(priceAfterOffer.toDouble())
            tvProductDescription.text = product.description

            // neu san pham khong co mau hoac size an di
            if(product.colors.isNullOrEmpty()){
                tvProductColors.visibility = View.INVISIBLE
            }
            if(product.sizes.isNullOrEmpty()){
                tvProductSize.visibility = View.INVISIBLE
            }
        }
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {colorAdapter.differ.submitList(it)}
        product.sizes?.let {sizeApdater.differ.submitList(it)}
    }

    private fun sepUpViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun seUpColorsRv() {
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setUpSizesRv() {
        binding.rvSizes.apply {
            adapter = sizeApdater
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}