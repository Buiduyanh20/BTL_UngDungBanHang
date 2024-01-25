package com.example.ungdungbanhang.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ungdungbanhang.R
import com.example.ungdungbanhang.util.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences : SharedPreferences,
    private val firebaseAuth: FirebaseAuth
)
    : ViewModel() {
        // trang thai xu ly luong
        private val _statusNavigate = MutableStateFlow(0)
        val  navigate: StateFlow<Int> = _statusNavigate

        companion object{
            const val SHOPPING_ACTIVITY = 23
            // chuyen huong giao dien ve accountOptionsFragmnet
            const val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFargment_to_accountOptionsFragment
            //const val ACCOUNT_OPTIONS_FRAGMENT = R.id.introductionFargment

        }
        init {
            // kiem tra nguoi dung da click chua
            val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
            val user = firebaseAuth.currentUser

            // kiem tra nguoi dung da dang nhap chua
            if (user != null) {
                viewModelScope.launch {
                    _statusNavigate.emit(SHOPPING_ACTIVITY)
                }
            } else if (!isButtonClicked) {
                viewModelScope.launch {
                    _statusNavigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
                }
            }else{
                Unit
            }
        }
    fun startButtonClick()
    {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }
}