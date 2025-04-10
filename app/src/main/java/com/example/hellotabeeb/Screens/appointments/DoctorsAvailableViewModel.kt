// DoctorsAvailableViewModel.kt
package com.example.hellotabeeb.Screens.appointments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Doctor(
    val name: String = "",
    val profilePicture: String = "",
    val qualification: String = "",
    val specialization: String = ""
)

class DoctorsAvailableViewModel : ViewModel() {
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors

    init {
        fetchDoctors()
    }

    private fun fetchDoctors() {
        viewModelScope.launch {
            val firestore = FirebaseFirestore.getInstance()
            Log.d("DoctorsVM", "Starting to fetch doctors")

            firestore.collection("doctors").get()
                .addOnSuccessListener { result ->
                    Log.d("DoctorsVM", "Fetch successful, document count: ${result.size()}")
                    val doctorsList = result.map { document ->
                        Doctor(
                            name = document.getString("name") ?: "",
                            profilePicture = document.getString("profilePicture") ?: "",
                            qualification = document.getString("qualification") ?: "",
                            specialization = document.getString("specialization") ?: ""
                        ).also {
                            Log.d("DoctorsVM", "Mapped doctor: ${it.name}")
                        }
                    }
                    _doctors.value = doctorsList
                    Log.d("DoctorsVM", "Updated doctors list size: ${doctorsList.size}")
                }
                .addOnFailureListener { exception ->
                    Log.e("DoctorsVM", "Error fetching doctors", exception)
                }
        }
    }
}