package com.example.ungdungbanhang.firebase

import com.example.ungdungbanhang.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FireBaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

    ) {

    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("cart")

    // ham them san pham vao gio hang
    fun addProductInCart(cartProduct: CartProduct, onResult: (CartProduct?,Exception?) -> Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }.addOnFailureListener {
                onResult(null,it)
            }
    }
    // ham tang so luong
    fun increaseQuantity(documenId: String, onResult: (String?,Exception?) -> Unit){
        firestore.runTransaction { transition ->
            val documentRef = transition.get(cartCollection.document(documenId))
            val productObject = documentRef.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newCartProduct = cartProduct.copy(quantity = newQuantity)
                transition.set(cartCollection.document(documenId),newCartProduct)
            }
        }.addOnSuccessListener {
            onResult(documenId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    // ham  giam so luong san pham trong gio hang
    fun decreaseQuantity(documenId: String, onResult: (String?,Exception?) -> Unit){
        firestore.runTransaction { transition ->
            val documentRef = transition.get(cartCollection.document(documenId))
            val productObject = documentRef.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity -1
                val newCartProduct = cartProduct.copy(quantity = newQuantity)
                transition.set(cartCollection.document(documenId),newCartProduct)
            }
        }.addOnSuccessListener {
            onResult(documenId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }
    enum class QuantityChanging {
        INCREASE,DECREASE
    }
}