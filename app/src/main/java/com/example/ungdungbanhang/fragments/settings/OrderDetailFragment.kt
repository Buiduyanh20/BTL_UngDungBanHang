package com.example.ungdungbanhang.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungbanhang.adapters.BillingProductsAdapter
import com.example.ungdungbanhang.data.order.OrderStatus
import com.example.ungdungbanhang.data.order.getOrderStatus
import com.example.ungdungbanhang.databinding.FragmentOrderDetailBinding
import com.example.ungdungbanhang.helper.formatPriceVN
import com.example.ungdungbanhang.util.VerticalItemDecoration
import com.example.ungdungbanhang.util.hideBottomNavigation


class OrderDetailFragment: Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        hideBottomNavigation()

        setupOrderRv()
        binding.imageCloseOrder.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.apply {

            tvOrderId.text = "Đơn hàng #${order.orderId}"
            val orderStatus = getOrderStatus(order.orderStatus)
            val isCancelled = orderStatus is OrderStatus.Canceled

            if (isCancelled) {
                // neu don hang dang o trang thai "Cancelled" thi hien thi thong bao an stepView
                stepView.visibility = View.GONE
                tvCancellationMessage.visibility = View.VISIBLE
            } else {
                // neu don hang khong o trang thai "Cancelled" thi hien stepView
                stepView.visibility = View.VISIBLE
                stepView.setSteps(
                    mutableListOf(
                        OrderStatus.Ordered.status,
                        OrderStatus.Confirmed.status,
                        OrderStatus.Shipped.status,
                        OrderStatus.Delivered.status,
                    )
                )

                val currentOrderState = when (orderStatus) {
                    is OrderStatus.Ordered -> 0
                    is OrderStatus.Confirmed -> 1
                    is OrderStatus.Shipped -> 2
                    is OrderStatus.Delivered -> 3
                    is OrderStatus.Canceled -> 4
                    else -> 4
                }

                stepView.go(currentOrderState, false)

                // quy trinh bang stepView thu 3 khi don hang da duoc giao
                if (currentOrderState == 3) {
                    stepView.done(true)
                }
            }
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phoneNumber

            tvTotalPrice.text = formatPriceVN(order.totalPrice.toDouble())
            }
        billingProductsAdapter.differ.submitList(order.products)
        }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
