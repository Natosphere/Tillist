package com.example.tillist.ui.theme

import androidx.lifecycle.ViewModel
import com.example.tillist.ReceiptUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReceiptViewModel : ViewModel() {



    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    fun setTotalPrice(price: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                totalPrice = price
            )
        }
    }

    fun setPurchaseDate(date: String) {
        _uiState.update { currentState ->
            currentState.copy(
                dateOfPurchase = date
            )
        }
    }

    fun setStoreName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                storeName = name
            )
        }
    }

    fun resetScan() {
        _uiState.value = ReceiptUiState()
    }

}