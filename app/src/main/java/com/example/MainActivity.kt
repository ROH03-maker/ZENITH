package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContentScreen()
            }
        }
    }
}

@Composable
fun MainContentScreen(viewModel: ZenithViewModel = viewModel()) {
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        CyberpunkBackground()

        AnimatedContent(
            targetState = onboardingCompleted,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "ScreenTransition"
        ) { isCompleted ->
            if (!isCompleted) {
                OnboardingScreen(
                    onGetStarted = { viewModel.completeOnboarding() }
                )
            } else {
                DashboardNavigationLayout(viewModel)
            }
        }
    }
}

// --- Background ---
@Composable
fun CyberpunkBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F1A))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Neon glowing radial grids
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x193B82F6), Color.Transparent),
                    center = Offset(width * 0.1f, height * 0.2f),
                    radius = width * 0.8f
                )
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x158B5CF6), Color.Transparent),
                    center = Offset(width * 0.9f, height * 0.8f),
                    radius = width * 0.8f
                )
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1206B6D4), Color.Transparent),
                    center = Offset(width * 0.5f, height * 0.5f),
                    radius = width * 0.6f
                )
            )

            // Simple aesthetic matrix network grids
            val cols = 8
            val rows = 16
            val stepW = width / cols
            val stepH = height / rows

            for (i in 0..cols) {
                drawLine(
                    color = Color(0x063B82F6),
                    start = Offset(i * stepW, 0f),
                    end = Offset(i * stepW, height),
                    strokeWidth = 1f
                )
            }
            for (j in 0..rows) {
                drawLine(
                    color = Color(0x063B82F6),
                    start = Offset(0f, j * stepH),
                    end = Offset(width, j * stepH),
                    strokeWidth = 1f
                )
            }
        }
    }
}

// --- Onboarding Dashboard ---
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Title / Version details
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x1B3B82F6))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .border(1.dp, Color(0x3B3B82F6), RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = "ACADEMIC OPERATIONAL SYSTEM",
                    color = Color(0xFF06B6D4),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // Geometric holographic visual
        Box(
            modifier = Modifier
                .size(240.dp)
                .drawBehind {
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(
                        color = Color(0x333B82F6),
                        radius = 110.dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0x228B5CF6),
                        radius = 80.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0x1F06B6D4),
                        radius = 50.dp.toPx(),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    // Custom crosshairs
                    drawLine(
                        color = Color(0x443B82F6),
                        start = Offset(center.x - 130.dp.toPx(), center.y),
                        end = Offset(center.x + 130.dp.toPx(), center.y)
                    )
                    drawLine(
                        color = Color(0x443B82F6),
                        start = Offset(center.x, center.y - 130.dp.toPx()),
                        end = Offset(center.x, center.y + 130.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Zenith Logo",
                tint = Color(0xFF8B5CF6),
                modifier = Modifier
                    .size(85.dp)
                    .padding(8.dp)
            )
        }

        // Motivational text messages
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 30.dp)
        ) {
            Text(
                text = "Zenith.",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Academic Life. Reimagined.",
                color = Color(0xFF06B6D4),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "An all-in-one productivity ecosystem designed for modern scholars. Seamless routine sync, automated GPA scaling, and AEGIS AI operational intelligence.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp),
                lineHeight = 18.sp
            )
        }

        // Action controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("get_started_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "GET STARTED",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF8FAFC))
                ) {
                    Text("Guest Mode", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF8FAFC))
                ) {
                    Text("Enterprise", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Main Navigation Layout ---
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardNavigationLayout(viewModel: ZenithViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()

    if (currentStudent == null) {
        StudentLoginOverlayScreen(viewModel)
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                HeaderSection(currentTab, viewModel)
            },
            bottomBar = {
                GlassNavigationBar(
                    activeTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { if (targetState == "Hub") -it else it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeIn() togetherWith slideOutHorizontally(
                            targetOffsetX = { if (targetState == "Hub") it else -it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeOut()
                    },
                    label = "TabContent"
                ) { tab ->
                    when (tab) {
                        "Hub" -> HubScreen(viewModel)
                        "Routine" -> RoutineScreen(viewModel)
                        "Attendance" -> AttendanceScreen(viewModel)
                        "Notes" -> NotesScreen(viewModel)
                        "Focus" -> FocusScreen(viewModel)
                        "CGPA" -> CgpaScreen(viewModel)
                        "AI" -> AiAssistantScreen(viewModel)
                        "Social" -> SocialScreen(viewModel)
                        else -> HubScreen(viewModel)
                    }
                }
            }
        }
    }
}

// --- Header ---
@Composable
fun HeaderSection(currentTab: String, viewModel: ZenithViewModel) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    var showProfileSwitcher by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "LEVEL ${currentStudent?.level ?: 24} SCHOLAR",
                color = Color(0xFF06B6D4),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = currentStudent?.name ?: "Zenith.",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
            if (currentStudent != null) {
                Text(
                    text = "${currentStudent?.department} • ${currentStudent?.rollNo}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile switcher trigger button
            IconButton(
                onClick = { showProfileSwitcher = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x1F8B5CF6))
                    .border(1.dp, Color(0xFF8B5CF6), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Switch Student Profile",
                    tint = Color(0xFFC084FC)
                )
            }
        }
    }

    if (showProfileSwitcher) {
        StudentSwitchDialog(
            viewModel = viewModel,
            onDismiss = { showProfileSwitcher = false }
        )
    }
}

// --- Navigation Item ---
data class NavigationTabDef(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun GlassNavigationBar(activeTab: String, onTabSelected: (String) -> Unit) {
    val tabs = listOf(
        NavigationTabDef("Hub", Icons.Default.Home),
        NavigationTabDef("Routine", Icons.Default.List),
        NavigationTabDef("Attendance", Icons.Default.CheckCircle),
        NavigationTabDef("Notes", Icons.Default.Create),
        NavigationTabDef("Focus", Icons.Default.Notifications),
        NavigationTabDef("CGPA", Icons.Default.Build),
        NavigationTabDef("AI", Icons.Default.Star),
        NavigationTabDef("Social", Icons.Default.Share)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xE80B0F1A),
        border = BorderStroke(1.dp, Color(0x1F3B82F6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = activeTab == tab.label
                val activeColor = Color(0xFF3B82F6)
                val inactiveColor = Color(0xFF94A3B8).copy(alpha = 0.7f)

                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab.label) }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("tab_${tab.label.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) activeColor else inactiveColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        color = if (selected) activeColor else inactiveColor,
                        fontSize = 8.5.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ===================== MODULE 2: HOME HUB =====================
@Composable
fun HubScreen(viewModel: ZenithViewModel) {
    val streak by viewModel.studyStreak.collectAsState()
    val focusedMins by viewModel.focusedMinutesTotal.collectAsState()
    val attendanceList by viewModel.attendance.collectAsState()
    val routineClasses by viewModel.classes.collectAsState()
    val examsList by viewModel.exams.collectAsState()

    val totalAttendancePercentage = remember(attendanceList) {
        val totalClasses = attendanceList.sumOf { it.total }
        val totalAttended = attendanceList.sumOf { it.attended }
        if (totalClasses == 0) 88.4f else (totalAttended.toFloat() / totalClasses * 100)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Progress productivity score card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("productivity_score_card"),
                borderGlow = Color(0xFF3B82F6)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = "PRODUCTIVITY SCORE",
                            fontSize = 11.sp,
                            color = Color(0xFF06B6D4),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "92 / 100",
                            fontSize = 32.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Excellent. You remain in top 5% student bracket. Midterm preparedness metrics fully high.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    // Progress ring
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color(0x1F3B82F6),
                                radius = size.width / 2,
                                style = Stroke(width = 5.dp.toPx())
                            )
                            drawArc(
                                brush = Brush.sweepGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))),
                                startAngle = -90f,
                                sweepAngle = 360f * 0.92f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }
                        Text(
                            text = "92%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Attendance & Streak Grid Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Streak Card
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("streak_card"),
                    borderGlow = Color(0xFFEA580C)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STREAK",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$streak Days",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ELITE SCHOLAR TIER",
                            color = Color(0xFFF97316),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Attendance Overall Card
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("overall_attendance_card"),
                    borderGlow = Color(0xFF10B981)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ATTENDANCE",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = String.format("%.1f%%", totalAttendancePercentage),
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { totalAttendancePercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0x3310B981)
                        )
                    }
                }
            }
        }

        // Real-Time Timeline Widget
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timeline_hub_widget")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "TIMELINE: CURRENT CYCLE",
                        fontSize = 11.sp,
                        color = Color(0xFF8B5CF6),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (routineClasses.isEmpty()) {
                        Text(
                            text = "No classroom routines logged.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        routineClasses.take(3).forEach { cls ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp, 40.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFF3B82F6))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = cls.subject,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${cls.dayOfWeek} • ${cls.time} • ${cls.room}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                Text(
                                    text = "NEXT",
                                    fontSize = 10.sp,
                                    color = Color(0xFF06B6D4),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Countdown Alert list (high priorities)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "URGENT EXAM COUNTDOWNS",
                        fontSize = 11.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (examsList.isEmpty()) {
                        Text(
                            text = "Operational horizon clean. No pending high tests.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    } else {
                        examsList.forEach { exam ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (exam.priority == "HIGH") Color(0xFFEF4444)
                                                else if (exam.priority == "MEDIUM") Color(0xFFF97316)
                                                else Color(0xFF3B82F6)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = exam.subject,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${exam.daysRemaining} days left",
                                        fontWeight = FontWeight.Bold,
                                        color = if (exam.priority == "HIGH") Color(0xFFEF4444) else Color.White,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Direct AEGIS Suggestion Widget
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1F06B6D4))
                    .border(1.dp, Color(0x3306B6D4), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF06B6D4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "\"Intelligence core advises standard 45m Pomodoro cycles at 4 PM to prep for Midterm.\" -- AEGIS System AI",
                    color = Color(0xFFF8FAFC),
                    fontSize = 11.5.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// ===================== MODULE 3: ROUTINE TIMETABLE =====================
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RoutineScreen(viewModel: ZenithViewModel) {
    val classes by viewModel.classes.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()
    val studentDeptStr = currentStudent?.department ?: "General"

    var showAddDialog by remember { mutableStateOf(false) }

    var subject by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableStateOf("Mon") }
    var timeRange by remember { mutableStateOf("10:15 - 11:45") }
    var room by remember { mutableStateOf("Room 402") }
    var teacher by remember { mutableStateOf("Prof. K. Aris") }
    var colorHex by remember { mutableStateOf("#3B82F6") }

    val daysOptions = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var filterDaySelected by remember { mutableStateOf("All") }

    val filteredClasses = classes.filter {
        filterDaySelected == "All" || it.dayOfWeek.equals(filterDaySelected, ignoreCase = true)
    }.sortedWith(compareBy({ daysOptions.indexOf(it.dayOfWeek) }, { it.time }))

    // Active suggestions based on department
    val listSuggestedSubjects = remember(studentDeptStr) {
        if (studentDeptStr.contains("CSE", ignoreCase = true) || studentDeptStr.contains("Computer", ignoreCase = true)) {
            listOf("Data Structures", "Algorithms", "Computer Networks", "Database Systems", "AI Ethics")
        } else if (studentDeptStr.contains("EEE", ignoreCase = true) || studentDeptStr.contains("Electrical", ignoreCase = true)) {
            listOf("Circuit Theory", "Signal Processing", "Digital Electronics", "Power Systems", "Control Systems")
        } else {
            listOf("Mathematics II", "Physics II", "Chemistry", "Technical English", "Humanities")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WEEKLY SCHEDULE MATRIX",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Class", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Slot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Matrix Day filter tabs
        Text("MATRIX VIEW TRACKS:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (filterDaySelected == "All") Color(0xFF3B82F6) else Color(0x1AFFFFFF))
                        .clickable { filterDaySelected = "All" }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("All Days", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            items(daysOptions.size) { index ->
                val d = daysOptions[index]
                val isActive = filterDaySelected == d
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) Color(0xFF3B82F6) else Color(0x1AFFFFFF))
                        .clickable { filterDaySelected = d }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(d, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredClasses.isEmpty()) {
            Box(modifier = Modifier.weight(1.0f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (classes.isEmpty()) "No classes configured. Access 'Log Slot' to build schedule." else "No classes scheduled for $filterDaySelected.",
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.weight(1.0f)
            ) {
                items(filteredClasses.size) { index ->
                    val cls = filteredClasses[index]
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp, 44.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when (cls.colorHex) {
                                                "#8B5CF6" -> Color(0xFF8B5CF6)
                                                "#06B6D4" -> Color(0xFF06B6D4)
                                                else -> Color(0xFF3B82F6)
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0x1F3B82F6))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = cls.dayOfWeek.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF06B6D4)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cls.time,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cls.subject,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${cls.room} • ${cls.teacher}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.removeClass(cls.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Operational Routine", color = Color.White) },
            containerColor = Color(0xFF0B0F1A),
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Department Preset Chips to fill the Subject
                    Text("Select Suggested Subject:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listSuggestedSubjects.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (subject == chip) Color(0xFF3B82F6) else Color(0x1F3B82F6))
                                    .clickable { subject = chip }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(chip, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Class / Subject Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            focusedLabelColor = Color(0xFF3B82F6),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            unfocusedBorderColor = Color(0x22FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Text("Day of Week Selection:", color = Color.White, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(daysOptions) { day ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (dayOfWeek == day) Color(0xFF3B82F6) else Color(0x22FFFFFF))
                                    .clickable { dayOfWeek = day }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = timeRange,
                        onValueChange = { timeRange = it },
                        label = { Text("Time Grid (e.g. 10:15 - 11:45)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )

                    // Time presets row
                    Text("Quick Class Times:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val times = listOf("09:00 - 10:30", "10:45 - 12:15", "13:30 - 15:00", "15:15 - 16:45")
                        items(times.size) { index ->
                            val t = times[index]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (timeRange == t) Color(0x223B82F6) else Color(0x11FFFFFF))
                                    .clickable { timeRange = t }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(t, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Auditorium / Lab Slot") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )

                    // Place Presets
                    Text("Popular Areas:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val places = listOf("Room 402", "MAC Lab A", "Micro Lab", "Auditorium 2", "Sem Room 1")
                        items(places.size) { index ->
                            val p = places[index]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (room == p) Color(0x223B82F6) else Color(0x11FFFFFF))
                                    .clickable { room = p }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(p, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = teacher,
                        onValueChange = { teacher = it },
                        label = { Text("Teacher Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )

                    Text("Accent Theme Tag:", color = Color.White, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("#3B82F6", "#8B5CF6", "#06B6D4").forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (col) {
                                            "#8B5CF6" -> Color(0xFF8B5CF6)
                                            "#06B6D4" -> Color(0xFF06B6D4)
                                            else -> Color(0xFF3B82F6)
                                        }
                                    )
                                    .border(
                                        2.dp,
                                        if (colorHex == col) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { colorHex = col }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank()) {
                            viewModel.addClass(
                                ClassItem(
                                    id = System.currentTimeMillis().toString(),
                                    subject = subject,
                                    dayOfWeek = dayOfWeek,
                                    time = timeRange,
                                    room = room,
                                    teacher = teacher,
                                    colorHex = colorHex
                                )
                            )
                            showAddDialog = false
                            subject = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

// ===================== MODULE 4: ATTENDANCE TRACKER =====================
@Composable
fun AttendanceScreen(viewModel: ZenithViewModel) {
    val attendanceList by viewModel.attendance.collectAsState()
    val classesList by viewModel.classes.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()
    val studentDept = currentStudent?.department ?: "General"

    var newSubjectName by remember { mutableStateOf("") }
    
    // Auto-detect recommended subjects based on current student's department
    val suggestedSubjects = remember(studentDept) {
        if (studentDept.contains("CSE", ignoreCase = true) || studentDept.contains("Computer", ignoreCase = true)) {
            listOf("Data Structures", "Algorithms", "Operating Systems", "Computer Networks", "Database Management")
        } else if (studentDept.contains("EEE", ignoreCase = true) || studentDept.contains("Electrical", ignoreCase = true)) {
            listOf("Circuit Theory", "Signal Processing", "Digital Electronics", "Power Systems", "Control Systems")
        } else {
            listOf("Mathematics II", "Physics Lab", "Chemistry", "Technical Writing", "Project Management")
        }
    }

    // Capture distinct subjects from the weekly routine matrix
    val routineClassSubjects = remember(classesList) {
        classesList.map { it.subject }.distinct().filter { it.isNotBlank() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "ATTENDANCE METRIC TRACKER",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Head quick register input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newSubjectName,
                onValueChange = { newSubjectName = it },
                label = { Text("Log New Subject / Choose Class", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1.5f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10B981),
                    focusedLabelColor = Color(0xFF10B981),
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Button(
                onClick = {
                    if (newSubjectName.isNotBlank()) {
                        viewModel.addSubject(newSubjectName.trim())
                        newSubjectName = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Log", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Subject Recommendations & Weekly Routine Importer
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (routineClassSubjects.isNotEmpty()) {
                Button(
                    onClick = {
                        routineClassSubjects.forEach { routineSubj ->
                            val exists = attendanceList.any { it.name.trim().lowercase() == routineSubj.trim().lowercase() }
                            if (!exists) {
                                viewModel.addSubject(routineSubj)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1110B981)),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📥 IMPORT ALL ${routineClassSubjects.size} SUBJECTS FROM ROUTINE", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Quick ADD Chips
            Text("QUICK SUGGESTIONS FOR ${studentDept.uppercase()}:", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestedSubjects.size) { index ->
                    val recommended = suggestedSubjects[index]
                    val isAlreadyLogged = attendanceList.any { it.name.trim().lowercase() == recommended.trim().lowercase() }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAlreadyLogged) Color(0x2210B981) else Color(0x11FFFFFF))
                            .border(1.dp, if (isAlreadyLogged) Color(0xFF10B981) else Color(0x12FFFFFF), RoundedCornerShape(8.dp))
                            .clickable {
                                if (!isAlreadyLogged) {
                                    viewModel.addSubject(recommended)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isAlreadyLogged) "✓ $recommended" else "+ $recommended",
                            color = if (isAlreadyLogged) Color(0xFF10B981) else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (attendanceList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No subjects tracked yet. Create one above.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(attendanceList) { subject ->
                    val isBelowStandard = subject.percentage < 75f

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderGlow = if (isBelowStandard) Color(0xFFEF4444) else Color(0xFF10B981)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = subject.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${subject.attended} attended / ${subject.total} total classes",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                // Interactive Adjustments Row
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateAttendance(subject.id, -1, -1) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x1F94A3B8))
                                    ) {
                                        Text("-", color = Color.White, fontWeight = FontWeight.Black)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { viewModel.updateAttendance(subject.id, 1, 1) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    ) {
                                        Text("+", color = Color.White, fontWeight = FontWeight.Black)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { viewModel.updateAttendance(subject.id, 0, 1) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x1FFF4444))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Missed class",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel.removeSubject(subject.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = Color(0x8894A3B8)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Indicator bar
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    progress = { subject.percentage / 100f },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isBelowStandard) Color(0xFFEF4444) else Color(0xFF10B981),
                                    trackColor = Color(0x22FFFFFF)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = String.format("%.0f%%", subject.percentage),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isBelowStandard) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Optimization recommendation tag
                            if (isBelowStandard) {
                                val needed = subject.classesNeededFor75
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1FFF4444))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Below standard. Must attend next $needed classes consecutively to reach 75%.",
                                        color = Color(0xFFEF4444),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1910B981))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Optimized. Standard rating of 75% or higher maintained.",
                                        color = Color(0xFF10B981),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===================== MODULE 5: NOTES SECTION =====================
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun NotesScreen(viewModel: ZenithViewModel) {
    val notesList by viewModel.notes.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()
    val studentDeptStr = currentStudent?.department ?: "General"

    var searchToken by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var decryptionPdfItem by remember { mutableStateOf<NoteItem?>(null) }

    // Dynamic subject folder filtering options computed on the fly
    val dynamicFolderList = remember(notesList, studentDeptStr) {
        val defaultSuggest = if (studentDeptStr.contains("CSE", ignoreCase = true) || studentDeptStr.contains("Computer", ignoreCase = true)) {
            listOf("All", "Data Structures", "Algorithms", "AI Ethics", "Networks")
        } else if (studentDeptStr.contains("EEE", ignoreCase = true) || studentDeptStr.contains("Electrical", ignoreCase = true)) {
            listOf("All", "Circuits", "Signals", "Power Grid", "Sensors")
        } else {
            listOf("All", "Lectures", "Lab Drafts", "Syllabus", "Exam Study")
        }
        val customFolders = notesList.map { it.folder }.distinct()
        (defaultSuggest + customFolders).distinct()
    }

    var selectedFolder by remember { mutableStateOf("All") }

    val filteredNotes = notesList.filter {
        (selectedFolder == "All" || it.folder == selectedFolder) &&
                (it.title.contains(searchToken, ignoreCase = true) || 
                 it.content.contains(searchToken, ignoreCase = true) ||
                 (it.pdfName?.contains(searchToken, ignoreCase = true) ?: false))
    }

    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newFolder by remember { mutableStateOf("") }
    var newPdfName by remember { mutableStateOf("") }
    var newPdfUrl by remember { mutableStateOf("") }

    LaunchedEffect(showCreateDialog) {
        if (showCreateDialog && newFolder.isBlank()) {
            newFolder = if (studentDeptStr.isNotBlank() && studentDeptStr != "General") studentDeptStr else "Lectures"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STUDY MODULE COGNITIVE INDEX",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Code Note", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchToken,
            onValueChange = { searchToken = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search title, axioms, or PDF name...", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B5CF6),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedBorderColor = Color(0x22FFFFFF)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subject Filtering Chips (Dynamic based on department & entered folders)
        Text("FILTER SUBJECT AREAS:", fontSize = 9.sp, color = Color(0xFF94A3B8), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dynamicFolderList.size) { index ->
                val f = dynamicFolderList[index]
                val active = selectedFolder == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) Color(0xFF8B5CF6) else Color(0x1AFFFFFF))
                        .border(1.dp, if (active) Color(0xFF8B5CF6) else Color(0x17FFFFFF), RoundedCornerShape(10.dp))
                        .clickable { selectedFolder = f }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = f, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching items found in study database.", color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredNotes.size) { index ->
                    val note = filteredNotes[index]
                    var isExpanded by remember { mutableStateOf(false) }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        borderGlow = Color(0xFF8B5CF6)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.0f)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0x1A8B5CF6))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = note.folder.uppercase(),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC084FC)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = note.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.dateString, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(onClick = { viewModel.removeNote(note.id) }, modifier = Modifier.size(28.dp)) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444).copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = note.content,
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                maxLines = if (isExpanded) 100 else 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )

                            // Render PDF document reference if present!
                            if (!note.pdfName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1A06B6D4))
                                        .border(1.dp, Color(0x3306B6D4), RoundedCornerShape(8.dp))
                                        .clickable { decryptionPdfItem = note }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                  ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Share, 
                                            contentDescription = "PDF Document", 
                                            tint = Color(0xFF06B6D4), 
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = note.pdfName!!,
                                            color = Color(0xFFF1F5F9),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0x3306B6D4))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("DECRYPT PDF", color = Color(0xFF06B6D4), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tap to collapse study details.",
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color(0xFF06B6D4)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to add a dynamic notes structure with an attachment
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Log Study Note / Code Block", color = Color.White, fontWeight = FontWeight.Bold) },
            containerColor = Color(0xFF0B0F1A),
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Topic / Axiom Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6)
                        )
                    )

                    OutlinedTextField(
                        value = newFolder,
                        onValueChange = { newFolder = it },
                        label = { Text("Subject Area / Folder (e.g. CSE, EEE, Math)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6)
                        )
                    )

                    // Prefill Chips for Subjects
                    Text("Popular Subjects:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val suggests = listOf("Data Structures", "Algorithms", "AI Ethics", "Electronics", "Syllabus").distinct()
                        suggests.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (newFolder == chip) Color(0xFF8B5CF6) else Color(0x1F8B5CF6))
                                    .clickable { newFolder = chip }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(chip, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Study Notes Details / Code snippet / Core Formula") },
                        modifier = Modifier.height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8B5CF6)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("PDF / STUDY DOCUMENT ATTACHMENT:", color = Color(0xFF06B6D4), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = newPdfName,
                        onValueChange = { newPdfName = it },
                        placeholder = { Text("File Name (e.g. Lecture_2_Networks.pdf)") },
                        label = { Text("PDF Name Reference") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF06B6D4)
                        )
                    )

                    OutlinedTextField(
                        value = newPdfUrl,
                        onValueChange = { newPdfUrl = it },
                        placeholder = { Text("Connected Web URL or drive node link") },
                        label = { Text("PDF Resource Link URL") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF06B6D4)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addNote(
                                title = newTitle.trim(),
                                content = newContent.trim(),
                                folder = if (newFolder.isNotBlank()) newFolder.trim() else "Syllabus",
                                pdfName = if (newPdfName.isNotBlank()) newPdfName.trim() else null,
                                pdfUrl = if (newPdfUrl.isNotBlank()) newPdfUrl.trim() else null
                            )
                            showCreateDialog = false
                            newTitle = ""
                            newContent = ""
                            newPdfName = ""
                            newPdfUrl = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Text("Log Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // Decrypt details of student pdf secure attachment
    if (decryptionPdfItem != null) {
        val pdf = decryptionPdfItem!!
        AlertDialog(
            onDismissRequest = { decryptionPdfItem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF06B6D4))
                    Text("ZENITH DECRYPT TERMINAL v1.40", color = Color.White, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                }
            },
            containerColor = Color(0xFF070B14),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("PDF NODE DECRYPTION COMPLETED SUCCESSFULLY.", color = Color(0xFF10B981), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x11FFFFFF))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("ATTACHED SYSTEM PATH:", color = Color(0xFF94A3B8), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(pdf.pdfName ?: "N/A", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("SECURE DIRECT INTERNET NODE:", color = Color(0xFF94A3B8), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(pdf.pdfUrl ?: "Not provided. Physical campus file reference.", color = Color(0xFF06B6D4), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Text("This document belongs to department: ${pdf.folder.uppercase()}. Authorized scholar node login profile verified.", color = Color(0xFF94A3B8), fontSize = 10.sp, lineHeight = 14.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { decryptionPdfItem = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
                ) {
                    Text("Dismiss Terminal", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ===================== MODULE 7: POMODORO FOCUS MODE =====================
@Composable
fun FocusScreen(viewModel: ZenithViewModel) {
    var timerSecondsTotal by remember { mutableStateOf(25 * 60) }
    var timerSecondsRemaining by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var selectedSoundIndex by remember { mutableStateOf(1) }
    var musicPlaybackSecs by remember { mutableStateOf(0) }

    val focusModes = listOf(25, 50)
    val environmentSounds = listOf(
        "Off", 
        "Midnight Synth Lofi", 
        "Chopin Nocturne (Ambient Piano)", 
        "Cyberpunk Deep Focus", 
        "Rainy Campus Café Jazz", 
        "Binaural Alpha Wave Focus",
        "Celestial Space Drone"
    )

    // Animated soundtrack wave simulated
    val waveHeights = remember { mutableStateListOf(10.dp, 20.dp, 15.dp, 5.dp, 18.dp, 12.dp, 8.dp) }

    LaunchedEffect(isRunning, timerSecondsRemaining) {
        if (isRunning && timerSecondsRemaining > 0) {
            delay(1000L)
            timerSecondsRemaining -= 1
            if (selectedSoundIndex > 0) {
                musicPlaybackSecs = (musicPlaybackSecs + 1) % 220 // loop back after 3m40s
            }
            // Randomize visual sound waves slightly
            for (i in 0 until waveHeights.size) {
                waveHeights[i] = (5 + (Math.random() * 25).toInt()).dp
            }
        } else if (isRunning && timerSecondsRemaining == 0) {
            isRunning = false
            viewModel.registerFocusedMinutes(timerSecondsTotal / 60)
            timerSecondsRemaining = timerSecondsTotal
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "COGNITIVE STRENGTH SPRINT ENGINE",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Pulse timer ring
        Box(
            modifier = Modifier
                .size(220.dp)
                .drawBehind {
                    val progress = timerSecondsRemaining.toFloat() / timerSecondsTotal
                    drawCircle(
                        color = Color(0x1F3B82F6),
                        radius = size.width / 2,
                        style = Stroke(width = 10.dp.toPx())
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val m = timerSecondsRemaining / 60
                val s = timerSecondsRemaining % 60
                Text(
                    text = String.format("%02d:%02d", m, s),
                    fontSize = 42.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRunning) "ACTIVELY TUNED..." else "READY SPLIT",
                    color = Color(0xFF06B6D4),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cyberpunk interactive sound waves and playback position
        if (selectedSoundIndex > 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.height(70.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    waveHeights.forEach { h ->
                        val activeHeight = if (isRunning) h else 6.dp
                        Box(
                            modifier = Modifier
                                .size(6.dp, activeHeight)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF06B6D4))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                val trackMinutes = musicPlaybackSecs / 60
                val trackSeconds = musicPlaybackSecs % 60
                Text(
                    text = "Tuning: ${environmentSounds[selectedSoundIndex]} (${String.format("%02d:%02d", trackMinutes, trackSeconds)} / 03:40)",
                    color = Color(0xFF06B6D4),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Box(
                modifier = Modifier.height(70.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Cognitive soundscape suspended. Choose an audio focus track.",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Focus mode selector button groups
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            focusModes.forEach { mins ->
                val active = timerSecondsTotal / 60 == mins
                OutlinedButton(
                    onClick = {
                        if (!isRunning) {
                            timerSecondsTotal = mins * 60
                            timerSecondsRemaining = mins * 60
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (active) Color(0xFF3B82F6) else Color(0x33FFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (active) Color(0x193B82F6) else Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text("$mins MIN FOCUS")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selectable ambient focus songs
        Text("AI Cognitive Focus Soundtracks:", color = Color(0xFF94A3B8), fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(environmentSounds.size) { index ->
                val title = environmentSounds[index]
                val selected = selectedSoundIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) Color(0x1F06B6D4) else Color(0x11FFFFFF))
                        .border(1.dp, if (selected) Color(0xFF06B6D4) else Color(0x15FFFFFF), RoundedCornerShape(8.dp))
                        .clickable { selectedSoundIndex = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (index > 0) {
                            Icon(
                                imageVector = if (selected && isRunning) Icons.Default.PlayArrow else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (selected) Color(0xFF06B6D4) else Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action controls
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFEA580C) else Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isRunning) "PAUSE FOCUS" else "EXECUTE SPRINT", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                    timerSecondsRemaining = timerSecondsTotal
                    musicPlaybackSecs = 0
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("RESET")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Motivational Quote ticker
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "\"Your capability index expands whenever you stay focused for full 25m consecutive cognitive cycles. Keep pushing.\" -- AEGIS Advisor Core",
                color = Color(0xFFF8FAFC),
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 15.sp,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

// ===================== MODULE 8: CGPA CALCULATOR =====================
@Composable
fun CgpaScreen(viewModel: ZenithViewModel) {
    val semesters by viewModel.semesters.collectAsState()
    var showAddGpaDialog by remember { mutableStateOf(false) }

    var inputSemesterTitle by remember { mutableStateOf("") }
    var inputGpaStr by remember { mutableStateOf("") }
    var inputCreditsStr by remember { mutableStateOf("") }

    // Target projection variables
    var targetCgpaGoal by remember { mutableFloatStateOf(8.5f) }

    val aggregateCgpa = remember(semesters) {
        val totalCredits = semesters.sumOf { it.credits }
        val weightedSum = semesters.sumOf { (it.gpa * it.credits).toDouble() }
        if (totalCredits == 0) 0f else (weightedSum / totalCredits).toFloat()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GRADE SCALE PREDICTION PANEL",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = { showAddGpaDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Log GPA", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Linear Graph Visualization
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("HISTORIC CGPA SCALING PROGRESS", color = Color(0xFF06B6D4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                SemesterGpaGraph(semesters)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Aggregate CGPA rating box
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("CURRENT GPA INDEX", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.2f", aggregateCgpa),
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text("Out of 10.00 max scale", color = Color(0xFF06B6D4), fontSize = 9.sp)
                }
            }

            GlassCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("TOTAL CREDITS LOCKED", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = semesters.sumOf { it.credits }.toString(),
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text("Credits calculated in average", color = Color(0xFF8B5CF6), fontSize = 9.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Optimizer Targets
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("OPTIMIZER TARGET PROJECTION", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Desired CGPA Goal: ${String.format("%.2f", targetCgpaGoal)}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }

                Slider(
                    value = targetCgpaGoal,
                    onValueChange = { targetCgpaGoal = it },
                    valueRange = 6.0f..10.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF06B6D4),
                        activeTrackColor = Color(0xFF3B82F6)
                    )
                )

                // Prediction calculations
                val isBelow = targetCgpaGoal > aggregateCgpa
                val diffFactor = targetCgpaGoal - aggregateCgpa
                val requiredGpaRaw = if (semesters.isEmpty()) targetCgpaGoal else (targetCgpaGoal + diffFactor)

                Text(
                    text = if (semesters.isEmpty()) {
                        "Locked at baseline. Enter a semester history to execute analytics."
                    } else if (requiredGpaRaw > 10.0f) {
                        "ALERT: Projected metric exceeds 10.0 max boundary. Increase credit density first."
                    } else {
                        String.format("Estimated GPA required next semester to hit target: %.2f", requiredGpaRaw)
                    },
                    color = if (requiredGpaRaw > 10.0f) Color(0xFFEF4444) else Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Semesters histories
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(semesters) { s ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(s.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${s.credits} Credits logged", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("GPA: ${s.gpa}", fontWeight = FontWeight.Black, color = Color(0xFF06B6D4), fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = { viewModel.removeSemester(s.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddGpaDialog) {
        AlertDialog(
            onDismissRequest = { showAddGpaDialog = false },
            title = { Text("Log Term Grade Record", color = Color.White) },
            containerColor = Color(0xFF0B0F1A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputSemesterTitle,
                        onValueChange = { inputSemesterTitle = it },
                        label = { Text("Semester Designation (e.g. Semester I)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6)
                        )
                    )

                    OutlinedTextField(
                        value = inputGpaStr,
                        onValueChange = { inputGpaStr = it },
                        label = { Text("GPA Secured (0.00 - 10.00)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6)
                        )
                    )

                    OutlinedTextField(
                        value = inputCreditsStr,
                        onValueChange = { inputCreditsStr = it },
                        label = { Text("Term Total Credits Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val gpaVal = inputGpaStr.toFloatOrNull() ?: 8.0f
                        val crVal = inputCreditsStr.toIntOrNull() ?: 20
                        if (inputSemesterTitle.isNotBlank()) {
                            viewModel.addSemester(inputSemesterTitle, gpaVal, crVal)
                            showAddGpaDialog = false
                            inputSemesterTitle = ""
                            inputGpaStr = ""
                            inputCreditsStr = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGpaDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
fun SemesterGpaGraph(semesters: List<SemesterGpa>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(vertical = 6.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxGpa = 10f

        // Grid Lines background
        for (i in 1..3) {
            val y = height * (1 - i / 3f)
            drawLine(
                color = Color(0x0EFFFFFF),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        if (semesters.size > 1) {
            val points = semesters.mapIndexed { index, sem ->
                val x = index * (width / (semesters.size - 1))
                val y = height - (sem.gpa / maxGpa * height)
                Offset(x, y)
            }

            // Area shader
            val path = Path().apply {
                moveTo(points.first().x, height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x2A3B82F6), Color.Transparent)
                )
            )

            // Direct plotting links
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Color(0xFF3B82F6),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 3f
                )
            }

            // Dot points
            points.forEach { pt ->
                drawCircle(color = Color(0xFF06B6D4), radius = 5f, center = pt)
                drawCircle(color = Color(0x3306B6D4), radius = 10f, center = pt)
            }
        } else if (semesters.size == 1) {
            val pt = Offset(width / 2f, height - (semesters.first().gpa / maxGpa * height))
            drawCircle(color = Color(0xFF3B82F6), radius = 6f, center = pt)
            drawCircle(color = Color(0x333B82F6), radius = 12f, center = pt)
        }
    }
}

// ===================== MODULE 9: AEGIS AI STUDY ASSISTANT =====================
@Composable
fun AiAssistantScreen(viewModel: ZenithViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isTyping by viewModel.isAegisTyping.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val suggestions = listOf(
        "Analyze midterm readiness gaps",
        "Summarize ethical AI core",
        "Formulate a study schedule",
        "Teach me Quantum Computing fundamentals"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AEGIS INTELLIGENCE COGNITIVE ENGINE",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            IconButton(onClick = { viewModel.clearChat() }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Chat", tint = Color(0xFF06B6D4))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // History Scroller
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x0FFFFFFF))
                .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(16.dp))
                .padding(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(chatHistory) { msg ->
                    val isUser = msg.role == "user"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .background(
                                    if (isUser) Color(0x2D3B82F6) else Color(0x1CFFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (isUser) Color(0x443B82F6) else Color(0x1F06B6D4),
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isUser) "OPERATOR USER" else "AEGIS INTELLIGENCE",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isUser) Color(0xFF3B82F6) else Color(0xFF06B6D4),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                if (isTyping) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF06B6D4))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF06B6D4))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF06B6D4))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AEGIS drafting response matrices...", color = Color(0xFF06B6D4), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Suggestions buttons
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x11FFFFFF))
                        .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(10.dp))
                        .clickable { viewModel.sendMessageToAegis(label) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input send layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                placeholder = { Text("Query AI Study Core...", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF06B6D4),
                    unfocusedBorderColor = Color(0x33FFFFFF)
                )
            )

            Button(
                onClick = {
                    if (inputQuery.isNotBlank()) {
                        viewModel.sendMessageToAegis(inputQuery)
                        inputQuery = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}

// ===================== MODULE 10: SOCIAL FEED =====================
@Composable
fun SocialScreen(viewModel: ZenithViewModel) {
    val posts by viewModel.communityPosts.collectAsState()
    var postDraftInput by remember { mutableStateOf("") }
    var selectDraftTag by remember { mutableStateOf("STUDY GROUP") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "UNIVERSITY INTEL SHARING HUB",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Log post panel
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = postDraftInput,
                    onValueChange = { postDraftInput = it },
                    placeholder = { Text("Broadcast query or study notes to campus group...", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0x13FFFFFF)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tag selector
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("CAMPUS", "STUDY GROUP", "TIPS").forEach { tag ->
                            val active = selectDraftTag == tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) Color(0x323B82F6) else Color(0x11FFFFFF))
                                    .clickable { selectDraftTag = tag }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(tag, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (postDraftInput.isNotBlank()) {
                                viewModel.addPost(postDraftInput, selectDraftTag)
                                postDraftInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Database empty. Start the first topic thread!", color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(posts) { post ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF8B5CF6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = post.author.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(post.author, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(post.authorTitle, color = Color(0xFF06B6D4), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0x238B5CF6))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(post.tag, color = Color(0xFF8B5CF6), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = post.content,
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive statistics row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { viewModel.toggleLikePost(post.id) }
                                ) {
                                    Icon(
                                        imageVector = if (post.likedByUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Likes",
                                        tint = if (post.likedByUser) Color(0xFFEF4444) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${post.likes} Likes", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Create,
                                        contentDescription = "Comments",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${post.commentsCount} comments", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===================== CUSTOM GLASS PANEL CONTAINER =====================
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderGlow: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0x13FFFFFF))
            .border(
                1.dp,
                if (borderGlow != Color.Transparent) borderGlow.copy(alpha = 0.5f) else Color(0x1AFFFFFF),
                RoundedCornerShape(22.dp)
            )
    ) {
        content()
    }
}

// ===================== STUDENT LOGIN & SWAP INTERFACES =====================
@Composable
fun StudentLoginOverlayScreen(viewModel: ZenithViewModel) {
    val allStudents by viewModel.allStudents.collectAsState()
    var isSignUpMode by remember { mutableStateOf(false) }

    // Forms fields
    var nameInput by remember { mutableStateOf("") }
    var regNoInput by remember { mutableStateOf("") }
    var sessionInput by remember { mutableStateOf("") }
    var deptInput by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Logo & Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Zenith OS Terminal",
                    tint = Color(0xFF06B6D4),
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = "ZENITH LINK OS",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "AUTHENTICATION PROTOCOL REQUIRED",
                    color = Color(0xFF06B6D4),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderGlow = Color(0x3306B6D4)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Switcher Headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x11FFFFFF)),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = { isSignUpMode = false; errorMessage = "" },
                            modifier = Modifier
                                .weight(1f)
                                .background(if (!isSignUpMode) Color(0x1F06B6D4) else Color.Transparent)
                        ) {
                            Text(
                                "ACCESS ACCOUNT",
                                color = if (!isSignUpMode) Color(0xFF06B6D4) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        TextButton(
                            onClick = { isSignUpMode = true; errorMessage = "" },
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSignUpMode) Color(0x1F06B6D4) else Color.Transparent)
                        ) {
                            Text(
                                "REGISTER IDENTITY",
                                color = if (isSignUpMode) Color(0xFF06B6D4) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (!isSignUpMode) {
                        // Access account mode: select existing student from list
                        Text(
                            text = "SELECT REGISTERED STUDENT PROFILE TO CONNECT:",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            allStudents.forEach { student ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x0EFFFFFF))
                                        .border(1.dp, Color(0x12FFFFFF), RoundedCornerShape(12.dp))
                                        .clickable { viewModel.selectStudent(student) }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0x228B5CF6))
                                                .border(1.dp, Color(0xFF8B5CF6), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color(0xFFC084FC),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = student.name,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${student.department} • ${student.rollNo}",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Log In",
                                        tint = Color(0xFF06B6D4),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Sign up / Switch mode
                        Text(
                            text = "INITIALIZE NEW IDENTITY ON THE CAMPUS DECRYPT NETWORK:",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Agent Scholar Name", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                focusedLabelColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0x33FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = regNoInput,
                            onValueChange = { regNoInput = it },
                            label = { Text("Registration Number", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                focusedLabelColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0x33FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = sessionInput,
                            onValueChange = { sessionInput = it },
                            label = { Text("Academic Session (e.g. 2022-2026)", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                focusedLabelColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0x33FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = deptInput,
                            onValueChange = { deptInput = it },
                            label = { Text("Campus Department Name", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                focusedLabelColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0x33FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        if (errorMessage.isNotBlank()) {
                            Text(errorMessage, color = Color(0xFFEF4444), fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (nameInput.isBlank() || regNoInput.isBlank() || sessionInput.isBlank() || deptInput.isBlank()) {
                                    errorMessage = "Initialization elements cannot be empty."
                                } else {
                                    viewModel.registerAndSelectStudent(
                                        name = nameInput.trim(),
                                        registrationNo = regNoInput.trim(),
                                        session = sessionInput.trim(),
                                        department = deptInput.trim()
                                    )
                                    nameInput = ""
                                    regNoInput = ""
                                    sessionInput = ""
                                    deptInput = ""
                                    errorMessage = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("COMMISSION CURRENT STATE", color = Color.Black, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }

            // Bottom security trace text logs
            Text(
                text = "STATUS // SYS_TRACE.LINK_OK • ENCRYPTION: AES_256 • SEC_PORT_4550",
                color = Color(0x44FFFFFF),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StudentSwitchDialog(viewModel: ZenithViewModel, onDismiss: () -> Unit) {
    val allStudents by viewModel.allStudents.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()

    var isAddingStudent by remember { mutableStateOf(false) }

    // form inputs
    var nameInput by remember { mutableStateOf("") }
    var regNoInput by remember { mutableStateOf("") }
    var sessionInput by remember { mutableStateOf("") }
    var deptInput by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            borderGlow = Color(0xFF8B5CF6)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAddingStudent) "INITIALIZE IDENTITY" else "ACCOUNTS PORTAL",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                if (!isAddingStudent) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        allStudents.forEach { student ->
                            val isCurrent = student.id == currentStudent?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isCurrent) Color(0x338B5CF6) else Color(0x0EFFFFFF))
                                    .border(
                                        1.dp,
                                        if (isCurrent) Color(0xFF8B5CF6) else Color(0x12FFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.selectStudent(student)
                                        onDismiss()
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (isCurrent) Color(0xFFC084FC) else Color(0xFF94A3B8)
                                    )
                                    Column {
                                        Text(student.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("${student.department} • ${student.rollNo}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                }

                                if (isCurrent) {
                                    Text("ACTIVE", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Dynamic action buttons inside switcher
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { isAddingStudent = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x338B5CF6)),
                                border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New profile", color = Color.White, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.logoutStudent()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x1FEF4444)),
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Logout", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    // Registration inside switcher dialog
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Agent Scholar Name", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                focusedLabelColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )

                        OutlinedTextField(
                            value = regNoInput,
                            onValueChange = { regNoInput = it },
                            label = { Text("Registration Number", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                focusedLabelColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )

                        OutlinedTextField(
                            value = sessionInput,
                            onValueChange = { sessionInput = it },
                            label = { Text("Academic Session", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                focusedLabelColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )

                        OutlinedTextField(
                            value = deptInput,
                            onValueChange = { deptInput = it },
                            label = { Text("Campus Department Name", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                focusedLabelColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )

                        if (err.isNotBlank()) {
                            Text(err, color = Color(0xFFEF4444), fontSize = 12.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = { isAddingStudent = false; err = "" },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    if (nameInput.isBlank() || regNoInput.isBlank() || sessionInput.isBlank() || deptInput.isBlank()) {
                                        err = "All elements are required."
                                    } else {
                                        viewModel.registerAndSelectStudent(
                                            name = nameInput.trim(),
                                            registrationNo = regNoInput.trim(),
                                            session = sessionInput.trim(),
                                            department = deptInput.trim()
                                        )
                                        isAddingStudent = false
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Create", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
