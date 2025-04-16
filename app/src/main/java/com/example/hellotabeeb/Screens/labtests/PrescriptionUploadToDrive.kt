package com.example.hellotabeeb.utils

import android.content.Context
import android.net.Uri
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class PrescriptionUploadToDrive(private val context: Context) {
    private val FOLDER_NAME = "lab test prescription from android app"

    suspend fun uploadPrescription(fileUri: Uri, patientName: String, bookingId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Check file size
                val fileSize = getFileSize(fileUri)
                if (fileSize > 3 * 1024 * 1024) { // 3MB in bytes
                    throw Exception("File size exceeds the 3MB limit")
                }

                // Initialize Drive service
                val driveService = getDriveService()

                // Get or create folder
                val folderId = getFolderId(driveService)

                // Upload file
                val fileName = "${patientName}_${bookingId}_${System.currentTimeMillis()}"
                val mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"

                val fileMetadata = com.google.api.services.drive.model.File()
                    .setName(fileName)
                    .setParents(listOf(folderId))

                val inputStream = context.contentResolver.openInputStream(fileUri)
                    ?: throw Exception("Failed to open file")

                val content = InputStreamContent(mimeType, inputStream)

                val uploadedFile = driveService.files().create(fileMetadata, content)
                    .setFields("id, webViewLink")
                    .execute()

                // Make file readable by anyone with the link
                val permission = com.google.api.services.drive.model.Permission()
                    .setType("anyone")
                    .setRole("reader")

                driveService.permissions().create(uploadedFile.id, permission).execute()

                return@withContext uploadedFile.webViewLink
            } catch (e: Exception) {
                throw Exception("Failed to upload prescription: ${e.message}")
            }
        }
    }

    private fun getFileSize(fileUri: Uri): Long {
        val fileDescriptor = context.contentResolver.openFileDescriptor(fileUri, "r")
        val fileSize = fileDescriptor?.statSize ?: 0
        fileDescriptor?.close()
        return fileSize
    }

    private suspend fun getDriveService(): Drive {
        return withContext(Dispatchers.IO) {
            // Load service account credentials
            val inputStream = context.assets.open("service-account-key.json")

            val credential = GoogleCredential.fromStream(inputStream)
                .createScoped(listOf(DriveScopes.DRIVE_FILE))

            // Build drive service
            Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("HelloTabeeb").build()
        }
    }

    private suspend fun getFolderId(driveService: Drive): String {
        return withContext(Dispatchers.IO) {
            // Check if folder exists
            val folderList = driveService.files().list()
                .setQ("name='$FOLDER_NAME' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                .execute()

            // Return existing folder ID if found
            if (folderList.files.isNotEmpty()) {
                return@withContext folderList.files[0].id
            }

            // Create new folder if not found
            val folderMetadata = com.google.api.services.drive.model.File()
                .setName(FOLDER_NAME)
                .setMimeType("application/vnd.google-apps.folder")

            val folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute()

            return@withContext folder.id
        }
    }
}