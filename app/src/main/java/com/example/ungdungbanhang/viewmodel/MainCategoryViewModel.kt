package com.example.ungdungbanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.ungdungbanhang.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.ungdungbanhang.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class MainCategoryViewModel@Inject constructor(
    private val  firestore: FirebaseFirestore
):ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProduct()
        fetchBestDealsProudcts()
        fetchBestProduct()
    }

    fun fetchSpecialProduct() {
        if(!pagingInfo.isPagingEnd)
        {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            firestore
                .collection("Product").whereEqualTo("category","Special Products").get().addOnSuccessListener { result ->
                    val specialProductsList = result.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductsList))
                    }
                    pagingInfo.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDealsProudcts(){
        if(!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _bestDealsProducts.emit(Resource.Loading())
            }

            firestore.collection("Product").whereEqualTo("category","Best Deals").get().addOnSuccessListener {result ->
                val bestDealsProduct = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProduct))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }

    fun fetchBestProduct()
    {
        if(!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }

            firestore.collection("Product").whereEqualTo("category","Best Products").limit(pagingInfo.page * 10).get().addOnSuccessListener { result ->
                val bestProduct = result.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = bestProduct == pagingInfo.oldBestProducts
                pagingInfo.oldBestProducts = bestProduct
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(bestProduct))
                }
                pagingInfo.page++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }
}

// phan trang san pham
internal data class PagingInfo(
    var page: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)