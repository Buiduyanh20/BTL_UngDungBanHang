package com.example.ungdungbanhang.viewmodel

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.data.Address
import com.example.ungdungbanhang.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()


    init {
        getUserAddress()
    }
    fun getUserAddress(){
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener{value, error ->
                if(error != null){
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                }else{
                    viewModelScope.launch {
                        val address = value?.toObjects(Address::class.java)
                        _address.emit(Resource.Success(address!!))
                    }
                }
            }
    }
}