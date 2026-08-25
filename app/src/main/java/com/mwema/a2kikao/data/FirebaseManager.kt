package com.mwema.a2kikao.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mwema.a2kikao.ui.screens.auth.UserRole
import kotlinx.coroutines.tasks.await
import java.util.Calendar

object FirebaseManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUserUId: String?
        get() = auth.currentUser?.uid

    /**
     * Map UserRole to the corresponding Firestore collection name.
     */
    private fun getCollectionForRole(role: UserRole): String {
        return when (role) {
            UserRole.STUDENT -> "students"
            UserRole.LECTURER -> "lecturers"
            UserRole.ADMIN -> "admins"
        }
    }

    suspend fun signUp(email: String, password: String, profile: UserProfile): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed: UID is null")
            
            val finalProfile = profile.copy(uid = uid)
            
            // 1. Save to the master registry (uid -> role mapping)
            val registryData = mapOf(
                "uid" to uid,
                "email" to email,
                "role" to profile.role.name
            )
            firestore.collection("users_registry").document(uid).set(registryData).await()
            
            // 2. Save to the role-specific collection
            val collectionName = getCollectionForRole(profile.role)
            firestore.collection(collectionName).document(uid).set(finalProfile).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed: UID is null")
            
            // 1. Check the registry to find the role
            val registryDoc = firestore.collection("users_registry").document(uid).get().await()
            val roleStr = registryDoc.getString("role") ?: throw Exception("User role registry not found")
            val role = UserRole.valueOf(roleStr)
            
            // 2. Fetch the full profile from the divided collection
            val collectionName = getCollectionForRole(role)
            val document = firestore.collection(collectionName).document(uid).get().await()
            val profile = document.toObject(UserProfile::class.java) ?: throw Exception("Profile not found in $collectionName")
            
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            // Check registry first
            val registryDoc = firestore.collection("users_registry").document(uid).get().await()
            val roleStr = registryDoc.getString("role") ?: return null
            val role = UserRole.valueOf(roleStr)
            
            val collectionName = getCollectionForRole(role)
            val document = firestore.collection(collectionName).document(uid).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStudentClasses(studentId: String): List<CourseClass> {
        return try {
            val profile = getUserProfile(studentId)
            val studentCourse = profile?.course ?: ""
            
            val snapshot = if (studentCourse.isNotEmpty()) {
                firestore.collection("classes")
                    .whereEqualTo("targetCourse", studentCourse)
                    .get().await()
            } else {
                firestore.collection("classes")
                    .whereArrayContains("studentsEnrolled", studentId)
                    .get().await()
            }
            snapshot.toObjects(CourseClass::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getClassesForCourse(courseName: String): List<CourseClass> {
        return try {
            val snapshot = firestore.collection("classes")
                .whereEqualTo("targetCourse", courseName)
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

    suspend fun deleteClass(classId: String): Result<Unit> {
        return try {
            firestore.collection("classes").document(classId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotificationsForStudent(studentId: String): List<KikaoNotification> {
        return try {
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
            val globalSnapshot = firestore.collection("notifications")
                .whereEqualTo("classId", null)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            val globalNotifs = globalSnapshot.toObjects(KikaoNotification::class.java)

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
            
            if (classIds.isEmpty()) return emptyList()
            
            val snapshot = firestore.collection("attendance_requests")
                .whereIn("classId", classIds)
                .whereEqualTo("status", "PENDING")
                .get().await()
                
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(AttendanceRequestData::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
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

    suspend fun getStudentsByCourses(courseNames: List<String>): List<UserProfile> {
        return try {
            if (courseNames.isEmpty()) return emptyList()

            val snapshot = firestore.collection("students")
                .whereIn("course", courseNames)
                .get().await()
            snapshot.toObjects(UserProfile::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun recordAttendance(sessionId: String, studentProfile: UserProfile): Result<Unit> {
        return try {
            val attendanceData = mapOf(
                "uid" to studentProfile.uid,
                "studentName" to studentProfile.fullName,
                "regNo" to (studentProfile.registrationNumber ?: ""),
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("sessions").document(sessionId)
                .collection("attendees").document(studentProfile.uid)
                .set(attendanceData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionsByCourseCodes(courseCodes: List<String>): List<Map<String, Any>> {
        return try {
            if (courseCodes.isEmpty()) return emptyList()

            val snapshot = firestore.collection("sessions")
                .whereIn("courseCode", courseCodes)
                .get().await()
            
            snapshot.documents.map { doc ->
                val data = doc.data?.toMutableMap() ?: mutableMapOf()
                data["id"] = doc.id
                data
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTodaySessions(courseCodes: List<String>): List<Map<String, Any>> {
        return try {
            if (courseCodes.isEmpty()) return emptyList()
            
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfDay = calendar.timeInMillis

            val snapshot = firestore.collection("sessions")
                .whereIn("courseCode", courseCodes)
                .whereGreaterThanOrEqualTo("timestamp", startOfDay)
                .get().await()
            
            snapshot.documents.map { doc ->
                val data = doc.data?.toMutableMap() ?: mutableMapOf()
                data["id"] = doc.id
                data
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStudentAttendanceForSession(sessionId: String, studentId: String): Boolean {
        return try {
            val doc = firestore.collection("sessions").document(sessionId)
                .collection("attendees").document(studentId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
