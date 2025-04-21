package com.example.hellotabeeb.Screens.appointments


data class FoursquareResponse(
    val results: List<FoursquarePlace> = emptyList()
)

data class FoursquarePlace(
    val fsq_id: String,
    val name: String,
    val location: FoursquareLocation?,
    val categories: List<FoursquareCategory>?,
    val distance: Int?,
    val description: String?
)

data class FoursquareLocation(
    val address: String?,
    val country: String?,
    val locality: String?,
    val region: String?,
    val formatted_address: String?
)

data class FoursquareCategory(
    val id: Int,
    val name: String,
    val icon: FoursquareIcon?
)

data class FoursquareIcon(
    val prefix: String,
    val suffix: String
) {
}