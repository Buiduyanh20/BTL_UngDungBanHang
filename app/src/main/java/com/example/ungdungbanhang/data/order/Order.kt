package com.example.ungdungbanhang.data.order

import android.os.Parcelable
import com.example.ungdungbanhang.data.Address
import com.example.ungdungbanhang.data.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Parcelize
data class Order (
        val orderStatus: String = "",
        val totalPrice: Float = 0f,
        val products: List<CartProduct> = emptyList(),
        val address: Address = Address(),
        // cho date là ngày hiện tại
        val date: String = generateCurrentDateTime(),
        //val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
        // mã đơn hàng
        //val orderId: Long = nextLong(0,100_000_000_000) + totalPrice.toLong()
        val orderId: Long = generateOrderId(),
        //val orderId: Long = (1..10).joinToString("") { Random.nextInt(0, 10).toString() }.toLong()
): Parcelable {
        companion object {
                private const val MAX_ORDER_ID_LENGTH = 12

                private fun generateOrderId(): Long {
                        val randomOrderId = (1..MAX_ORDER_ID_LENGTH).joinToString("") {
                                Random.nextInt(0, 10).toString()
                        }
                        return randomOrderId.toLong()
                }
                private fun generateCurrentDateTime(): String {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        return dateFormat.format(Date())
                }
        }
}