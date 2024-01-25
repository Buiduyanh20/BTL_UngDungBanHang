package com.example.ungdungbanhang.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.adapters.AddressAdapter
import com.example.ungdungbanhang.adapters.BillingProductsAdapter
import com.example.ungdungbanhang.data.Address
import com.example.ungdungbanhang.data.CartProduct
import com.example.ungdungbanhang.data.order.Order
import com.example.ungdungbanhang.data.order.OrderStatus
import com.example.ungdungbanhang.databinding.FragmentBillingBinding
import com.example.ungdungbanhang.helper.formatPriceVN
import com.example.ungdungbanhang.util.HorizontalItemDecoration
import com.example.ungdungbanhang.util.Resource
import com.example.ungdungbanhang.viewmodel.BillingViewModel
import com.example.ungdungbanhang.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@AndroidEntryPoint
class BillingFragment: Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    // gia tri dem khi don hang dat thanh cong
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBillingProductsRv()
        setUpAddressRv()
        // xu ly su kien click nut back
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }
        // xu ly su kien click them dia chi
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // luu chon dia chi giao hang
        addressAdapter.onClick = {
            selectedAddress = it
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Đã có lỗi xảy ra${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Đơn hàng của bạn đã được đặt", Snackbar.LENGTH_LONG)
                            .show()

                        // xu ly gui gmail khi xac nhan dat hang

                    }

                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), "Đã có lỗi xảy ra ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(products)
        // hien thi tong tien san pham
        binding.tvTotalPrice.text = formatPriceVN(totalPrice.toDouble())

        // su kien thanh dat hang
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Hãy chọn địa chỉ giao hàng", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }
    // ham xu ly gui gmail xac nhan dat hang
    /*fun  sendOrderConfirmationEmail(userEmail: String, orderDetails: String){
        // Gọi hàm gửi email từ đoạn mã đã cung cấp trong câu trả lời trước
        sendEmail(userEmail, "Đơn hàng đã được đặt thành công", orderDetails)
    }*/

    /*private fun sendEmail(to: String, subject: String, body: String) {
        val properties = Properties()
        properties["mail.smtp.host"] = "your_smtp_host"
        properties["mail.smtp.port"] = "your_smtp_port"
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("levietanhzz108@gmail.com", "01082003md.")
            }
        })

        try {
            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress("your_email@gmail.com"))
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(to))
            mimeMessage.subject = subject
            mimeMessage.setText(body)

            Transport.send(mimeMessage)

            Log.d("Email", "Gửi Email thành công.")

        } catch (e: MessagingException) {
            Log.e("Email", "Gửi Email thất bại: ${e.message}")
        }
    }*/

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Xác nhận")
            setMessage("Bạn có muốn đặt hàng các mặt hàng trong giỏ hàng của bạn?")
            setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Có") { dialog, _ ->
                //val currentDate = System.currentTimeMillis()
                //val orderDate = Date(currentDate)

                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!,
                    //dateOrder = orderDate
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}