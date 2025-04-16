package com.example.hellotabeeb.Screens.labtests

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brevo.auth.ApiKeyAuth
import brevo.Configuration
import brevoApi.TransactionalEmailsApi
import brevoModel.SendSmtpEmail
import brevoModel.SendSmtpEmailReplyTo
import brevoModel.SendSmtpEmailSender
import brevoModel.SendSmtpEmailTo
import com.example.hellotabeeb.BuildConfig
import com.example.hellotabeeb.utils.PrescriptionUploadToDrive
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.SetOptions

data class BookingDetails(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val testName: String = "",
    val testFee: String = "",
    val totalFee: String = "",
    val labName: String = "",
    val hasPrescription: Boolean = false,
    val prescriptionUri: Uri? = null,    // Local URI
    val prescriptionUrl: String = ""     // Drive URL after upload
)

class ConfirmationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val BREVO_API_KEYS = BuildConfig.BREVO_API_KEYS



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
        apiKeyAuth.apiKey = BREVO_API_KEYS
    }

    fun confirmBooking(bookingDetails: BookingDetails) {
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading

            try {
                // 1. Get code based on lab name
                val discountCode = getUnusedCode(bookingDetails.labName)

                // 2. Upload prescription to Google Drive if available
                var prescriptionUrl = ""
                if (bookingDetails.hasPrescription && bookingDetails.prescriptionUri != null) {
                    val uploader = PrescriptionUploadToDrive(getApplication())
                    prescriptionUrl = uploader.uploadPrescription(
                        bookingDetails.prescriptionUri,
                        bookingDetails.name,
                        discountCode
                    )
                }

                // 3. Create updated booking details with prescription URL
                val updatedBookingDetails = bookingDetails.copy(prescriptionUrl = prescriptionUrl)

                // 4. Move code to availedCodes collection with user details
                moveCodeToAvailed(discountCode, updatedBookingDetails)

                // 5. Send confirmation email
                sendConfirmationEmail(updatedBookingDetails, discountCode)

                _bookingState.value = BookingState.Success(discountCode)
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }



    private suspend fun getUnusedCode(labName: String): String {
        val firestoreLabName = getFirestoreLabName(labName)

        // Return static codes for labs other than Chughtai
        return when (firestoreLabName) {
            "chughtaiLab" -> fetchCodeFromFirestore()
            "essa" -> "hellotabib"
            "excel" -> "HTB"
            "IDC" -> "IDC"
            else -> "hellotabib" // Default fallback
        }
    }

    private suspend fun fetchCodeFromFirestore(): String {
        try {
            // Original implementation for Chughtai Lab
            val snapshot = db.collection("codes")
                .whereEqualTo("isUsed", "false")
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                throw Exception("No discount codes available")
            }

            val document = snapshot.documents[0]
            return document.getString("code") ?: throw Exception("Invalid discount code format")
        } catch (e: Exception) {
            println("Error fetching code: ${e.message}")
            throw Exception("Failed to get discount code: ${e.message}")
        }
    }

private suspend fun moveCodeToAvailed(code: String, bookingDetails: BookingDetails) {
    val batch = db.batch()
    val firestoreLabName = getFirestoreLabName(bookingDetails.labName)

    try {
        // Only delete from codes collection for Chughtai Lab
        if (firestoreLabName == "chughtaiLab") {
            val codeQuery = db.collection("codes")
                .whereEqualTo("code", code)
                .whereEqualTo("isUsed", "false")
                .limit(1)
                .get()
                .await()

            if (!codeQuery.isEmpty) {
                batch.delete(codeQuery.documents[0].reference)
            } else {
                throw Exception("Code not found or already used")
            }
        }

        // Get current month-year format (e.g., "01-2025")
        val calendar = java.util.Calendar.getInstance()
        val monthYear = String.format(
            "%02d-%d",
            calendar.get(java.util.Calendar.MONTH) + 1,
            calendar.get(java.util.Calendar.YEAR)
        )

        val availedCodeData = hashMapOf(
            "code" to code,
            "userName" to bookingDetails.name,
            "userEmail" to bookingDetails.email,
            "userPhone" to bookingDetails.phone,
            "userAge" to bookingDetails.age,
            "hasPrescription" to bookingDetails.hasPrescription,
            "prescriptionUrl" to bookingDetails.prescriptionUrl, // Add this line
            "testName" to bookingDetails.testName,
            "testFee" to bookingDetails.testFee,
            "labName" to bookingDetails.labName,
            "labNameFirestore" to firestoreLabName,
            "availedAt" to Timestamp.now()
        )

        // Create the monthly document if it doesn't exist
        val monthYearDocRef = db.collection("availedCodes").document(monthYear)
        batch.set(monthYearDocRef, hashMapOf<String, Any>(), SetOptions.merge())

        // Generate a unique document ID
        val uniqueDocId = if (firestoreLabName == "chughtaiLab") {
            // For Chughtai Lab, use the code itself as before
            code
        } else {
            // For static codes, append a timestamp to make it unique
            "${code}_${System.currentTimeMillis()}"
        }

        // Save code data with the unique document ID
        val codeDocRef = db.collection("availedCodes")
                          .document(monthYear)
                          .collection("codes")
                          .document(uniqueDocId)

        // Save code data
        batch.set(codeDocRef, availedCodeData)

        batch.commit().await()
    } catch (e: Exception) {
        throw Exception("Failed to process discount code: ${e.message}")
    }
}

    // Import this function from LabDetailViewModel
    private fun getFirestoreLabName(labName: String): String {
        return when (labName) {
            "Chughtai Lab" -> "chughtaiLab"
            "Excel Labs" -> "excel"
            "Dr. Essa Laboratory & Diagnostic Centre" -> "essa"
            "Islamabad Diagnostic Center" -> "IDC"
            else -> labName
        }
    }

    private suspend fun sendConfirmationEmail(bookingDetails: BookingDetails, discountCode: String) {
        withContext(Dispatchers.IO) {
            try {
                val apiInstance = TransactionalEmailsApi()

                val sender = SendSmtpEmailSender()
                    .email("support@hellotabeeb.com")
                    .name("HelloTabeeb Lab Services")

                val to = listOf(
                    SendSmtpEmailTo()
                        .email(bookingDetails.email)
                        .name(bookingDetails.name)
                )

                // Split test names and fees for better display
                val testNames = bookingDetails.testName.split(",")
                val testFees = bookingDetails.testFee.split(",").mapNotNull { it.toDoubleOrNull() }

                // Build test details HTML with table rows
                val testDetailsRows = StringBuilder()
                testNames.forEachIndexed { index, name ->
                    val fee = if (index < testFees.size) "Rs. ${testFees[index]}" else ""
                    testDetailsRows.append("""
                        <tr>
                            <td style="padding: 10px; border-bottom: 1px solid #e2e8f0;">${name}</td>
                            <td style="padding: 10px; text-align: right; border-bottom: 1px solid #e2e8f0;">${fee}</td>
                        </tr>
                    """)
                }

                val subject = "Lab Test Booking Confirmation"
                val htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto;">
                    <div style="padding: 20px;">
                        <h2 style="color: #333; font-size: 20px; margin-bottom: 20px; font-weight: normal;">Lab Test Booking Confirmation</h2>
                        
                        <p>Dear ${bookingDetails.name},</p>
                        
                        <p>Thank you for booking your lab test with HelloTabeeb. Your booking has been confirmed.</p>
                        
                        <div style="margin: 30px 0; background-color: #f8f9fa; padding: 15px; border-radius: 6px;">
                            <h3 style="color: #333; font-size: 16px; margin-bottom: 15px;">Booking Details:</h3>
                            
                            <table style="width: 100%; border-collapse: collapse; background-color: #ffffff; border-radius: 4px;">
                                <thead>
                                    <tr style="background-color: #edf2f7;">
                                        <th style="padding: 12px; text-align: left; border-bottom: 2px solid #e2e8f0;">Test</th>
                                        <th style="padding: 12px; text-align: right; border-bottom: 2px solid #e2e8f0;">Fee</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${testDetailsRows}
                                    <tr style="background-color: #edf2f7; font-weight: bold;">
                                        <td style="padding: 12px;">Total</td>
                                        <td style="padding: 12px; text-align: right;">Rs. ${testFees.sum()}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        
                        <div style="margin: 20px 0; background-color: #fff8f8; padding: 15px; border-radius: 6px; border-left: 4px solid #ff3333; text-align: center;">
                            <p style="margin: 0; font-size: 16px;">Your Discount Code</p>
                            <p style="font-size: 24px; font-weight: bold; margin: 10px 0; color: #ff3333; letter-spacing: 2px;">${discountCode}</p>
                        </div>
                        
                        <p>Your lab test is booked with ${bookingDetails.labName}</p>
                        
                        <div style="margin: 30px 0;">
                            <a href="#" style="display: inline-block; background-color: #2f3e4e; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; font-weight: bold;">Book Again</a>
                        </div>
                        
                        <p>For support or to Book Home sampling, please call us at</p>
                        
                        <p style="font-size: 18px; font-weight: bold; margin: 15px 0;">0337 4373334</p>
                        
                        <p>Thank You!</p>
                        
                        <div style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;">
                            <p style="color: #666; font-size: 14px;">Show this mail at the Lab during your visit or home sampling to avail discount</p>
                        </div>
                    </div>
                </body>
                </html>
                """.trimIndent()

                val sendSmtpEmail = SendSmtpEmail()
                    .sender(sender)
                    .to(to)
                    .subject(subject)
                    .htmlContent(htmlContent)
                    .replyTo(SendSmtpEmailReplyTo().email("support@hellotabeeb.com").name("HelloTabeeb Support"))

                println("Attempting to send email to: ${bookingDetails.email}")

                val result = apiInstance.sendTransacEmail(sendSmtpEmail)
                println("Email sent successfully. MessageId: ${result.messageId}")

            } catch (e: Exception) {
                // Error handling code remains the same
                println("Error sending email: ${e.message}")
                throw Exception("Failed to send email: ${e.message}")
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


//shdvfherbvfhrb