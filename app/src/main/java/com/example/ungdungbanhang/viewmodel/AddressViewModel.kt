package com.example.ungdungbanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.data.Address
import com.example.ungdungbanhang.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
    ): ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

        // ham them dia chi
        fun addAddress(address: Address){
            val validateInputs = validateInputs(address)
            if(validateInputs){
                viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
                firestore.collection("user").document(auth.uid!!).collection("address").document()
                    .set(address).addOnSuccessListener {
                        viewModelScope.launch{
                            _addNewAddress.emit(Resource.Success(address)) }
                    }.addOnFailureListener {
                        viewModelScope.launch{ _addNewAddress.emit(Resource.Error(it.message.toString())) }
                    }
            }
            else{
                viewModelScope.launch{ _error.emit("Vui lòng nhập đầy đủ thông tin") }
            }
        }

    // kiem tra gia tri nguoi dung nhap vao
    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phoneNumber.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }
}