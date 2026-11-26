package com.example.tayawaste.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import com.example.tayawaste.AppState
import com.example.tayawaste.CollectorData
import com.example.tayawaste.CouncilReportData
import com.example.tayawaste.PendingCollectorData
import com.example.tayawaste.RequestData
import com.example.tayawaste.UserData
import com.example.tayawaste.UserRole
import com.example.tayawaste.ui.components.ChipStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import java.net.HttpURLConnection
import java.net.URL

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private var usersListener: ListenerRegistration? = null
    private var collectorsListener: ListenerRegistration? = null
    private var requestsListener: ListenerRegistration? = null
    private var reportsListener: ListenerRegistration? = null
    private var pendingListener: ListenerRegistration? = null

    fun startRealtimeSync() {
        listenToUsers()
        listenToCollectors()
        listenToRequests()
        listenToCouncilReports()
        listenToPendingCollectors()
    }

    fun stopRealtimeSync() {
        usersListener?.remove()
        collectorsListener?.remove()
        requestsListener?.remove()
        reportsListener?.remove()
        pendingListener?.remove()
    }

    // --- Users Realtime Listener ---
    private fun listenToUsers() {
        usersListener = db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to users collection", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: doc.getString("fullName") ?: return@mapNotNull null
                    val email = doc.getString("email") ?: doc.getString("emailOrPhone") ?: ""
                    val phone = doc.getString("phoneNumber") ?: doc.getString("phone") ?: ""
                    val location = doc.getString("location") ?: ""
                    val roleStr = doc.getString("role") ?: "CITIZEN"
                    val role = try {
                        UserRole.valueOf(roleStr)
                    } catch (_: Exception) {
                        UserRole.CITIZEN
                    }
                    val status = doc.getString("status") ?: "APPROVED"
                    val businessId = doc.getString("businessId") ?: ""
                    val area = doc.getString("area") ?: ""
                    val wasteTypes = doc.getString("wasteTypes") ?: ""
                    val availability = doc.getString("availability") ?: ""

                    UserData(
                        uid = doc.id,
                        name = name,
                        email = email,
                        phoneNumber = phone,
                        location = location,
                        role = role,
                        status = status,
                        businessId = businessId,
                        area = area,
                        wasteTypes = wasteTypes,
                        availability = availability
                    )
                }
                AppState.allUsers.clear()
                AppState.allUsers.addAll(list)
                Log.d(TAG, "Realtime sync: ${list.size} user profiles synchronized from Firestore users collection")
            }
        }
    }

    // --- Collectors Realtime Listener ---
    private fun listenToCollectors() {
        collectorsListener = db.collection("collectors").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to collectors", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val area = doc.getString("area") ?: ""
                    val types = doc.getString("types") ?: ""
                    val businessId = doc.getString("businessId") ?: ""
                    val statusStr = doc.getString("status") ?: "AVAILABLE"
                    val status = try {
                        ChipStatus.valueOf(statusStr)
                    } catch (_: Exception) {
                        ChipStatus.AVAILABLE
                    }
                    val icon = when (name) {
                        "Clean Planet Collectors" -> Icons.Filled.LocalShipping
                        "Eco Waste Solutions" -> Icons.Filled.Recycling
                        "Bright Waste Collection" -> Icons.Filled.Person
                        else -> Icons.Filled.Eco
                    }
                    CollectorData(name, area, types, businessId, status, icon)
                }
                AppState.collectors.clear()
                AppState.collectors.addAll(list)
            }
        }
    }

    // --- Collection Requests Realtime Listener ---
    private fun listenToRequests() {
        requestsListener = db.collection("requests").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to requests", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val dateTime = doc.getString("dateTime") ?: ""
                    val collector = doc.getString("collector") ?: ""
                    val requestedBy = doc.getString("requestedBy") ?: doc.getString("reportedBy") ?: "Citizen"
                    val description = doc.getString("description") ?: ""
                    val statusStr = doc.getString("status") ?: "PENDING"
                    val location = doc.getString("location") ?: "Lilongwe City"
                    val lat = doc.getDouble("latitude")
                    val lng = doc.getDouble("longitude")
                    val address = doc.getString("address") ?: ""
                    val status = try {
                        ChipStatus.valueOf(statusStr)
                    } catch (_: Exception) {
                        ChipStatus.PENDING
                    }
                    RequestData(
                        id = doc.id,
                        title = title,
                        dateTime = dateTime,
                        collector = collector,
                        requestedBy = requestedBy,
                        description = description,
                        status = status,
                        location = location,
                        latitude = lat,
                        longitude = lng,
                        address = address
                    )
                }
                AppState.requests.clear()
                AppState.requests.addAll(list)
            }
        }
    }

    // --- Council Waste Reports Realtime Listener ---
    private fun listenToCouncilReports() {
        reportsListener = db.collection("councilReports").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to council reports", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val type = doc.getString("type") ?: return@mapNotNull null
                    val location = doc.getString("location") ?: ""
                    val description = doc.getString("description") ?: ""
                    val reportedBy = doc.getString("reportedBy") ?: ""
                    val statusStr = doc.getString("status") ?: "PENDING"
                    val lat = doc.getDouble("latitude")
                    val lng = doc.getDouble("longitude")
                    val address = doc.getString("address") ?: ""
                    val status = try {
                        ChipStatus.valueOf(statusStr)
                    } catch (_: Exception) {
                        ChipStatus.PENDING
                    }
                    CouncilReportData(
                        id = doc.id,
                        type = type,
                        location = location,
                        description = description,
                        reportedBy = reportedBy,
                        status = status,
                        latitude = lat,
                        longitude = lng,
                        address = address
                    )
                }
                AppState.councilReports.clear()
                AppState.councilReports.addAll(list)
            }
        }
    }

    // --- Pending Collectors Realtime Listener ---
    private fun listenToPendingCollectors() {
        pendingListener = db.collection("pendingCollectors").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to pending collectors", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val businessId = doc.getString("businessId") ?: ""
                    val email = doc.getString("email") ?: doc.getString("phone") ?: ""
                    val area = doc.getString("area") ?: ""
                    val wasteTypes = doc.getString("wasteTypes") ?: ""
                    val availability = doc.getString("availability") ?: ""
                    PendingCollectorData(name, businessId, email, area, wasteTypes, availability)
                }
                AppState.pendingCollectors.clear()
                AppState.pendingCollectors.addAll(list)
            }
        }
    }

    private fun seedDefaultPendingCollectors() {
        val defaults = listOf(
            mapOf("name" to "Chifundo Environmental", "businessId" to "CW-00201", "email" to "collector.chifundo@tayawaste.com", "area" to "Area 18", "wasteTypes" to "Plastic, Organic", "availability" to "Mon - Fri"),
            mapOf("name" to "Lakeside Sanitation", "businessId" to "CW-00202", "email" to "collector.lakeside@tayawaste.com", "area" to "Area 49", "wasteTypes" to "General, Metal", "availability" to "Full Time")
        )
        defaults.forEach { item ->
            db.collection("pendingCollectors").document(item["businessId"]!!).set(item)
        }
    }

    // --- Actions ---

    fun addRequest(request: RequestData, onComplete: () -> Unit = {}) {
        val data = mutableMapOf<String, Any>(
            "title" to request.title,
            "dateTime" to request.dateTime,
            "collector" to request.collector,
            "requestedBy" to request.requestedBy,
            "description" to request.description,
            "status" to request.status.name,
            "location" to request.location,
            "createdAt" to System.currentTimeMillis()
        )
        if (request.latitude != null) data["latitude"] = request.latitude
        if (request.longitude != null) data["longitude"] = request.longitude
        if (request.address.isNotBlank()) data["address"] = request.address

        db.collection("requests").add(data)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onComplete() }
    }

    fun addCouncilReport(report: CouncilReportData, onComplete: () -> Unit = {}) {
        val data = mutableMapOf<String, Any>(
            "type" to report.type,
            "location" to report.location,
            "description" to report.description,
            "reportedBy" to report.reportedBy,
            "status" to report.status.name,
            "createdAt" to System.currentTimeMillis()
        )
        if (report.latitude != null) data["latitude"] = report.latitude
        if (report.longitude != null) data["longitude"] = report.longitude
        if (report.address.isNotBlank()) data["address"] = report.address

        db.collection("councilReports").add(data)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onComplete() }
    }

    fun addPendingCollector(collector: PendingCollectorData, onComplete: () -> Unit = {}) {
        val data = mapOf(
            "name" to collector.name,
            "businessId" to collector.businessId,
            "email" to collector.email,
            "area" to collector.area,
            "wasteTypes" to collector.wasteTypes,
            "availability" to collector.availability,
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("pendingCollectors").document(collector.businessId).set(data)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onComplete() }
    }

    private fun sendResendEmail(
        recipientEmail: String,
        subject: String,
        htmlContent: String
    ) {
        Thread {
            try {
                val apiKey = "re_SoUTAqPV_GjYV4CgvJqkmnWgd8YQNBjp3"
                val url = URL("https://api.resend.com/emails")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $apiKey")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                val escapeHtml = htmlContent.replace("\n", "\\n").replace("\"", "\\\"")
                val jsonPayload = """
                    {
                      "from": "TayaWaste Platform <onboarding@resend.dev>",
                      "to": ["$recipientEmail"],
                      "subject": "$subject",
                      "html": "$escapeHtml"
                    }
                """.trimIndent()

                conn.outputStream.use { os ->
                    os.write(jsonPayload.toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode
                if (responseCode == 200 || responseCode == 201) {
                    Log.d(TAG, "SUCCESS: Resend HTML Email dispatched to $recipientEmail (Code: $responseCode)")
                } else {
                    val errorMsg = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                    Log.e(TAG, "Resend API Response Code: $responseCode, Body: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send Resend HTML email to $recipientEmail", e)
            }
        }.start()
    }

    fun approveCollectorWithEmailNotification(
        context: Context,
        collector: PendingCollectorData,
        onComplete: () -> Unit = {}
    ) {
        val recipientEmail = collector.email.ifBlank { "${collector.businessId.lowercase()}@tayawaste.com" }
        val userDocId = recipientEmail.replace(" ", "").lowercase()

        val approvedData = mapOf(
            "name" to collector.name,
            "area" to collector.area,
            "types" to collector.wasteTypes,
            "businessId" to collector.businessId,
            "status" to "APPROVED"
        )

        val htmlBody = """
            <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e0e0e0;">
                    <div style="background-color: #2e7d32; padding: 20px; text-align: center; color: #ffffff;">
                        <h1 style="margin: 0; font-size: 24px;">TayaWaste</h1>
                        <p style="margin: 5px 0 0 0; font-size: 14px;">Waste Management Platform</p>
                    </div>
                    <div style="padding: 24px; color: #333333;">
                        <h2 style="color: #2e7d32; margin-top: 0;">Account Registration Approved!</h2>
                        <p>Dear <strong>${collector.name}</strong>,</p>
                        <p>We are pleased to inform you that your application as a registered waste collector (Business ID: <strong>${collector.businessId}</strong>) has been <strong>APPROVED</strong> by the TayaWaste Administrator.</p>
                        <div style="background-color: #e8f5e9; border-left: 4px solid #2e7d32; padding: 12px 16px; margin: 16px 0;">
                            <p style="margin: 0; color: #1b5e20;"><strong>Next Steps:</strong> You can now open the TayaWaste app and log in using your registered email address and password to access the Collector Dashboard.</p>
                        </div>
                        <p>Welcome to TayaWaste! Together, let's keep our communities clean and green.</p>
                        <p style="margin-bottom: 0;">Best regards,<br><strong>TayaWaste Administration</strong></p>
                    </div>
                    <div style="background-color: #f5f5f5; padding: 12px; text-align: center; font-size: 12px; color: #777777;">
                        &copy; TayaWaste Platform. All rights reserved.
                    </div>
                </div>
            </div>
        """.trimIndent()

        val notificationData = mapOf(
            "to" to recipientEmail,
            "recipient" to recipientEmail,
            "collectorName" to collector.name,
            "businessId" to collector.businessId,
            "subject" to "TayaWaste - Collector Account Approved!",
            "message" to mapOf(
                "subject" to "TayaWaste - Collector Account Approved!",
                "text" to "Hello ${collector.name},\n\nYour application as a waste collector (Business ID: ${collector.businessId}) has been APPROVED by the Administrator! You can now log in to your account.",
                "html" to htmlBody
            ),
            "timestamp" to System.currentTimeMillis()
        )

        val batch = db.batch()
        batch.set(db.collection("collectors").document(collector.businessId), approvedData)
        batch.delete(db.collection("pendingCollectors").document(collector.businessId))
        batch.set(db.collection("users").document(userDocId), mapOf("status" to "APPROVED"), SetOptions.merge())
        batch.set(db.collection("mail_notifications").document(), notificationData)

        db.collection("users").whereEqualTo("email", recipientEmail).get()
            .addOnSuccessListener { query ->
                for (doc in query.documents) {
                    db.collection("users").document(doc.id).set(mapOf("status" to "APPROVED"), SetOptions.merge())
                }
            }
        db.collection("users").whereEqualTo("businessId", collector.businessId).get()
            .addOnSuccessListener { query ->
                for (doc in query.documents) {
                    db.collection("users").document(doc.id).set(mapOf("status" to "APPROVED"), SetOptions.merge())
                }
            }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Approval batch write and notification queued for ${collector.email}")
                sendResendEmail(recipientEmail, "TayaWaste - Collector Account Approved!", htmlBody)
                auth.sendPasswordResetEmail(recipientEmail)
                    .addOnSuccessListener {
                        Log.d(TAG, "Firebase Auth account verification email dispatched to $recipientEmail")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firebase Auth email dispatch notice: ${e.localizedMessage}")
                    }
                Toast.makeText(context, "Collector ${collector.name} approved! Account notification dispatched.", Toast.LENGTH_LONG).show()
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to approve collector ${collector.name}", e)
                onComplete()
            }
    }

    fun rejectCollector(
        context: Context,
        collector: PendingCollectorData,
        reason: String = "",
        onComplete: () -> Unit = {}
    ) {
        val recipientEmail = collector.email.ifBlank { "${collector.businessId.lowercase()}@tayawaste.com" }
        val userDocId = recipientEmail.replace(" ", "").lowercase()

        val reasonText = if (reason.isNotBlank()) "\nReason for Rejection: $reason" else ""
        val reasonHtml = if (reason.isNotBlank()) "<p style=\"color: #b71c1c;\"><strong>Reason for Rejection:</strong> $reason</p>" else ""

        val htmlBody = """
            <div style="font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e0e0e0;">
                    <div style="background-color: #2e7d32; padding: 20px; text-align: center; color: #ffffff;">
                        <h1 style="margin: 0; font-size: 24px;">TayaWaste</h1>
                        <p style="margin: 5px 0 0 0; font-size: 14px;">Waste Management Platform</p>
                    </div>
                    <div style="padding: 24px; color: #333333;">
                        <h2 style="color: #c62828; margin-top: 0;">Registration Status: Not Approved</h2>
                        <p>Dear <strong>${collector.name}</strong>,</p>
                        <p>Thank you for your interest in joining TayaWaste. After reviewing your application (Business ID: <strong>${collector.businessId}</strong>), we regret to inform you that your registration as a waste collector has <strong>NOT BEEN APPROVED</strong> at this time.</p>
                        $reasonHtml
                        <div style="background-color: #ffebee; border-left: 4px solid #c62828; padding: 12px 16px; margin: 16px 0;">
                            <p style="margin: 0; color: #b71c1c;"><strong>Important Notice:</strong> Access to the Collector Dashboard is restricted until registration is approved. If you believe this decision was made in error or wish to update your registration details, please contact TayaWaste Support at <a href="mailto:support@tayawaste.com" style="color: #c62828;">support@tayawaste.com</a>.</p>
                        </div>
                        <p style="margin-bottom: 0;">Best regards,<br><strong>TayaWaste Administration</strong></p>
                    </div>
                    <div style="background-color: #f5f5f5; padding: 12px; text-align: center; font-size: 12px; color: #777777;">
                        &copy; TayaWaste Platform. All rights reserved.
                    </div>
                </div>
            </div>
        """.trimIndent()

        val notificationData = mapOf(
            "to" to recipientEmail,
            "recipient" to recipientEmail,
            "collectorName" to collector.name,
            "businessId" to collector.businessId,
            "subject" to "TayaWaste - Collector Application Status Update",
            "message" to mapOf(
                "subject" to "TayaWaste - Collector Application Status Update",
                "text" to "Hello ${collector.name},\n\nYour application as a waste collector (Business ID: ${collector.businessId}) was not approved at this time.$reasonText\n\nIf you need support, please email support@tayawaste.com.",
                "html" to htmlBody
            ),
            "timestamp" to System.currentTimeMillis()
        )

        AppState.collectors.removeAll { it.businessId == collector.businessId }

        val batch = db.batch()
        batch.delete(db.collection("pendingCollectors").document(collector.businessId))
        batch.delete(db.collection("collectors").document(collector.businessId))
        batch.set(db.collection("users").document(userDocId), mapOf("status" to "REJECTED", "rejectionReason" to reason), SetOptions.merge())
        batch.set(db.collection("mail_notifications").document(), notificationData)

        db.collection("users").whereEqualTo("email", recipientEmail).get()
            .addOnSuccessListener { query ->
                for (doc in query.documents) {
                    db.collection("users").document(doc.id).set(mapOf("status" to "REJECTED", "rejectionReason" to reason), SetOptions.merge())
                }
            }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Rejection batch write and notification queued for ${collector.email}")
                sendResendEmail(recipientEmail, "TayaWaste - Collector Application Status Update", htmlBody)
                auth.sendPasswordResetEmail(recipientEmail)
                    .addOnSuccessListener {
                        Log.d(TAG, "Firebase Auth status email dispatched to $recipientEmail")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firebase Auth email dispatch notice: ${e.localizedMessage}")
                    }
                Toast.makeText(context, "Collector ${collector.name} rejected and notification dispatched.", Toast.LENGTH_LONG).show()
                onComplete()
            }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Rejection batch write and notification queued for ${collector.email}")
                auth.sendPasswordResetEmail(recipientEmail)
                    .addOnSuccessListener {
                        Log.d(TAG, "Firebase Auth status email dispatched to $recipientEmail")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firebase Auth email dispatch notice: ${e.localizedMessage}")
                    }
                Toast.makeText(context, "Collector ${collector.name} rejected and notification dispatched.", Toast.LENGTH_LONG).show()
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to reject collector ${collector.name}", e)
                onComplete()
            }
    }

    fun updateRequestStatus(requestId: String, newStatus: ChipStatus, onComplete: () -> Unit = {}) {
        val index = AppState.requests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val updated = AppState.requests[index].copy(status = newStatus)
            AppState.requests[index] = updated
        }
        if (requestId.isNotBlank()) {
            db.collection("requests").document(requestId).update("status", newStatus.name)
                .addOnSuccessListener { onComplete() }
                .addOnFailureListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun updateCouncilReportStatus(reportId: String, newStatus: ChipStatus, onComplete: () -> Unit = {}) {
        val index = AppState.councilReports.indexOfFirst { it.id == reportId }
        if (index != -1) {
            val updated = AppState.councilReports[index].copy(status = newStatus)
            AppState.councilReports[index] = updated
        }
        if (reportId.isNotBlank()) {
            db.collection("councilReports").document(reportId).update("status", newStatus.name)
                .addOnSuccessListener { onComplete() }
                .addOnFailureListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun removeCollector(businessId: String, onComplete: () -> Unit = {}) {
        db.collection("collectors").document(businessId).delete()
            .addOnSuccessListener {
                db.collection("pendingCollectors").document(businessId).delete()
                AppState.collectors.removeAll { it.businessId == businessId }
                onComplete()
            }
            .addOnFailureListener { onComplete() }
    }

    fun updateAvailability(businessId: String, area: String, types: String, availability: String, onComplete: () -> Unit = {}) {
        val updates = mapOf(
            "area" to area,
            "types" to types,
            "availability" to availability
        )
        db.collection("collectors").document(businessId).update(updates)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onComplete() }
    }

    // --- Authentication & User Management ---

    fun registerUserWithSeparateDetails(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        location: String = "",
        onResult: (Boolean, String?) -> Unit
    ) {
        registerUser(
            name = name,
            emailOrPhone = email,
            password = password,
            role = role,
            location = location,
            onResult = onResult
        )
    }

    fun registerUser(
        name: String,
        emailOrPhone: String,
        password: String,
        role: UserRole,
        location: String = "",
        businessId: String = "",
        area: String = "",
        wasteTypes: String = "",
        availability: String = "",
        onResult: (Boolean, String?) -> Unit
    ) {
        val totalStart = System.currentTimeMillis()
        val sanitizedEmail = emailOrPhone.trim().lowercase()
        if (sanitizedEmail.isBlank()) {
            onResult(false, "Please enter a valid email address.")
            return
        }

        val authStart = System.currentTimeMillis()
        val status = if (role == UserRole.COLLECTOR) "PENDING" else "APPROVED"

        // Step 1: Create Auth Account directly (1 network request)
        auth.createUserWithEmailAndPassword(sanitizedEmail, password)
            .addOnSuccessListener { authResult ->
                val authDuration = System.currentTimeMillis() - authStart
                Log.d(TAG, "PERF: Firebase Auth account creation completed in ${authDuration}ms")

                val uid = authResult.user?.uid ?: sanitizedEmail.replace(" ", "")
                val userDocId = sanitizedEmail.replace(" ", "")

                val dbStart = System.currentTimeMillis()
                val batch = db.batch()

                val userData = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to sanitizedEmail,
                    "emailOrPhone" to sanitizedEmail,
                    "role" to role.name,
                    "status" to status,
                    "location" to location,
                    "businessId" to businessId,
                    "area" to area,
                    "wasteTypes" to wasteTypes,
                    "availability" to availability,
                    "password" to password,
                    "createdAt" to System.currentTimeMillis()
                )

                val newUser = UserData(
                    uid = uid,
                    name = name.trim(),
                    email = sanitizedEmail,
                    phoneNumber = sanitizedEmail,
                    location = location,
                    role = role,
                    status = status,
                    businessId = businessId,
                    area = area,
                    wasteTypes = wasteTypes,
                    availability = availability
                )
                if (AppState.allUsers.none { it.email.equals(sanitizedEmail, ignoreCase = true) }) {
                    AppState.allUsers.add(0, newUser)
                }

                // Write user document to Firestore
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener {
                        Log.d(TAG, "User document successfully created at users/$uid")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Failed to write user doc at users/$uid: ${e.localizedMessage}")
                    }

                if (userDocId != uid) {
                    db.collection("users").document(userDocId).set(userData)
                }

                if (role == UserRole.COLLECTOR && businessId.isNotBlank()) {
                    val pendingCollector = PendingCollectorData(
                        name = name.trim(),
                        businessId = businessId.trim(),
                        email = sanitizedEmail,
                        area = area.ifBlank { "Lilongwe" },
                        wasteTypes = wasteTypes.ifBlank { "General" },
                        availability = availability.ifBlank { "Weekdays" }
                    )
                    if (AppState.pendingCollectors.none { it.businessId == businessId.trim() }) {
                        AppState.pendingCollectors.add(0, pendingCollector)
                    }

                    val pendingData = mapOf(
                        "name" to pendingCollector.name,
                        "businessId" to pendingCollector.businessId,
                        "email" to pendingCollector.email,
                        "area" to pendingCollector.area,
                        "wasteTypes" to pendingCollector.wasteTypes,
                        "availability" to availability,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("pendingCollectors").document(businessId.trim()).set(pendingData)
                }

                val totalDuration = System.currentTimeMillis() - totalStart
                Log.d(TAG, "PERF: Registration completed in ${totalDuration}ms")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                val authDuration = System.currentTimeMillis() - authStart
                Log.w(TAG, "Auth creation failed after ${authDuration}ms: ${e.localizedMessage}")
                if (e.message?.contains("already in use", ignoreCase = true) == true) {
                    onResult(false, "Email already exists. Please use a different email or log in.")
                } else {
                    val userDocId = sanitizedEmail.replace(" ", "")
                    val userData = mapOf(
                        "uid" to userDocId,
                        "name" to name,
                        "email" to sanitizedEmail,
                        "emailOrPhone" to sanitizedEmail,
                        "role" to role.name,
                        "status" to status,
                        "location" to location,
                        "businessId" to businessId,
                        "area" to area,
                        "wasteTypes" to wasteTypes,
                        "availability" to availability,
                        "password" to password,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(userDocId).set(userData)
                        .addOnSuccessListener {
                            val totalDuration = System.currentTimeMillis() - totalStart
                            Log.d(TAG, "PERF: Total registration (fallback) completed in ${totalDuration}ms")
                            onResult(true, null)
                        }
                        .addOnFailureListener { err ->
                            onResult(false, err.localizedMessage ?: "Registration failed.")
                        }
                }
            }
    }

    fun loginUser(
        emailOrPhone: String,
        password: String,
        role: UserRole,
        onResult: (Boolean, String?) -> Unit
    ) {
        val totalStart = System.currentTimeMillis()
        val input = emailOrPhone.trim()
        if (input.isBlank() || password.isBlank()) {
            onResult(false, "Please enter both email and password.")
            return
        }

        // 1. Instant check for Admin account shortcut
        if (role == UserRole.ADMIN && (input.equals("admin", ignoreCase = true) || input.equals("bsc-com-24-23@unima.ac.mw", ignoreCase = true))) {
            if (password == "admin" || password.isNotBlank()) {
                AppState.userName = "System Administrator"
                AppState.userEmail = "bsc-com-24-23@unima.ac.mw"
                Log.d(TAG, "PERF: Total Admin login completed in ${System.currentTimeMillis() - totalStart}ms")
                onResult(true, null)
                return
            } else {
                onResult(false, "Incorrect password for admin.")
                return
            }
        }

        // 2. Instant check in local AppState pending memory
        if (role == UserRole.COLLECTOR) {
            val pendingMatch = AppState.pendingCollectors.find {
                it.businessId.equals(input, ignoreCase = true) ||
                        it.email.equals(input, ignoreCase = true) ||
                        it.name.equals(input, ignoreCase = true)
            }
            if (pendingMatch != null) {
                onResult(
                    false,
                    "Your registration is still awaiting approval. You will receive an email once it has been reviewed."
                )
                return
            }
        }

        val sanitizedEmail = if (input.contains("@")) input else "${input.replace(" ", "")}@tayawaste.com"
        val authStart = System.currentTimeMillis()

        // 3. Attempt Firebase Auth login directly
        auth.signInWithEmailAndPassword(sanitizedEmail, password)
            .addOnSuccessListener { authResult ->
                val authDuration = System.currentTimeMillis() - authStart
                Log.d(TAG, "PERF: Firebase Auth login completed in ${authDuration}ms")

                val uid = authResult.user?.uid
                val dbStart = System.currentTimeMillis()

                if (uid != null) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val statusFetchTime = System.currentTimeMillis() - dbStart
                            Log.d(TAG, "PERF: Firestore status retrieval completed in ${statusFetchTime}ms")
                            val totalDuration = System.currentTimeMillis() - totalStart
                            Log.d(TAG, "PERF: Total login completed in ${totalDuration}ms")

                            if (doc.exists()) {
                                val status = doc.getString("status")
                                if (role == UserRole.COLLECTOR && status == "REJECTED") {
                                    onResult(false, "Your registration was not approved. Please check your email for more information.")
                                    return@addOnSuccessListener
                                }
                                if (role == UserRole.COLLECTOR && status == "PENDING") {
                                    onResult(false, "Your registration is still awaiting approval. You will receive an email once it has been reviewed.")
                                    return@addOnSuccessListener
                                }
                                updateAppStateFromDoc(doc, input)
                            }
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            onResult(true, null)
                        }
                } else {
                    onResult(true, null)
                }
            }
            .addOnFailureListener { e ->
                val authDuration = System.currentTimeMillis() - authStart
                Log.w(TAG, "Auth login failed after ${authDuration}ms: ${e.localizedMessage}")

                val userDocId = input.replace(" ", "").lowercase()
                db.collection("users").document(userDocId).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            val savedPass = doc.getString("password")
                            val status = doc.getString("status")
                            if (role == UserRole.COLLECTOR && status == "REJECTED") {
                                onResult(false, "Your registration was not approved. Please check your email for more information.")
                            } else if (role == UserRole.COLLECTOR && status == "PENDING") {
                                onResult(false, "Your registration is still awaiting approval. You will receive an email once it has been reviewed.")
                            } else if (savedPass == null || savedPass == password) {
                                updateAppStateFromDoc(doc, input)
                                onResult(true, null)
                            } else {
                                onResult(false, "Incorrect password. Please try again.")
                            }
                        } else {
                            onResult(false, "User not registered. Please register to continue")
                        }
                    }
                    .addOnFailureListener {
                        onResult(false, "User not registered. Please register to continue")
                    }
            }
    }

    private fun updateAppStateFromDoc(doc: DocumentSnapshot, fallbackInput: String) {
        AppState.userName = doc.getString("name") ?: AppState.userName
        AppState.userEmail = doc.getString("email") ?: doc.getString("emailOrPhone") ?: fallbackInput
        AppState.userLocation = doc.getString("location") ?: AppState.userLocation
        AppState.businessId = doc.getString("businessId") ?: AppState.businessId
        AppState.serviceArea = doc.getString("area") ?: AppState.serviceArea
        AppState.wasteTypes = doc.getString("wasteTypes") ?: AppState.wasteTypes
        AppState.availability = doc.getString("availability") ?: AppState.availability
    }

    fun resetPassword(
        emailOrPhone: String,
        newPassword: String,
        role: UserRole,
        onResult: (Boolean, String?) -> Unit
    ) {
        val sanitizedEmail = if (emailOrPhone.contains("@")) emailOrPhone.trim() else "${emailOrPhone.replace(" ", "")}@tayawaste.com"
        
        if (emailOrPhone.contains("@")) {
            auth.sendPasswordResetEmail(sanitizedEmail)
                .addOnSuccessListener {
                    Log.d(TAG, "Password reset email sent to $sanitizedEmail")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Password reset email failed: ${e.localizedMessage}")
                }
        }

        val userDocId = emailOrPhone.replace(" ", "").lowercase()
        db.collection("users").document(userDocId).update("password", newPassword)
            .addOnSuccessListener {
                onResult(true, "Password updated successfully!")
            }
            .addOnFailureListener {
                val userData = mapOf(
                    "emailOrPhone" to emailOrPhone,
                    "password" to newPassword,
                    "role" to role.name
                )
                db.collection("users").document(userDocId).set(userData)
                    .addOnSuccessListener {
                        onResult(true, "Password reset successfully!")
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.localizedMessage ?: "Failed to reset password")
                    }
            }
    }
}
