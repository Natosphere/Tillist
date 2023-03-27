package com.example.tillist

import android.net.Uri

data class ReceiptUiState (

    val totalPrice: Double = 0.00,
    val dateOfPurchase: String = "",
    val storeName: String = "",
//    val imageUri: Uri = null
)