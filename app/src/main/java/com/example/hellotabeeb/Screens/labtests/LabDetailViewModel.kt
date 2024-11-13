package com.example.hellotabeeb.Screens.labtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TestDetail(
    val name: String = "",
    val fee: String = "",
    val discountPercentage: Int = 20 // Default 20% discount
)

class LabDetailViewModel : ViewModel() {
    private val _testDetails = MutableStateFlow<List<TestDetail>>(emptyList())
    val testDetails: StateFlow<List<TestDetail>> = _testDetails

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // List of tests that should have 30% discount
    private val specialDiscountTests = listOf("Lipid Profile", "Serum 25-OH Vitamin D", "Glycosylated Hemoglobin (HbA1c)")

    init {
        fetchTestDetails()
    }

    private fun customTestSort(test: TestDetail): Pair<Int, String> {
        val name = test.name.trim()
        return when {
            name.first().isDigit() -> Pair(1, name.lowercase())
            else -> Pair(0, name.lowercase())
        }
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
                    val name = document.getString("Name") ?: ""
                    TestDetail(
                        name = name,
                        fee = document.getString("Fees") ?: "",
                        discountPercentage = if (specialDiscountTests.contains(name)) 30 else 20
                    )
                }.sortedWith(compareBy(
                    { customTestSort(it).first },
                    { customTestSort(it).second }
                ))
                _testDetails.value = tests
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
    }
}