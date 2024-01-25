package com.example.ungdungbanhang.data.order

sealed class OrderStatus(val status: String){
    object Ordered: OrderStatus("Ordered")
    object Canceled: OrderStatus("Canceled")
    object Confirmed: OrderStatus("Confirmed")
    object Shipped: OrderStatus("Shipped")
    object Delivered: OrderStatus("Delivered")
    object Returned: OrderStatus("Returned")
}

// lay ra trang thai don hang hien tai
fun getOrderStatus(status: String): OrderStatus{
    return when (status) {
        "Ordered" -> OrderStatus.Ordered
        "Canceled" -> OrderStatus.Canceled
        "Confirmed" -> OrderStatus.Confirmed
        "Shipped" -> OrderStatus.Shipped
        "Delivered" -> OrderStatus.Delivered
        "Returned" -> OrderStatus.Returned
        else -> OrderStatus.Ordered
    }
}
// cách xử lý đơn trang thái đơn hàng bằng enum
/*
* enum class OrderStatus(val status: String) {
    Ordered("Đã đặt hàng"),
    Canceled("Hủy"),
    Confirmed("Đã xác nhận"),
    Shipped("Đã vận chuyển"),
    Delivered("Đã giao hàng"),
    Returned("Đã trả hàng")
}

// Lấy trạng thái đơn hàng hiện tại
// Lấy trạng thái đơn hàng hiện tại
fun getOrderStatus(status: String): OrderStatus =
    OrderStatus.values().find { it.status == status } ?: run {
        // Xử lý trường hợp không khớp ở đây, có thể log hoặc thông báo lỗi
        OrderStatus.Ordered
    }
*
* */