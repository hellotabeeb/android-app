package com.example.hellotabeeb.Screens.labtests

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    private val specialDiscountTests = listOf("Lipid Profile", "Serum 25-OH Vitamin D", "Glycosylated Hemoglobin (HbA1c)")

    fun fetchTestDetails(labName: String) {
        _isLoading.value = true
        _error.value = null

        val db = FirebaseFirestore.getInstance()
        val firestoreLabName = getFirestoreLabName(labName)
        db.collection("labs")
            .document(firestoreLabName) // Use mapped lab name to fetch the correct lab's tests
            .collection("tests")
            .get()
            .addOnSuccessListener { result ->
                val discountPercentage = when (firestoreLabName) {
                    "chughtaiLab" -> 20
                    "excel" -> 25
                    "essa" -> 20
                    "IDC" -> 15
                    else -> 20
                }

                val tests = result.documents.map { document ->
                    val name = document.getString("Name") ?: ""
                    TestDetail(
                        name = name,
                        fee = document.getString("Fees") ?: "",
                        discountPercentage = if (firestoreLabName == "chughtaiLab" && specialDiscountTests.contains(name)) 30 else discountPercentage
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

    private fun customTestSort(test: TestDetail): Pair<Int, String> {
        val name = test.name.trim()
        return when {
            name.first().isDigit() -> Pair(1, name.lowercase())
            else -> Pair(0, name.lowercase())
        }
    }
}

private fun getFirestoreLabName(labName: String): String {
    return when (labName) {
        "Chughtai Lab" -> "chughtaiLab"
        "Excel Labs" -> "excel"
        "Dr. Essa Laboratory & Diagnostic Centre" -> "essa"
        "Islamabad Diagnostic Center" -> "IDC"
        else -> labName
    }
}