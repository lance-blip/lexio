package za.co.quikle.lexio.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Chat : Screen("chat?conversationId={conversationId}&prefillQuery={prefillQuery}") {
        fun createRoute(conversationId: String? = null, prefillQuery: String? = null): String {
            val base = "chat"
            val params = mutableListOf<String>()
            if (conversationId != null) {
                params.add("conversationId=$conversationId")
            }
            if (prefillQuery != null) {
                val encoded = URLEncoder.encode(prefillQuery, StandardCharsets.UTF_8.toString())
                params.add("prefillQuery=$encoded")
            }
            return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
        }
    }
    data object Scenario : Screen("scenario")
    data object Library : Screen("library")
    data object LibraryDetail : Screen("library_detail/{categoryId}") {
        fun createRoute(categoryId: String): String {
            return "library_detail/$categoryId"
        }
    }
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object Premium : Screen("premium")
}
