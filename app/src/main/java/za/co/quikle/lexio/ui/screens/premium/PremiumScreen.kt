package za.co.quikle.lexio.ui.screens.premium

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import za.co.quikle.lexio.data.billing.BillingManager
import za.co.quikle.lexio.ui.theme.PrimaryGreen
import za.co.quikle.lexio.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    navController: NavHostController,
    billingManager: BillingManager? = null
) {
    val products by billingManager?.products?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    var selectedPeriod by remember { mutableStateOf("monthly") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pro Citizen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Upgrade to Pro Citizen",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Get unlimited access to all features",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Benefits list
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                PremiumBenefit("No ads — ever")
                PremiumBenefit("Unlimited legal questions")
                PremiumBenefit("Unlimited scenario analyses")
                PremiumBenefit("Detailed legal analysis with case law")
                PremiumBenefit("Document templates (CCMA forms, demand letters)")
                PremiumBenefit("Export conversations as PDF")
                PremiumBenefit("Offline access to full rights library")
                PremiumBenefit("Priority AI responses")
            }

            // Pricing toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PricingToggleButton(
                    text = "Monthly",
                    selected = selectedPeriod == "monthly",
                    onClick = { selectedPeriod = "monthly" },
                    modifier = Modifier.weight(1f)
                )
                PricingToggleButton(
                    text = "Annual",
                    selected = selectedPeriod == "annual",
                    onClick = { selectedPeriod = "annual" },
                    badge = "Save 30%",
                    modifier = Modifier.weight(1f)
                )
            }

            // Pricing card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedPeriod == "monthly") "R59" else "R499",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = if (selectedPeriod == "monthly") "/month" else "/year",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (selectedPeriod == "annual") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "That's only R41.58/month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val activity = LocalContext.current as? Activity
                    val targetProductId = if (selectedPeriod == "monthly")
                        BillingManager.MONTHLY_PRODUCT_ID else BillingManager.ANNUAL_PRODUCT_ID
                    val product = products.find { it.productId == targetProductId }

                    Button(
                        onClick = {
                            if (activity != null && product != null && billingManager != null) {
                                billingManager.launchPurchaseFlow(activity, product)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(
                            text = "Subscribe Now",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Footer
            Text(
                text = "Cancel anytime. Subscription managed through Google Play.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                billingManager?.checkSubscriptionStatus()
            }) {
                Text("Restore purchases")
            }
        }
    }
}

@Composable
private fun PremiumBenefit(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = PrimaryGreen
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PricingToggleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) PrimaryGreen.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = if (selected) 2.dp else 1.dp
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
