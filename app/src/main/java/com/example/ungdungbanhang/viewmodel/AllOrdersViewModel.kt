package com.example.ungdungbanhang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.data.Address
import com.example.ungdungbanhang.data.order.Order
import com.example.ungdungbanhang.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth : FirebaseAuth

): ViewModel(){

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()


    init {
        getAllOrders()
    }
    // lay ra cac don hang duoc order
    fun getAllOrders(){
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        // thuc hien truy cap vao collection lay danh sach don hang
        firestore.collection("user").document(auth.uid!!).collection("orders").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    val userId = auth.uid
                    // kiểm tra id user và số lượng đơn hàng
                    Log.d("Firestore", "Fetched ${orders.size}Firestore $userId")
                    _allOrders.emit(Resource.Success(orders))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error(it.message.toString()))
                }
            }
    }


    /*suspend fun getAllOrders() {
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        try {
            // Kiểm tra xem người dùng đã đăng nhập hay chưa
            val userId = auth.uid
            if (userId == null) {
                Log.d("Firestore", "User not logged in")
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error("User not logged in"))
                }
                return
            }

            // Truy vấn Firestore để lấy danh sách đơn hàng của người dùng
            val querySnapshot = firestore.collection("user").document(userId).collection("orders").get().await()

            // Chuyển đổi kết quả truy vấn thành danh sách đối tượng Order
            val orders = querySnapshot.toObjects(Order::class.java)

            // Log để kiểm tra giá trị
            Log.d("Firestore", "Fetched ${orders.size}Firestore $userId")

            // Gửi trạng thái thành công với danh sách đơn hàng
            viewModelScope.launch {
                _allOrders.emit(Resource.Success(orders))
            }
        } catch (e: Exception) {
            // Log lỗi và gửi trạng thái lỗi
            Log.e("Firestore", "Error: ${e.message}")
            viewModelScope.launch {
                _allOrders.emit(Resource.Error(e.message.toString()))
            }
        }
    }

     */
}
