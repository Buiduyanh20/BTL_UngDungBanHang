package com.example.ungdungbanhang.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ungdungbanhang.data.Category
import com.example.ungdungbanhang.viewmodel.CategoyViewModel
import com.example.ungdungbanhang.viewmodel.MainCategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelsFactoryFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category

):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoyViewModel(firestore,category) as T

    }
}