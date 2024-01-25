package com.example.ungdungbanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.data.Category
import com.example.ungdungbanhang.data.Product
import com.example.ungdungbanhang.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoyViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category

): ViewModel() {

    private val _offerProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProduct = _offerProduct.asStateFlow()

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct = _bestProduct.asStateFlow()

    // phan trang san pham
    private val pagingInfo = PagingInfo()
    init {
        fetchOfferProduct()
        fetchBestProduct()
    }

    // phieu san pham co Offer tot
    fun fetchOfferProduct()
    {
        viewModelScope.launch {
            _bestProduct.emit(Resource.Loading())
        }
        // truy van san pham co offerPercentage khac null
        firestore.collection("Product").whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                val offerProductList = it.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = offerProductList == pagingInfo.oldBestProducts
                pagingInfo.oldBestProducts = offerProductList
                viewModelScope.launch {
                    _offerProduct.emit(Resource.Success(products))
                }
                pagingInfo.page++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offerProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    // fill san pham tot theo danh muc
    fun fetchBestProduct(){
        if(!pagingInfo.isPagingEnd){
            viewModelScope.launch {
                _bestProduct.emit(Resource.Loading())
            }
            // truy van san pham tot khong
            firestore.collection("Product").whereEqualTo("category", category.category)
                .whereEqualTo("offerPercentage",null)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    val bestProudList = it.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestProudList == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProudList
                    viewModelScope.launch{
                        _bestProduct.emit(Resource.Success(products))
                    }
                    pagingInfo.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _offerProduct.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
