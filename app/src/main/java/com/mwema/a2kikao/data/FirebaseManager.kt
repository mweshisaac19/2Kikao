package com.mwema.a2kikao.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mwema.a2kikao.ui.screens.auth.UserRole
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUserUId: String?
        get() = auth.currentUser?.uid

    suspend fun signUp(email: String, password: String, profile: UserProfile): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed: UID is null")
            
            val finalProfile = profile.copy(uid = uid)
            firestore.collection("users").document(uid).set(finalProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed: UID is null")
            
            val document = firestore.collection("users").document(uid).get().await()
            val profile = document.toObject(UserProfile::class.java) ?: throw Exception("User profile not found")
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStudentClasses(studentId: String): List<CourseClass> {
        return try {
            val snapshot = firestore.collection("classes")
                .whereArrayContains("studentsEnrolled", studentId)
                .get().await()
            snapshot.toObjects(CourseClass::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLecturerClasses(lecturerId: String): List<CourseClass> {
        return try {
            val snapshot = firestore.collection("classes")
                .whereEqualTo("lecturerId", lecturerId)
                .get().await()
            snapshot.toObjects(CourseClass::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNotificationsForStudent(studentId: String): List<KikaoNotification> {
        return try {
            // Fetch global notifications and class-specific notifications
            val globalSnapshot = firestore.collection("notifications")
                .whereEqualTo("classId", null)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            
            val classes = getStudentClasses(studentId)
            val classIds = classes.map { it.id }
            
            val classNotifications = if (classIds.isNotEmpty()) {
                firestore.collection("notifications")
                    .whereIn("classId", classIds)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().await().toObjects(KikaoNotification::class.java)
            } else {
                emptyList()
            }

            (globalSnapshot.toObjects(KikaoNotification::class.java) + classNotifications)
                .sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNotificationsForLecturer(lecturerId: String): List<KikaoNotification> {
        return try {
            // Lecturers see system notifications and student requests (represented as notifications here)
            val globalSnapshot = firestore.collection("notifications")
                .whereEqualTo("classId", null)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            val globalNotifs = globalSnapshot.toObjects(KikaoNotification::class.java)

            // Also fetch student requests as "notifications" for the dashboard
            val classes = getLecturerClasses(lecturerId)
            val classIds = classes.map { it.id }

            val requestNotifs = if (classIds.isNotEmpty()) {
                val requestsSnapshot = firestore.collection("attendance_requests")
                    .whereIn("classId", classIds)
                    .orderBy("submittedAt", Query.Direction.DESCENDING)
                    .limit(10)
                    .get().await()
                
                requestsSnapshot.documents.map { doc ->
                    val type = doc.getString("type") ?: "LEAVE"
                    KikaoNotification(
                        id = doc.id,
                        title = "New $type Request",
                        message = doc.getString("reason") ?: "A student has submitted a request.",
                        sender = "Student",
                        timestamp = doc.getLong("submittedAt") ?: 0,
                        type = type.lowercase()
                    )
                }
            } else {
                emptyList()
            }

            (globalNotifs + requestNotifs).sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFinanceRecord(studentId: String): FinanceRecord? {
        return try {
            val document = firestore.collection("finance").document(studentId).get().await()
            document.toObject(FinanceRecord::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPendingLeaveRequests(lecturerId: String): List<AttendanceRequestData> {
        return try {
            val classes = getLecturerClasses(lecturerId)
            val classIds = classes.map { it.id }
            
            if (classIds.isEmpty()) return emptyList<AttendanceRequestData>()
            
            val snapshot = firestore.collection("attendance_requests")
                .whereIn("classId", classIds)
                .whereEqualTo("type", "LEAVE")
                .get().await()
                
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(AttendanceRequestData::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList<AttendanceRequestData>()
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
