package za.co.quikle.lexio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import za.co.quikle.lexio.navigation.Screen
import za.co.quikle.lexio.ui.components.RightsCard
import za.co.quikle.lexio.ui.theme.Dimensions
import za.co.quikle.lexio.ui.theme.PrimaryGreen
import za.co.quikle.lexio.ui.theme.PrimaryGreenDark
import za.co.quikle.lexio.ui.theme.PrimaryGreenLight
import za.co.quikle.lexio.ui.theme.SecondaryGold
import za.co.quikle.lexio.ui.theme.SecondaryGoldDark
import za.co.quikle.lexio.ui.theme.SecondaryGoldLight

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Top greeting section
            item {
                GreetingSection(
                    greeting = uiState.greeting,
                    subtitle = uiState.subtitle,
                    onSearchClick = { query ->
                        navController.navigate(Screen.Chat.createRoute(prefillQuery = query))
                    }
                )
            }

            // Primary action cards
            item {
                ActionCardsRow(
                    onAskAnythingClick = {
                        navController.navigate(Screen.Chat.createRoute())
                    },
                    onIsThisLegalClick = {
                        navController.navigate(Screen.Scenario.route)
                    }
                )
            }

            // Your Topics section
            item {
                SectionHeader(title = "Your Topics")
            }
            item {
                TopicChipsRow(
                    categories = uiState.categories,
                    onCategoryClick = { category ->
                        navController.navigate(Screen.LibraryDetail.createRoute(category.id))
                    }
                )
            }

            // Know Your Rights section
            item {
                SectionHeader(title = "Know Your Rights")
            }
            items(uiState.featuredRights) { right ->
                FeaturedRightItem(
                    right = right,
                    onClick = {
                        navController.navigate(Screen.LibraryDetail.createRoute(right.categoryId))
                    }
                )
            }

            // Trending Questions section
            item {
                SectionHeader(title = "What others are asking")
            }
            items(uiState.trendingQuestions) { question ->
                TrendingQuestionCard(
                    question = question,
                    onClick = {
                        navController.navigate(Screen.Chat.createRoute(prefillQuery = question))
                    }
                )
            }
        }
    }
}

@Composable
private fun GreetingSection(
    greeting: String,
    subtitle: String,
    onSearchClick: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 8.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search for a legal topic...",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.CardCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        // Tapping search navigates to chat with the text
        if (searchText.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to search →",
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryGreen,
                modifier = Modifier
                    .clickable { onSearchClick(searchText) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun ActionCardsRow(
    onAskAnythingClick: () -> Unit,
    onIsThisLegalClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionCard(
            title = "Ask Me\nAnything",
            icon = Icons.Filled.ChatBubble,
            gradientColors = listOf(PrimaryGreen, PrimaryGreenLight),
            onClick = onAskAnythingClick,
            modifier = Modifier.weight(1f)
        )
        ActionCard(
            title = "Is This\nLegal?",
            icon = Icons.Filled.Shield,
            gradientColors = listOf(SecondaryGoldDark, SecondaryGold),
            onClick = onIsThisLegalClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(Dimensions.CardCornerRadiusHero),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopEnd),
                tint = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun TopicChipsRow(
    categories: List<TopicCategory>,
    onCategoryClick: (TopicCategory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            TopicChip(
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun TopicChip(
    category: TopicCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimensions.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(18.dp),
                tint = PrimaryGreen
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FeaturedRightItem(
    right: FeaturedRight,
    onClick: () -> Unit
) {
    RightsCard(
        title = right.title,
        summary = right.description,
        citationText = right.citationShort,
        icon = right.categoryIcon,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
private fun TrendingQuestionCard(
    question: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(Dimensions.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = question,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
