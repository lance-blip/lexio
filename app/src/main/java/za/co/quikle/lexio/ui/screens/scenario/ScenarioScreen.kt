package za.co.quikle.lexio.ui.screens.scenario

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import za.co.quikle.lexio.domain.model.ApplicableLaw
import za.co.quikle.lexio.domain.model.NextStep
import za.co.quikle.lexio.domain.model.ScenarioAnalysis
import za.co.quikle.lexio.navigation.Screen
import za.co.quikle.lexio.ui.components.CitationChip
import za.co.quikle.lexio.ui.theme.Dimensions
import za.co.quikle.lexio.ui.theme.ErrorRed
import za.co.quikle.lexio.ui.theme.PrimaryGreen
import za.co.quikle.lexio.ui.theme.SuccessGreen
import za.co.quikle.lexio.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioScreen(
    navController: NavHostController,
    viewModel: ScenarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (uiState.currentStep != ScenarioStep.LOADING) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Is This Legal?",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        if (uiState.currentStep == ScenarioStep.RESULTS) {
                            IconButton(onClick = { viewModel.resetToInput() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "scenario_step",
            modifier = Modifier.padding(paddingValues)
        ) { step ->
            when (step) {
                ScenarioStep.INPUT -> InputStep(
                    uiState = uiState,
                    onInputChanged = viewModel::onInputChanged,
                    onWhoChanged = viewModel::onWhoChanged,
                    onWhatChanged = viewModel::onWhatChanged,
                    onWhenChanged = viewModel::onWhenChanged,
                    onWhereChanged = viewModel::onWhereChanged,
                    onToggleGuided = viewModel::toggleGuidedPrompts,
                    onUseGuidedDetails = viewModel::useGuidedDetails,
                    onAnalyse = viewModel::analyseScenario
                )
                ScenarioStep.LOADING -> LoadingStep(
                    progress = uiState.loadingProgress
                )
                ScenarioStep.RESULTS -> ResultsStep(
                    analysis = uiState.analysisResult,
                    category = uiState.identifiedCategory,
                    onShareClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Share feature coming soon")
                        }
                    },
                    onFollowUpClick = {
                        val scenarioContext = uiState.analysisResult?.userScenario ?: ""
                        navController.navigate(
                            Screen.Chat.createRoute(
                                prefillQuery = "Follow up on my scenario: $scenarioContext"
                            )
                        )
                    },
                    onNewAnalysis = viewModel::resetToInput
                )
            }
        }
    }
}

@Composable
private fun InputStep(
    uiState: ScenarioUiState,
    onInputChanged: (String) -> Unit,
    onWhoChanged: (String) -> Unit,
    onWhatChanged: (String) -> Unit,
    onWhenChanged: (String) -> Unit,
    onWhereChanged: (String) -> Unit,
    onToggleGuided: () -> Unit,
    onUseGuidedDetails: () -> Unit,
    onAnalyse: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Describe a situation and we'll analyse it against South African law",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main text input
        OutlinedTextField(
            value = uiState.scenarioInput,
            onValueChange = onInputChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = {
                Text(
                    text = "Tell us what happened. Include who was involved, what they did, and when it happened...",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Character counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${uiState.scenarioInput.length} characters",
                style = MaterialTheme.typography.labelSmall,
                color = if (uiState.scenarioInput.length < 20)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Guided prompts
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleGuided() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Need help describing it?",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (uiState.isGuidedExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(
                    visible = uiState.isGuidedExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        GuidedField(
                            label = "Who was involved?",
                            value = uiState.whoInvolved,
                            onValueChange = onWhoChanged
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GuidedField(
                            label = "What happened?",
                            value = uiState.whatHappened,
                            onValueChange = onWhatChanged
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GuidedField(
                            label = "When did this happen?",
                            value = uiState.whenHappened,
                            onValueChange = onWhenChanged
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GuidedField(
                            label = "Where did this happen?",
                            value = uiState.whereHappened,
                            onValueChange = onWhereChanged
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onUseGuidedDetails,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Use these details")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Analyse button
        Button(
            onClick = onAnalyse,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.scenarioInput.length >= 20,
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        ) {
            Text(
                text = "Analyse Against the Law",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        if (uiState.scenarioInput.isNotEmpty() && uiState.scenarioInput.length < 20) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please enter at least 20 characters to analyse",
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GuidedField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun LoadingStep(progress: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    val steps = listOf(
        "Identifying area of law...",
        "Searching relevant legislation...",
        "Analysing your scenario...",
        "Preparing your analysis..."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Shield,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .scale(scale),
            tint = PrimaryGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Analysing against South African law...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        steps.forEachIndexed { index, step ->
            val isComplete = index < progress
            val isCurrent = index == progress - 1 || (index == progress && progress < steps.size)

            AnimatedVisibility(
                visible = index <= progress,
                enter = fadeIn() + expandVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isComplete) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = SuccessGreen
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isComplete) "✓ ${step.removeSuffix("...")}" else step,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isComplete) SuccessGreen
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultsStep(
    analysis: ScenarioAnalysis?,
    category: String,
    onShareClick: () -> Unit,
    onFollowUpClick: () -> Unit,
    onNewAnalysis: () -> Unit
) {
    if (analysis == null) return

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Verdict header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Here's What the Law Says",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Category badge
        if (category.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryGreen.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Applicable Laws
        SectionTitle("Applicable Laws")
        analysis.applicableLaws.forEachIndexed { index, law ->
            ExpandableLawCard(
                law = law,
                initiallyExpanded = index == 0
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analysis
        SectionTitle("Analysis")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Text(
                text = analysis.analysis,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp),
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Potential Violations
        if (analysis.potentialViolations.isNotEmpty()) {
            SectionTitle("Potential Violations")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = WarningAmber.copy(alpha = 0.08f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    analysis.potentialViolations.forEach { violation ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = WarningAmber
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = violation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Your Rights
        SectionTitle("Your Rights in This Situation")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = SuccessGreen.copy(alpha = 0.08f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                analysis.rights.forEach { right ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = right,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Steps
        SectionTitle("What You Can Do")
        analysis.nextSteps.forEach { step ->
            NextStepCard(step = step)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Button(
            onClick = onShareClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share This Analysis")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onFollowUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(Dimensions.CardCornerRadius)
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ask a Follow-Up", color = PrimaryGreen)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onNewAnalysis,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "New Analysis",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ExpandableLawCard(
    law: ApplicableLaw,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = law.actName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Toggle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    law.sections.forEach { section ->
                        Text(
                            text = "• $section",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = law.relevance,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NextStepCard(step: NextStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = step.action,
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (step.contactInfo != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = step.contactInfo,
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Medium
                )
            }
            if (step.url != null) {
                Text(
                    text = step.url,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
