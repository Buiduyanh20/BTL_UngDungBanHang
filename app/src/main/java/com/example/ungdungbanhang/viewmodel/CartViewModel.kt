package com.example.ungdungbanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.data.CartProduct
import com.example.ungdungbanhang.firebase.FireBaseCommon
import com.example.ungdungbanhang.helper.getProductPrice
import com.example.ungdungbanhang.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FireBaseCommon

):ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    val productsPrice = cartProducts.map {
        when(it){
         is Resource.Success -> {
             calculatePrice(it.data!!)
         }
            else -> null
        }
    }

    // ham xoa san pham trong gio hang
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if(index != null && index != -1){
            val documnetId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart").document(documnetId)
                .delete()
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Any {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }
    }

    init {
        getCartProduct()
    }

    // lay san pham trong gio hang
    private fun getCartProduct() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        // truy cap firestore de lay san pham trong gio hang cua nguoi dung hien tai, su dung addSnap de cap nhat su thay doi
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener{ value, error ->
                if(error != null || value == null){
                    // neu co loi khong tin thay du lieu thong bao
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                }else{
                    // phan tich du lieu phat ra trang thai thanh cong
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    // ham thay doi so luong
    fun changeQuantity(
        cartProduct: CartProduct,
        quantiles: FireBaseCommon.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        // kiem gia tri index co bang null hay bang -1 khong
        if(index != null  && index != -1){
            val documnetId = cartProductDocuments[index].id
            when(quantiles){
                FireBaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch{
                        _cartProducts.emit(Resource.Loading())
                        increaseQuantity(documnetId)
                    }
                }
                FireBaseCommon.QuantityChanging.DECREASE -> {
                    // khong cho nguoi dung giam so luong nho hon 1
                    if(cartProduct.quantity == 1){
                        // neu nguoi dung tiep tuc click vao giam khi so luong = 1 thi xoa san pham
                        viewModelScope.launch { _deleteDialog.emit(cartProduct)}
                        return
                    }
                    viewModelScope.launch{
                        _cartProducts.emit(Resource.Loading())
                        decreaseQuantity(documnetId)
                    }
                }
            }
        }
    }
    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }
    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }
}