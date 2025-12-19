package ckgod.snowball.invest.feature.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.painterResource
import snowball.composeapp.generated.resources.Res
import snowball.composeapp.generated.resources.ic_chart

object ChartTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "차트"
            val icon = painterResource(Res.drawable.ic_chart)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("차트 화면 (준비 중)")
        }
    }
}
