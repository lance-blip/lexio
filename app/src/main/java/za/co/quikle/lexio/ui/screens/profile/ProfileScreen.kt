package za.co.quikle.lexio.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import za.co.quikle.lexio.navigation.Screen
import za.co.quikle.lexio.ui.theme.Dimensions
import za.co.quikle.lexio.ui.theme.PrimaryGreen
import za.co.quikle.lexio.ui.theme.SecondaryGold
import za.co.quikle.lexio.ui.theme.TextOnPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Profile Header ───────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(Dimensions.CardCornerRadiusHero),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CardElevation),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile avatar",
                            modifier = Modifier.size(40.dp),
                            tint = PrimaryGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (uiState.isLoggedIn) uiState.userEmail else "Guest User",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!uiState.isLoggedIn) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sign in coming soon")
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Sign In")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Premium badge
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isPremium) SecondaryGold.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = null,
                                    tint = SecondaryGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isPremium) "Pro Citizen" else "Free Plan",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (!uiState.isPremium) {
                                Text(
                                    text = "Upgrade",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.Premium.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Your Activity ────────────────────────────────────────────
            SectionTitle("Your Activity")
            MenuCard {
                MenuItem(
                    icon = Icons.Filled.ChatBubble,
                    title = "Saved Conversations",
                    trailing = "${uiState.savedConversationsCount}",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Saved conversations coming soon")
                        }
                    }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Bookmark,
                    title = "Saved Rights",
                    trailing = "${uiState.savedRightsCount}",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Saved rights coming soon")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Preferences ──────────────────────────────────────────────
            SectionTitle("Preferences")
            MenuCard {
                MenuToggleItem(
                    icon = Icons.Filled.DarkMode,
                    title = "Dark Mode",
                    isChecked = uiState.isDarkMode,
                    onToggle = { viewModel.toggleDarkMode() }
                )
                MenuDivider()
                MenuToggleItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notifications",
                    isChecked = uiState.isNotificationsEnabled,
                    onToggle = { viewModel.toggleNotifications() }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Language,
                    title = "Language",
                    trailing = "English",
                    onClick = { viewModel.showLanguageSheet() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── About ────────────────────────────────────────────────────
            SectionTitle("About")
            MenuCard {
                MenuItem(
                    icon = Icons.Filled.Info,
                    title = "About Lexio",
                    onClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Warning,
                    title = "Legal Disclaimer",
                    onClick = { viewModel.showDisclaimerSheet() }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Policy,
                    title = "Privacy Policy",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Privacy Policy coming soon")
                        }
                    }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Policy,
                    title = "Terms of Service",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Terms of Service coming soon")
                        }
                    }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Star,
                    title = "Rate the App",
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=za.co.quikle.lexio")
                            )
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=za.co.quikle.lexio")
                            )
                            context.startActivity(intent)
                        }
                    }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.Share,
                    title = "Share with Friends",
                    onClick = {
                        val shareText = "Check out Lexio — a free AI that helps you understand South African law! https://play.google.com/store/apps/details?id=za.co.quikle.lexio"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Lexio"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Support ──────────────────────────────────────────────────
            SectionTitle("Support")
            MenuCard {
                MenuItem(
                    icon = Icons.Filled.Email,
                    title = "Send Feedback",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@quikle.co.za")
                            putExtra(Intent.EXTRA_SUBJECT, "Lexio Feedback")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No email app found")
                            }
                        }
                    }
                )
                MenuDivider()
                MenuItem(
                    icon = Icons.Filled.ReportProblem,
                    title = "Report a Problem",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@quikle.co.za")
                            putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
                            putExtra(Intent.EXTRA_TEXT, "Please describe the issue:\n\n")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No email app found")
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Footer ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Powered by Quikle AI",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0-beta",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }

    // ── Language Bottom Sheet ─────────────────────────────────────────
    if (uiState.showLanguageSheet) {
        LanguageBottomSheet(onDismiss = { viewModel.hideLanguageSheet() })
    }

    // ── Disclaimer Bottom Sheet ──────────────────────────────────────
    if (uiState.showDisclaimerSheet) {
        DisclaimerBottomSheet(onDismiss = { viewModel.hideDisclaimerSheet() })
    }
}

// ── Reusable menu components ─────────────────────────────────────────

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun MenuCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(Dimensions.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    trailing: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = PrimaryGreen
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MenuToggleItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = PrimaryGreen
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryGreen,
                checkedTrackColor = PrimaryGreen.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 52.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

// ── Bottom Sheets ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Language",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // English — selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryGreen.copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "English",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Afrikaans — coming soon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Afrikaans",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryGold
                )
            }

            // isiZulu — coming soon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "isiZulu",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryGold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisclaimerBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Legal Disclaimer",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This app provides general legal information about South African law. It does not constitute legal advice, and no attorney-client relationship is created by using this app.\n\n" +
                        "For advice specific to your situation, consult a qualified South African legal practitioner.\n\n" +
                        "While we strive for accuracy, the law is complex and subject to change. Information provided may not reflect the most recent legal developments or address every aspect of a particular legal issue.\n\n" +
                        "Lexio uses artificial intelligence to generate responses. AI-generated content may contain errors or omissions. Always verify important legal information with a qualified professional.\n\n" +
                        "By using this app, you acknowledge that you understand these limitations and agree to use the information provided at your own risk.\n\n" +
                        "Lexio is powered by Quikle AI (Pty) Ltd. All rights reserved.\n\n" +
                        "© 2026 Quikle AI (Pty) Ltd",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = TextOnPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("I Understand")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
