package com.example.hellotabeeb.Screens.labtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brevo.auth.ApiKeyAuth
import brevo.Configuration
import brevoApi.TransactionalEmailsApi
import brevoModel.SendSmtpEmail
import brevoModel.SendSmtpEmailReplyTo
import brevoModel.SendSmtpEmailSender
import brevoModel.SendSmtpEmailTo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class BookingDetails(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val testName: String = "",
    val testFee: String = ""
)

class ConfirmationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val BREVO_API_KEY = "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"  // Replace with your actual API key

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Initial)
    val bookingState: StateFlow<BookingState> = _bookingState

    sealed class BookingState {
        object Initial : BookingState()
        object Loading : BookingState()
        data class Success(val discountCode: String) : BookingState()
        data class Error(val message: String) : BookingState()
    }

    init {
        // Configure Brevo API key
        val defaultClient = Configuration.getDefaultApiClient()
        val apiKeyAuth = defaultClient.getAuthentication("api-key") as ApiKeyAuth
        apiKeyAuth.apiKey = BREVO_API_KEY
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
            // Modified query to handle string "false" instead of boolean false
            val snapshot = db.collection("codes")
                .whereEqualTo("isUsed", "false")  // Changed to match string "false"
                .limit(1)
                .get()
                .await()

            println("Query completed. Found ${snapshot.size()} documents")

            if (snapshot.isEmpty) {
                throw Exception("No discount codes available")
            }

            val document = snapshot.documents[0]
            val code = document.getString("code")
            println("Retrieved code: $code")

            code ?: throw Exception("Invalid discount code format")
        } catch (e: Exception) {
            println("Error in getUnusedCode: ${e.message}")
            throw Exception("Failed to get discount code: ${e.message}")
        }
    }

    private suspend fun moveCodeToAvailed(code: String, bookingDetails: BookingDetails) {
        val batch = db.batch()

        try {
            // Modified query to handle string "false"
            val codeQuery = db.collection("codes")
                .whereEqualTo("code", code)
                .whereEqualTo("isUsed", "false")
                .limit(1)
                .get()
                .await()

            if (codeQuery.isEmpty) {
                throw Exception("Code not found or already used")
            }

            val codeDoc = codeQuery.documents[0]

            // Create in availedCodes collection
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

            // Delete from codes collection and add to availedCodes
            batch.delete(codeDoc.reference)
            batch.set(availedCodeRef, availedCodeData)

            batch.commit().await()
            println("Successfully moved code to availedCodes")

        } catch (e: Exception) {
            println("Error in moveCodeToAvailed: ${e.message}")
            throw Exception("Failed to process discount code: ${e.message}")
        }
    }

    private suspend fun sendConfirmationEmail(bookingDetails: BookingDetails, discountCode: String) {
        withContext(Dispatchers.IO) {  // Add this wrapper
            try {
                val apiInstance = TransactionalEmailsApi()

                // Use a verified sender email from your Brevo account
                val sender = SendSmtpEmailSender()
                    .email("ahad.naseer@hellotabeeb.com")
                    .name("HelloTabeeb Lab Services")

                val to = listOf(
                    SendSmtpEmailTo()
                        .email(bookingDetails.email)
                        .name(bookingDetails.name)
                )

                val subject = "Lab Test Booking Confirmation - HelloTabeeb"
                val htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">Lab Test Booking Confirmation</h2>
                    <p>Dear ${bookingDetails.name},</p>
                    <p>Thank you for booking your lab test with HelloTabeeb. Your booking has been confirmed.</p>
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0;">Booking Details:</h3>
                        <ul style="list-style-type: none; padding-left: 0;">
                            <li><strong>Test:</strong> ${bookingDetails.testName}</li>
                            <li><strong>Fee:</strong> ${bookingDetails.testFee}</li>
                            <li style="margin-top: 10px;"><strong>Your Discount Code:</strong> 
                                <span style="background-color: #e9ecef; padding: 5px 10px; border-radius: 3px;">
                                    ${discountCode}
                                </span>
                            </li>
                        </ul>
                    </div>
                    <p><strong>Important:</strong> Please keep this code safe as it can only be used once.</p>
                    <p>Please show this email at the lab during your visit.</p>
                    <hr style="border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #666; font-size: 14px;">
                        Best regards,<br>
                        HelloTabeeb Lab Services Team
                    </p>
                </div>
            </body>
            </html>
        """.trimIndent()

                val sendSmtpEmail = SendSmtpEmail()
                    .sender(sender)
                    .to(to)
                    .subject(subject)
                    .htmlContent(htmlContent)
                    .replyTo(SendSmtpEmailReplyTo().email("ahad.naseer@hellotabeeb.com").name("HelloTabeeb Support"))

                println("Attempting to send email to: ${bookingDetails.email}")

                val result = apiInstance.sendTransacEmail(sendSmtpEmail)
                println("Email sent successfully. MessageId: ${result.messageId}")

            } catch (e: brevo.ApiException) {
                println("Brevo API Error: ${e.code} - ${e.responseBody}")
                throw Exception("Failed to send email: ${e.message}")
            } catch (e: IOException) {
                println("Network Error while sending email: ${e.message}")
                throw Exception("Network error while sending email: ${e.message}")
            } catch (e: Exception) {
                println("Unexpected error while sending email: ${e.message}")
                e.printStackTrace()
                throw Exception("Unexpected error while sending email: ${e.message}")
            }
        }
    }


    // Utility function to fix existing codes in database
    suspend fun fixCodesInDatabase() {
        // Implementation here
    }

    // Function to add new codes correctly
    suspend fun addNewCode(codeValue: String) {
        // Implementation here
    }
}