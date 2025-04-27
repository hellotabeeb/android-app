package com.example.hellotabeeb.Screens.appointments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorsAvailableViewModel : ViewModel() {


companion object {
    // Make the cache accessible for logging/debugging
    val doctorCache = mutableMapOf<String, Doctor>()

    fun getDoctorFromCache(doctorId: String): Doctor? {
        return doctorCache[doctorId]
    }

    fun addDoctorToCache(doctor: Doctor) {
        doctorCache[doctor.id] = doctor
    }
}


    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors

    private val apiService = FoursquareApiService.create()

    // Updated category mapping for more accurate results
    private val specializationCategoryMap = mapOf(
        "Skin Specialist" to "15019", // Dermatologist
        "Gynecologist" to "15008", // OB-GYN
        "Urologist" to "15022", // Urologist
        "Child Specialist" to "15014", // Pediatrician
        "Orthopedic Surgeon" to "15011", // Orthopedist
        "Consultant Physician" to "15000", // Doctor's Office
        "ENT Specialist" to "15023", // ENT
        "Neurologist" to "15010", // Neurologist
        "Eye Specialist" to "15012", // Ophthalmologist
        "Psychiatrist" to "15016", // Psychiatrist
        "Dentist" to "15007", // Dentist
        "Gastroenterologist" to "15000", // General medical for specialty
        "Heart Specialist" to "15003", // Cardiologist
        "Pulmonologist" to "15000", // General medical for specialty
        "General Physician" to "15000", // Doctor's Office
        "Diabetes Specialist" to "15006", // Endocrinologist
        "General Surgeon" to "15021", // Surgeon
        "Endocrinologist" to "15006", // Endocrinologist
        "Kidney Specialist" to "15009", // Nephrologist
        "Pain Management" to "15000"  // General medical for specialty
    )

    fun findNearbyDoctors(latitude: Double, longitude: Double, specialization: String) {
        viewModelScope.launch {
            try {
                // Get the category ID for the specialization or default to general medical (15000)
                val categoryId = specializationCategoryMap[specialization] ?: "15000"

                Log.d("DoctorsVM", "Searching for specialization: $specialization with category: $categoryId")
                Log.d("DoctorsVM", "Location coordinates: $latitude, $longitude")

                val response = apiService.searchNearbyDoctors(
                    latLng = "$latitude,$longitude",
                    radius = 10000, // 10km radius
                    categories = categoryId,
                    limit = 20,
                    apiKey = "fsq3Xa94bQVoTI9wGIWC5cpn3by+2PTm18H8AiWXlgbO9kc="
                )

                Log.d("DoctorsVM", "API Response: ${response.results.size} places found")

                // Map response to Doctor objects with robust error handling
                val doctorsList = response.results.mapNotNull { place ->
                    try {
                        // Add inside the try block before mapping the response
                        Log.d("DoctorsVM", "Raw API response for first result: ${response.results.firstOrNull()}")
                        Log.d("DoctorsVM", "FSQ ID for first result: ${response.results.firstOrNull()?.fsq_id}")
                        Doctor(
                            id = place.fsq_id ?: "generated-${System.currentTimeMillis()}-${place.name.hashCode()}",
                            name = place.name,
                            qualification = place.categories?.firstOrNull()?.name ?: specialization,
                            specialization = specialization,
                            profilePicture = place.categories?.firstOrNull()?.icon?.let {
                                "${it.prefix}64${it.suffix}"
                            } ?: "",
                            distance = place.distance ?: 0,
                            address = place.location?.formatted_address
                                ?: place.location?.address
                                ?: "Address not available",
                            // Add missing parameters with appropriate values
                            phone = place.tel,
                            website = place.website,
                            hours = place.hours?.display,
                            isOpen = place.hours?.open_now,
                            city = place.location?.locality,
                            region = place.location?.region,
                            country = place.location?.country,
                            neighborhood = null, // Add if available in your response
                            latitude = place.geocodes?.main?.latitude,
                            longitude = place.geocodes?.main?.longitude
                        )
                    } catch (e: Exception) {
                        Log.e("DoctorsVM", "Error mapping place to doctor: ${e.message}")
                        null
                    }
                }

                if (doctorsList.isNotEmpty()) {
                    _doctors.value = doctorsList
                    // Add doctors to cache
                    doctorsList.forEach { doctor ->
                        addDoctorToCache(doctor)
                    }
                    Log.d("DoctorsVM", "Found ${doctorsList.size} doctors nearby")
                } else {
                    // If no results with specific category, try with general medical category
                    Log.d("DoctorsVM", "No doctors found with specific category, trying fallback...")
                    try {
                        val response = apiService.searchNearbyDoctors(
                            latLng = "$latitude,$longitude",
                            radius = 10000, // 10km radius
                            categories = categoryId,
                            limit = 20
                            // API key is now provided in the service
                        )

                        val fallbackResponse = apiService.searchNearbyDoctors(
                            latLng = "$latitude,$longitude",
                            radius = 10000,
                            categories = "15000", // General medical fallback
                            limit = 20
                            // API key is now provided in the service
                        )

                        val fallbackDoctors = fallbackResponse.results.mapNotNull { place ->
                            try {
                                Doctor(
                                    id = place.fsq_id ?: "generated-${System.currentTimeMillis()}-${place.name.hashCode()}",
                                    name = place.name,
                                    qualification = place.categories?.firstOrNull()?.name ?: specialization,
                                    specialization = specialization,
                                    profilePicture = place.categories?.firstOrNull()?.icon?.let {
                                        "${it.prefix}64${it.suffix}"
                                    } ?: "",
                                    distance = place.distance ?: 0,
                                    address = place.location?.formatted_address
                                        ?: place.location?.address
                                        ?: "Address not available",
                                    // New field mappings
                                    phone = place.tel,
                                    website = place.website,
                                    hours = place.hours?.display,
                                    isOpen = place.hours?.open_now,
                                    city = place.location?.locality,
                                    region = place.location?.region,
                                    country = place.location?.country,
                                    neighborhood = null, // Add if available in your response
                                    latitude = place.geocodes?.main?.latitude,
                                    longitude = place.geocodes?.main?.longitude
                                )
                            } catch (e: Exception) {
                                Log.e("DoctorsVM", "Error mapping fallback place to doctor: ${e.message}")
                                null
                            }
                        }

                        _doctors.value = fallbackDoctors
                        Log.d("DoctorsVM", "Found ${fallbackDoctors.size} doctors nearby (fallback)")
                    } catch (e: Exception) {
                        Log.e("DoctorsVM", "Error in fallback search: ${e.message}")
                        _doctors.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e("DoctorsVM", "Error finding nearby doctors: ${e.message}")
                e.printStackTrace()
                _doctors.value = emptyList()
            }
        }
    }
}

data class Doctor(
    val id: String,
    val name: String,
    val qualification: String,
    val specialization: String,
    val profilePicture: String,
    val distance: Int,
    val address: String,
    // New fields
    val phone: String?,
    val website: String?,
    val hours: String?,
    val isOpen: Boolean?,
    val city: String?,
    val region: String?,
    val country: String?,
    val neighborhood: String?,
    val latitude: Double?,
    val longitude: Double?
)