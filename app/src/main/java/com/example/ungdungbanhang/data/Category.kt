package com.example.ungdungbanhang.data

sealed class Category(val category: String) {

    object Shirt: Category("Áo")
    object Trouser: Category("Quần")
    object Chair: Category("Ghế")
    object Table: Category("Bàn")
    object Shoe: Category("Giày")
    object Cupboard: Category("Tủ")
}