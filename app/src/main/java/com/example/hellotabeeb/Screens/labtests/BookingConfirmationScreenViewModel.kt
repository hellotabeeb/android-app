package com.example.hellotabeeb.Screens.labtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.sendgrid.SendGrid
import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import java.io.IOException

data class BookingDetails(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val testName: String = "",
    val testFee: String = ""
)

class ConfirmationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val SENDGRID_API_KEY = "PH96yixUWvNZCmY866WSTeba2DzxlZQb"

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Initial)
    val bookingState: StateFlow<BookingState> = _bookingState

    sealed class BookingState {
        object Initial : BookingState()
        object Loading : BookingState()
        data class Success(val discountCode: String) : BookingState()
        data class Error(val message: String) : BookingState()
    }

    fun confirmBooking(bookingDetails: BookingDetails) {
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading

            try {
                // 1. Get an unused code
                val discountCode = getUnusedCode()

                // 2. Move code to availedCodes collection with user details
                moveCodeToAvailed(discountCode, bookingDetails)

                // 3. Send confirmation email
                sendConfirmationEmail(bookingDetails, discountCode)

                _bookingState.value = BookingState.Success(discountCode)
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private suspend fun getUnusedCode(): String {
        return try {
            val snapshot = db.collection("codes")
                .whereEqualTo("isUsed", false)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                throw Exception("No discount codes available")
            }

            val document = snapshot.documents[0]
            document.getString("code") ?: throw Exception("Invalid discount code")
        } catch (e: Exception) {
            throw Exception("Failed to get discount code: ${e.message}")
        }
    }

    private suspend fun moveCodeToAvailed(code: String, bookingDetails: BookingDetails) {
        val batch = db.batch()

        try {
            // 1. Get reference to the code document
            val codeQuery = db.collection("codes")
                .whereEqualTo("code", code)
                .limit(1)
                .get()
                .await()

            val codeDoc = codeQuery.documents.firstOrNull()
                ?: throw Exception("Code not found")

            // 2. Create new document in availedCodes
            val availedCodeRef = db.collection("availedCodes").document(code)
            val availedCodeData = hashMapOf(
                "code" to code,
                "userName" to bookingDetails.name,
                "userEmail" to bookingDetails.email,
                "userPhone" to bookingDetails.phone,
                "testName" to bookingDetails.testName,
                "testFee" to bookingDetails.testFee,
                "availedAt" to Timestamp.now()
            )

            // 3. Add operations to batch
            batch.delete(codeDoc.reference)  // Delete from codes collection
            batch.set(availedCodeRef, availedCodeData)  // Add to availedCodes collection

            // 4. Commit the batch
            batch.commit().await()

        } catch (e: Exception) {
            throw Exception("Failed to process discount code: ${e.message}")
        }
    }

    private fun sendConfirmationEmail(bookingDetails: BookingDetails, discountCode: String) {
        try {
            val from = Email("your-verified-sender@yourdomain.com")
            val subject = "Lab Test Booking Confirmation"
            val to = Email(bookingDetails.email)

            val emailContent = """
                Dear ${bookingDetails.name},
                
                Thank you for booking your lab test with us.
                
                Booking Details:
                Test: ${bookingDetails.testName}
                Fee: ${bookingDetails.testFee}
                
                Your discount code is: $discountCode
                Please keep this code safe as it can only be used once.
                
                Please show this email at the lab during your visit.
                
                Best regards,
                Your Lab Team
            """.trimIndent()

            val content = Content("text/plain", emailContent)
            val mail = Mail(from, subject, to, content)

            val sg = SendGrid(SENDGRID_API_KEY)
            val request = Request()

            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response = sg.api(request)

            if (response.statusCode !in 200..299) {
                throw IOException("Failed to send email: ${response.body}")
            }

        } catch (e: IOException) {
            throw Exception("Failed to send email: ${e.message}")
        }
    }

    // Helper function to add new codes (if needed)
    suspend fun addNewCode(code: String) {
        try {
            db.collection("codes").add(
                hashMapOf(
                    "code" to code,
                    "isUsed" to false
                )
            ).await()
        } catch (e: Exception) {
            throw Exception("Failed to add new code: ${e.message}")
        }
    }
}