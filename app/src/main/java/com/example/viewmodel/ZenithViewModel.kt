package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.AegisBrain
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.BuildConfig

// --- Models ---
data class StudentProfile(
    val id: String,
    val name: String,
    val email: String,
    val rollNo: String,
    val department: String,
    val level: Int = 24
)

data class ClassItem(
    val id: String,
    val subject: String,
    val dayOfWeek: String, // "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    val time: String,
    val room: String,
    val teacher: String,
    val colorHex: String
)

data class AttendanceSubject(
    val id: String,
    val name: String,
    val attended: Int,
    val total: Int
) {
    val percentage: Float
        get() = if (total == 0) 100f else (attended.toFloat() / total * 100)

    val classesNeededFor75: Int
        get() {
            if (total == 0) return 0
            if (percentage >= 75f) return 0
            // Solve for x: (attended + x) / (total + x) >= 0.75
            // attended + x >= 0.75 * total + 0.75 * x
            // 0.25 * x >= 0.75 * total - attended
            // x >= (0.75 * total - attended) / 0.25 = 3 * total - 4 * attended
            val x = 3 * total - 4 * attended
            return if (x > 0) x else 0
        }
}

data class NoteItem(
    val id: String,
    val title: String,
    val content: String,
    val folder: String,
    val dateString: String,
    val pdfName: String? = null,
    val pdfUrl: String? = null
)

data class ExamItem(
    val id: String,
    val subject: String,
    val daysRemaining: Int,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val dateString: String
)

data class SemesterGpa(
    val id: String,
    val name: String,
    val gpa: Float,
    val credits: Int
)

data class ChatMessage(
    val id: String,
    val role: String, // "user" or "aegis"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CommunityPost(
    val id: String,
    val author: String,
    val authorTitle: String,
    val content: String,
    val likes: Int,
    val tag: String,
    val commentsCount: Int,
    val likedByUser: Boolean = false
)

class ZenithViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("zenith_os_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().build()

    // --- Student Profile State Flows ---
    private val _currentStudent = MutableStateFlow<StudentProfile?>(null)
    val currentStudent: StateFlow<StudentProfile?> = _currentStudent.asStateFlow()

    private val _allStudents = MutableStateFlow<List<StudentProfile>>(emptyList())
    val allStudents: StateFlow<List<StudentProfile>> = _allStudents.asStateFlow()

    // --- State Flows ---
    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _currentTab = MutableStateFlow("Hub") // Hub, Routine, Attendance, Notes, Focus, CGPA, AI, Social
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _classes = MutableStateFlow<List<ClassItem>>(emptyList())
    val classes: StateFlow<List<ClassItem>> = _classes.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceSubject>>(emptyList())
    val attendance: StateFlow<List<AttendanceSubject>> = _attendance.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(emptyList())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _exams = MutableStateFlow<List<ExamItem>>(emptyList())
    val exams: StateFlow<List<ExamItem>> = _exams.asStateFlow()

    private val _semesters = MutableStateFlow<List<SemesterGpa>>(emptyList())
    val semesters: StateFlow<List<SemesterGpa>> = _semesters.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAegisTyping = MutableStateFlow(false)
    val isAegisTyping: StateFlow<Boolean> = _isAegisTyping.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // --- Focus State ---
    private val _focusedMinutesTotal = MutableStateFlow(0)
    val focusedMinutesTotal: StateFlow<Int> = _focusedMinutesTotal.asStateFlow()

    private val _studyStreak = MutableStateFlow(14) // default motivational streak
    val studyStreak: StateFlow<Int> = _studyStreak.asStateFlow()

    init {
        loadData()
    }

    fun completeOnboarding() {
        _onboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    // --- Routine Methods ---
    fun addClass(classItem: ClassItem) {
        val updated = _classes.value + classItem
        _classes.value = updated
        saveList("routine_classes_${getStudentSuffix()}", updated, ClassItem::class.java)
    }

    fun removeClass(id: String) {
        val updated = _classes.value.filter { it.id != id }
        _classes.value = updated
        saveList("routine_classes_${getStudentSuffix()}", updated, ClassItem::class.java)
    }

    // --- Attendance Methods ---
    fun addSubject(name: String) {
        val updated = _attendance.value + AttendanceSubject(
            id = System.currentTimeMillis().toString(),
            name = name,
            attended = 0,
            total = 0
        )
        _attendance.value = updated
        saveList("attendance_${getStudentSuffix()}", updated, AttendanceSubject::class.java)
    }

    fun updateAttendance(id: String, attendedChange: Int, totalChange: Int) {
        val updated = _attendance.value.map {
            if (it.id == id) {
                val newAttended = (it.attended + attendedChange).coerceAtLeast(0)
                val newTotal = (it.total + totalChange).coerceAtLeast(newAttended)
                it.copy(attended = newAttended, total = newTotal)
            } else it
        }
        _attendance.value = updated
        saveList("attendance_${getStudentSuffix()}", updated, AttendanceSubject::class.java)
    }

    fun removeSubject(id: String) {
        val updated = _attendance.value.filter { it.id != id }
        _attendance.value = updated
        saveList("attendance_${getStudentSuffix()}", updated, AttendanceSubject::class.java)
    }

    // --- Notes Methods ---
    fun addNote(title: String, content: String, folder: String, pdfName: String? = null, pdfUrl: String? = null) {
        val updated = _notes.value + NoteItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            folder = folder,
            dateString = "May 23, 2026",
            pdfName = pdfName,
            pdfUrl = pdfUrl
        )
        _notes.value = updated
        saveList("notes_${getStudentSuffix()}", updated, NoteItem::class.java)
    }

    fun removeNote(id: String) {
        val updated = _notes.value.filter { it.id != id }
        _notes.value = updated
        saveList("notes_${getStudentSuffix()}", updated, NoteItem::class.java)
    }

    // --- Exams Methods ---
    fun addExam(subject: String, days: Int, priority: String, dateStr: String) {
        val updated = _exams.value + ExamItem(
            id = System.currentTimeMillis().toString(),
            subject = subject,
            daysRemaining = days,
            priority = priority,
            dateString = dateStr
        )
        _exams.value = updated
        saveList("exams_${getStudentSuffix()}", updated, ExamItem::class.java)
    }

    fun removeExam(id: String) {
        val updated = _exams.value.filter { it.id != id }
        _exams.value = updated
        saveList("exams_${getStudentSuffix()}", updated, ExamItem::class.java)
    }

    // --- CGPA Methods ---
    fun addSemester(name: String, gpa: Float, credits: Int) {
        val updated = _semesters.value + SemesterGpa(
            id = System.currentTimeMillis().toString(),
            name = name,
            gpa = gpa,
            credits = credits
        )
        _semesters.value = updated
        saveList("semesters_${getStudentSuffix()}", updated, SemesterGpa::class.java)
    }

    fun removeSemester(id: String) {
        val updated = _semesters.value.filter { it.id != id }
        _semesters.value = updated
        saveList("semesters_${getStudentSuffix()}", updated, SemesterGpa::class.java)
    }

    // --- Focus Methods ---
    fun registerFocusedMinutes(mins: Int) {
        _focusedMinutesTotal.value += mins
        prefs.edit().putInt("focused_mins", _focusedMinutesTotal.value).apply()
        // Bonus study streak increment after session
        if (mins >= 25) {
            _studyStreak.value += 1
            prefs.edit().putInt("study_streak", _studyStreak.value).apply()
        }
    }

    // --- AI Methods (AEGIS) ---
    fun sendMessageToAegis(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(id = System.currentTimeMillis().toString(), role = "user", text = text)
        _chatHistory.value = _chatHistory.value + userMsg

        _isAegisTyping.value = true
        viewModelScope.launch {
            val key = BuildConfig.GEMINI_API_KEY ?: ""
            // Format history for context
            val apiHistory = _chatHistory.value.dropLast(1).map {
                com.example.api.Content(
                    parts = listOf(com.example.api.Part(text = it.text))
                )
            }
            val reply = AegisBrain.askAegis(text, key, apiHistory)
            val aegisMsg = ChatMessage(id = (System.currentTimeMillis() + 1).toString(), role = "aegis", text = reply)
            _chatHistory.value = _chatHistory.value + aegisMsg
            _isAegisTyping.value = false
            saveChatHistory(_chatHistory.value)
        }
    }

    fun clearChat() {
        _chatHistory.value = listOf(
            ChatMessage(
                id = "welcome",
                role = "aegis",
                text = "System Online. Welcome, Scholar. I am AEGIS, your direct cognitive operational manager. Input study doubts, query schedules, or command a study analysis. Ready."
            )
        )
        saveChatHistory(_chatHistory.value)
    }

    // --- Community Methods ---
    fun addPost(content: String, tag: String) {
        if (content.isBlank()) return
        val authorName = _currentStudent.value?.name ?: "You Scholar"
        val authorLvl = _currentStudent.value?.level ?: 24
        val updated = listOf(
            CommunityPost(
                id = System.currentTimeMillis().toString(),
                author = authorName,
                authorTitle = "Lvl $authorLvl Scholar",
                content = content,
                likes = 0,
                tag = tag.uppercase(),
                commentsCount = 0
            )
        ) + _communityPosts.value
        _communityPosts.value = updated
        saveList("community_posts", updated, CommunityPost::class.java)
    }

    fun toggleLikePost(id: String) {
        val updated = _communityPosts.value.map {
            if (it.id == id) {
                if (it.likedByUser) {
                    it.copy(likes = it.likes - 1, likedByUser = false)
                } else {
                    it.copy(likes = it.likes + 1, likedByUser = true)
                }
            } else it
        }
        _communityPosts.value = updated
        saveList("community_posts", updated, CommunityPost::class.java)
    }

    // --- Student Lifecycle & Switcher ---
    fun selectStudent(student: StudentProfile) {
        _currentStudent.value = student
        prefs.edit().putString("active_student_id", student.id).apply()
        loadStudentSpecificData(student.id)
    }

    fun registerAndSelectStudent(name: String, registrationNo: String, session: String, department: String) {
        val newId = "student_${System.currentTimeMillis()}"
        val newProfile = StudentProfile(
            id = newId,
            name = name,
            email = session, // Mapping academical session to email field
            rollNo = registrationNo, // Mapping registration number to rollNo field
            department = department
        )
        val updated = _allStudents.value + newProfile
        _allStudents.value = updated
        saveList("all_students", updated, StudentProfile::class.java)
        selectStudent(newProfile)
    }

    fun logoutStudent() {
        _currentStudent.value = null
        prefs.edit().remove("active_student_id").apply()
        // Reset lists to empty to protect and personalize screen state
        _classes.value = emptyList()
        _attendance.value = emptyList()
        _notes.value = emptyList()
        _exams.value = emptyList()
        _semesters.value = emptyList()
    }

    private fun getStudentSuffix(): String {
        return _currentStudent.value?.id ?: "global"
    }

    fun loadStudentSpecificData(studentId: String) {
        _classes.value = loadList("routine_classes_$studentId", ClassItem::class.java).ifEmpty { getInitialClasses() }
        _attendance.value = loadList("attendance_$studentId", AttendanceSubject::class.java).ifEmpty { getInitialAttendance() }
        _notes.value = loadList("notes_$studentId", NoteItem::class.java).ifEmpty { getInitialNotes() }
        _exams.value = loadList("exams_$studentId", ExamItem::class.java).ifEmpty { getInitialExams() }
        _semesters.value = loadList("semesters_$studentId", SemesterGpa::class.java).ifEmpty { getInitialSemesters() }
    }

    // --- Storage Helpers ---
    private fun loadData() {
        _onboardingCompleted.value = prefs.getBoolean("onboarding_completed", false)
        _focusedMinutesTotal.value = prefs.getInt("focused_mins", 0)
        _studyStreak.value = prefs.getInt("study_streak", 14)

        // Load all students list. Initialize with defaults if empty.
        var students = loadList("all_students", StudentProfile::class.java)
        if (students.isEmpty()) {
            students = listOf(
                StudentProfile("student_rohit", "Rohit Sarkar", "rohitsarkarwork03@gmail.com", "RGU-2026-03", "Computer Science", 25),
                StudentProfile("student_priya", "Priya Das", "priya.das@rgu.edu", "RGU-2026-19", "Automated Engineering", 22),
                StudentProfile("student_aman", "Aman Sharma", "aman.sharma@rgu.edu", "RGU-2026-44", "Cybernetic Physics", 28)
            )
            saveList("all_students", students, StudentProfile::class.java)
        }
        _allStudents.value = students

        val activeId = prefs.getString("active_student_id", null)
        val activeStudent = students.find { it.id == activeId }
        _currentStudent.value = activeStudent

        val suffix = activeStudent?.id ?: "global"
        _classes.value = loadList("routine_classes_$suffix", ClassItem::class.java).ifEmpty { getInitialClasses() }
        _attendance.value = loadList("attendance_$suffix", AttendanceSubject::class.java).ifEmpty { getInitialAttendance() }
        _notes.value = loadList("notes_$suffix", NoteItem::class.java).ifEmpty { getInitialNotes() }
        _exams.value = loadList("exams_$suffix", ExamItem::class.java).ifEmpty { getInitialExams() }
        _semesters.value = loadList("semesters_$suffix", SemesterGpa::class.java).ifEmpty { getInitialSemesters() }

        _communityPosts.value = loadList("community_posts", CommunityPost::class.java).ifEmpty { getInitialPosts() }

        // Load chat history
        _chatHistory.value = loadChatHistory().ifEmpty {
            listOf(
                ChatMessage(
                    id = "welcome",
                    role = "aegis",
                    text = "System Online. Welcome, Scholar. I am AEGIS, your direct cognitive operational manager. Input study doubts, query schedules, or command a study analysis. Ready."
                )
            )
        }
    }

    private fun <T> saveList(key: String, list: List<T>, clazz: Class<T>) {
        try {
            val type = Types.newParameterizedType(List::class.java, clazz)
            val adapter = moshi.adapter<List<T>>(type)
            val json = adapter.toJson(list)
            prefs.edit().putString(key, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun <T> loadList(key: String, clazz: Class<T>): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            val type = Types.newParameterizedType(List::class.java, clazz)
            val adapter = moshi.adapter<List<T>>(type)
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveChatHistory(history: List<ChatMessage>) {
        saveList("chat_history_v2", history, ChatMessage::class.java)
    }

    private fun loadChatHistory(): List<ChatMessage> {
        return loadList("chat_history_v2", ChatMessage::class.java)
    }

    // --- Mock Generators (To populated first-run experience visually) ---
    private fun getInitialClasses() = listOf(
        ClassItem("1", "Advanced AI Ethics", "Mon", "10:15 - 11:45", "Room 402", "Prof. K. Aris", "#3B82F6"),
        ClassItem("2", "Quantum Computing Fundamentals", "Mon", "13:00 - 14:30", "Lab S2", "Dr. Mira Sen", "#8B5CF6"),
        ClassItem("3", "Cybernetic Security Systems", "Tue", "10:15 - 11:45", "Room 101", "Prof. K. Aris", "#06B6D4"),
        ClassItem("4", "Data Networks Infrastructure", "Wed", "08:30 - 10:00", "Room 305", "Dr. Roy", "#3B82F6")
    )

    private fun getInitialAttendance() = listOf(
        AttendanceSubject("1", "AI Ethics", 14, 16),
        AttendanceSubject("2", "Quantum Comp", 11, 13),
        AttendanceSubject("3", "Cyber Security", 8, 12), // Percentage 66.6% -> Shows classes needed to reach 75%
        AttendanceSubject("4", "Data Networks", 12, 12)
    )

    private fun getInitialNotes() = listOf(
        NoteItem("1", "Quantum Tunneling Formulas", "Review probability equations for barrier transmission. Note standard kinetic coefficients and wave-function limits.", "Physics", "Mon, Oct 24"),
        NoteItem("2", "Ethical AI Core Axioms", "1. Transparency of data sources\n2. Alignment with human baseline needs\n3. Verification loops in reinforcement pipelines.", "AI Ethics", "Sun, Oct 23"),
        NoteItem("3", "React Native vs Native Rust UI", "Compare memory overhead, canvas access times, and touch gesture speed indexes. Study performance ratios.", "Dev", "Oct 19")
    )

    private fun getInitialExams() = listOf(
        ExamItem("1", "AI Ethics Midterm", 2, "HIGH", "Mon, Oct 26"),
        ExamItem("2", "Quantum Computing Basics", 6, "MEDIUM", "Fri, Oct 30"),
        ExamItem("3", "Infrastructure Practical Lab", 15, "LOW", "Nov 08")
    )

    private fun getInitialSemesters() = listOf(
        SemesterGpa("1", "Semester I", 8.2f, 22),
        SemesterGpa("2", "Semester II", 8.6f, 24),
        SemesterGpa("3", "Semester III", 9.1f, 20)
    )

    private fun getInitialPosts() = listOf(
        CommunityPost("c1", "Aman Sharma", "Lvl 30 Arch-Scholar", "Created a combined Cornell summary deck for Quantum Midterms. Access file in our library! 🔥 #Quantum", 12, "RESEARCH", 4, true),
        CommunityPost("c2", "Priya Das", "Lvl 19 Geek", "Is anyone down for an AEGIS-assisted sprint session in the campus tech library at 4:30 PM?", 7, "STUDY GROUP", 2),
        CommunityPost("c3", "Rohan Gupta", "Lvl 22 Coder", "Optimized my daily routine: Doing the 50/10 Pomodoro block with the Cyber Lounge ambient noise tracks keeps my neural indices fully active.", 23, "TIPS", 8)
    )
}
