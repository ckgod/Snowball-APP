package ckgod.snowball.invest.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object MyPageTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "백테스트"

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = null
                )
            }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("백테스트 화면 (준비 중)")
        }
    }
}
