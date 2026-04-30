package za.co.quikle.lexio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import za.co.quikle.lexio.ui.theme.CitationBackground
import za.co.quikle.lexio.ui.theme.CitationBackgroundDark
import za.co.quikle.lexio.ui.theme.SecondaryGoldDark
import za.co.quikle.lexio.ui.theme.SecondaryGoldLight

@Composable
fun CitationChip(
    reference: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) CitationBackgroundDark else CitationBackground
    val textColor = if (isDark) SecondaryGoldLight else SecondaryGoldDark

    Text(
        text = reference,
        style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        ),
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
