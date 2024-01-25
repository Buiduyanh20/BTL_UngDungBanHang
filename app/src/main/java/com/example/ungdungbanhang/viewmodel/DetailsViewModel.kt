package com.example.ungdungbanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.data.CartProduct
import com.example.ungdungbanhang.firebase.FireBaseCommon
import com.example.ungdungbanhang.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val fireBaseCommon: FireBaseCommon

):ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCar(cartProduct: CartProduct){
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
        firestore.collection("user")
            .document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {
                // kiem tra doucument co trong khong
                if(it.isEmpty()){ // them san pham moi
                    addNewProduct(cartProduct)
                }else{
                    val product = it.first().toObject(CartProduct::class.java)
                    if(product == cartProduct){ // tang so luong
                        val doucumentId = it.first().id
                        increaseQuantity(doucumentId,cartProduct)
                    }else{ // them san pham
                        addNewProduct(cartProduct)
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    // them san pham moi
    private fun addNewProduct(cartProduct: CartProduct){
        fireBaseCommon.addProductInCart(cartProduct){ addProduct,e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resource.Success(addProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    // ham tang so luong san pham trong gio
    private fun increaseQuantity(documenId: String, cartProduct: CartProduct){
        fireBaseCommon.increaseQuantity(documenId){ _,e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resource.Success(cartProduct))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}