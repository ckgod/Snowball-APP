package ckgod.snowball.invest.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ckgod.snowball.invest.feature.backtest.BacktestResultContent
import ckgod.snowball.invest.feature.detail.StockDetailContent
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack

/**
 * 2-pane 으로 넘어가는 기준 폭.
 *
 * 폴드8(SM-F971N) 실측: 내부 화면 933dp x 704dp, 커버 화면은 400dp 내외.
 * Material 의 expanded 기준 840dp 를 쓰면 펼친 세로(704dp)에서 2-pane 이 안 나오므로 600dp 로 잡는다.
 */
private val DualPaneMinWidth = 600.dp

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    // SnowballTheme 은 MaterialTheme 만 감싸고 배경을 칠하지 않는다. 지금까지는 화면마다
    // Scaffold 가 칠해줘서 문제가 없었는데, 2-pane 의 빈 패널처럼 Scaffold 가 없는 자리에서는
    // 매니페스트의 라이트 플랫폼 테마가 그대로 비쳐 다크 모드에 흰 배경이 보였다.
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints {
            if (maxWidth >= DualPaneMinWidth) {
                DualPaneRoot(component = component)
            } else {
                SinglePaneRoot(component = component)
            }
        }
    }
}

/**
 * 접힌 상태. 기존과 완전히 같다 — 스택 하나를 [Children] 으로 그리므로
 * 슬라이드 전환과 predictive back 이 그대로 동작한다.
 */
@Composable
private fun SinglePaneRoot(component: RootComponent) {
    Children(
        stack = component.childStack,
        modifier = Modifier.fillMaxSize(),
        animation = backAnimation(
            backHandler = component.backHandler,
            onBack = component::onBackClicked
        )
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Main -> MainContent(component = instance.component)
            is RootComponent.Child.StockDetail -> StockDetailContent(component = instance.component)
            is RootComponent.Child.BacktestResult -> BacktestResultContent(component = instance.component)
        }
    }
}

/**
 * 펼친 상태. 왼쪽은 스택 맨 아래의 Main 을 직접 그리고, 오른쪽은 **같은 스택**을 [Children] 으로 그린다.
 *
 * 깊이를 없애지 않고 그리는 방식만 바꾸는 것이 핵심이다.
 * 활성 child 가 Main 일 때(=아직 아무것도 고르지 않았을 때)만 오른쪽에 안내 문구를 놓는다.
 *
 * 전환 애니메이션은 [SinglePaneRoot] 와 다르게 준다. 접힘은 화면이 통째로 바뀌므로 슬라이드와
 * predictive back 이 맞지만, 펼침은 왼쪽 목록이 제자리에 있고 오른쪽 내용만 갈리므로 교차 페이드가 맞다.
 *
 * 한 가지 감수하는 점: 상세가 활성인 동안 Main 은 백스택에 있어 라이프사이클이 STOPPED 인데
 * 화면에는 보인다. 지금 HomeComponent 는 doOnDestroy 만 쓰므로 데이터 흐름에 영향이 없다.
 * 나중에 Home 에 onStart/onStop 의존 로직을 넣는다면 이 지점을 다시 봐야 한다.
 */
@Composable
private fun DualPaneRoot(component: RootComponent) {
    val stack by component.childStack.subscribeAsState()

    val mainComponent = stack.items
        .firstNotNullOfOrNull { (it.instance as? RootComponent.Child.Main)?.component }

    if (mainComponent == null) {
        // 스택 맨 아래는 항상 Main 이라 여기 오지 않는다. 와도 한 화면으로 떨어지게 둔다.
        SinglePaneRoot(component = component)
        return
    }

    if (!shouldShowSidePane(rootStack = stack, mainComponent = mainComponent)) {
        MainContent(
            component = mainComponent,
            modifier = Modifier.fillMaxSize(),
            useNavigationRail = true
        )
        return
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clipToBounds()
        ) {
            MainContent(
                component = mainComponent,
                useNavigationRail = true
            )
        }

        VerticalDivider()

        // clipToBounds 가 없으면 전환 중 화면이 이 패널을 넘어 왼쪽 목록 위에 그려진다.
        // slide() 는 Modifier.layout 으로 자기 너비만큼 밀어서 배치하는데 layout 은 클리핑을 하지 않고,
        // Compose 도 자식을 부모 경계로 자르지 않는다.
        Box(
            modifier = Modifier
                .weight(1f)
                .clipToBounds()
        ) {
            Children(
                stack = component.childStack,
                modifier = Modifier.fillMaxSize(),
                // 접힘과 다른 애니메이션을 쓴다.
                // 반쪽 폭 패널 안에서 화면을 통째로 미는 건 목록-상세 구조에 맞지 않는다.
                // 왼쪽 목록은 제자리에 있고 오른쪽 내용만 바뀌므로 교차 페이드가 맞다.
                // 뒤로가기 동작 자체는 childStack(handleBackButton = true) 이 계속 처리한다.
                animation = stackAnimation(fade())
            ) { child ->
                when (val instance = child.instance) {
                    // 왼쪽에 이미 그려져 있다. 오른쪽은 비어 있음을 알린다.
                    is RootComponent.Child.Main -> EmptyDetailPane()

                    is RootComponent.Child.StockDetail -> StockDetailContent(
                        component = instance.component,
                        // 왼쪽 목록이 그대로 보이므로 뒤로가기 화살표가 가리킬 곳이 없다.
                        showBackButton = false
                    )

                    is RootComponent.Child.BacktestResult -> BacktestResultContent(
                        component = instance.component
                    )
                }
            }
        }
    }
}

/**
 * 펼친 상태에서 오른쪽 패널을 띄울 이유가 있는지 판단한다.
 *
 * 목록-상세 구조인 탭은 투자 현황뿐이다. 잔고와 백테스트는 오른쪽에 채울 내용이 없어서,
 * 빈 패널을 두면 화면 절반을 버리는 셈이 된다.
 *
 * 단 백테스트 **결과**는 예외다. 결과가 뜬 동안은 왼쪽에 조건 입력, 오른쪽에 결과로 나란히 두는 게 낫다.
 * 그래서 탭이 아니라 **루트 스택에 실제로 띄울 자식이 있는지**를 먼저 본다.
 */
@Composable
private fun shouldShowSidePane(
    rootStack: ChildStack<*, RootComponent.Child>,
    mainComponent: MainComponent
): Boolean {
    // 분기 안에서 구독하면 분기가 바뀔 때마다 구독이 끊겼다 붙는다. 무조건 먼저 구독한다.
    val mainStack by mainComponent.childStack.subscribeAsState()

    return when (rootStack.active.instance) {
        is RootComponent.Child.StockDetail,
        is RootComponent.Child.BacktestResult -> true

        is RootComponent.Child.Main -> mainStack.active.instance is MainComponent.Child.Home
    }
}

@Composable
private fun EmptyDetailPane(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "종목을 선택해 주세요",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "왼쪽 목록에서 종목을 누르면 이곳에 상세가 표시됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
