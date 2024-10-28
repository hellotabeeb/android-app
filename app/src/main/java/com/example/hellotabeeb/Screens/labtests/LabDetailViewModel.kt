package com.example.hellotabeeb.Screens.labtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TestDetail(
    val name: String = "",
    val fee: String = ""
)

class LabDetailViewModel : ViewModel() {
    private val _testDetails = MutableStateFlow<List<TestDetail>>(emptyList())
    val testDetails: StateFlow<List<TestDetail>> = _testDetails

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchTestDetails()
    }

    private fun fetchTestDetails() {
        _isLoading.value = true
        _error.value = null

        val db = FirebaseFirestore.getInstance()
        db.collection("labs")
            .document("chughtaiLab")
            .collection("tests")
            .get()
            .addOnSuccessListener { result ->
                val tests = result.documents.map { document ->
                    TestDetail(
                        name = document.getString("Name") ?: "",
                        fee = document.getString("Fees") ?: ""
                    )
                }
                _testDetails.value = tests
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
    }
}