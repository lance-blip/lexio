package za.co.quikle.lexio.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import za.co.quikle.lexio.domain.model.LegalCitation

data class HomeUiState(
    val isLoading: Boolean = false,
    val greeting: String = "Hello, Citizen",
    val subtitle: String = "How can I help you today?",
    val featuredRights: List<FeaturedRight> = emptyList(),
    val trendingQuestions: List<String> = emptyList(),
    val categories: List<TopicCategory> = emptyList()
)

data class FeaturedRight(
    val id: String,
    val title: String,
    val description: String,
    val citationShort: String,
    val categoryIcon: ImageVector,
    val categoryId: String
)

data class TopicCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            categories = mockCategories(),
            featuredRights = mockFeaturedRights(),
            trendingQuestions = mockTrendingQuestions()
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private fun mockCategories(): List<TopicCategory> = listOf(
        TopicCategory("workplace", "Workplace Rights", Icons.Filled.Work),
        TopicCategory("consumer", "Consumer Protection", Icons.Filled.ShoppingCart),
        TopicCategory("housing", "Housing", Icons.Filled.Home),
        TopicCategory("constitutional", "Constitutional Rights", Icons.Filled.AccountBalance),
        TopicCategory("criminal", "Criminal Law", Icons.Filled.Gavel),
        TopicCategory("family", "Family Law", Icons.Filled.FamilyRestroom),
        TopicCategory("privacy", "Privacy & Data", Icons.Filled.Lock)
    )

    private fun mockFeaturedRights(): List<FeaturedRight> = listOf(
        FeaturedRight(
            id = "1",
            title = "Your employer must give you a hearing before dismissal",
            description = "No employee may be dismissed without a fair procedure, including the right to be heard and to have representation.",
            citationShort = "LRA s188",
            categoryIcon = Icons.Filled.Work,
            categoryId = "workplace"
        ),
        FeaturedRight(
            id = "2",
            title = "You have the right to return a defective product within 6 months",
            description = "The Consumer Protection Act gives you the right to return goods that are defective, unsafe, or not fit for purpose.",
            citationShort = "CPA s56",
            categoryIcon = Icons.Filled.ShoppingCart,
            categoryId = "consumer"
        ),
        FeaturedRight(
            id = "3",
            title = "No one may be evicted without a court order",
            description = "The Prevention of Illegal Eviction Act requires a court order for any eviction, and the court must consider all relevant circumstances.",
            citationShort = "PIE Act s4",
            categoryIcon = Icons.Filled.Home,
            categoryId = "housing"
        ),
        FeaturedRight(
            id = "4",
            title = "You have the right to remain silent when arrested",
            description = "Every arrested person has the right to remain silent, to be informed of the right to remain silent, and to be brought before a court within 48 hours.",
            citationShort = "Constitution s35",
            categoryIcon = Icons.Filled.Security,
            categoryId = "criminal"
        )
    )

    private fun mockTrendingQuestions(): List<String> = listOf(
        "Can my landlord increase rent without notice?",
        "What are my rights if I'm arrested?",
        "Can my employer deduct money from my salary without consent?",
        "How do I file a complaint with the CCMA?",
        "What does POPIA mean for my personal data?"
    )
}
