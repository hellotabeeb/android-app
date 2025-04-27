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
    val description: String?,
    val tel: String?,
    val website: String?,
    val hours: FoursquareHours?,
    val geocodes: FoursquareGeocodes?
)

data class FoursquareHours(
    val display: String?,
    val is_open: Boolean?,
    val open_now: Boolean?
)

data class FoursquareGeocodes(
    val main: FoursquareCoordinates?
)

data class FoursquareCoordinates(
    val latitude: Double,
    val longitude: Double
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