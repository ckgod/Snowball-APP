package ckgod.snowball.invest.navigation

import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.essenty.backhandler.BackHandler

expect fun <C: Any, T: Any> backAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit
): StackAnimation<C, T>