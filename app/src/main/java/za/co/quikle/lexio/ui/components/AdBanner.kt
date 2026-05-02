package za.co.quikle.lexio.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import za.co.quikle.lexio.data.premium.PremiumManager

@Composable
fun AdBanner(
    modifier: Modifier = Modifier
) {
    val isPremium by PremiumManager.isPremium.collectAsState()

    if (!isPremium) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    // Test ad unit ID — replace with real ID before release
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}
