package ckgod.snowball.invest

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.UIKit.UIApplicationWillTerminateNotification

// LifecycleRegistry를 iOS App 상태에 맞춰 자동으로 조작하는 함수
fun LifecycleRegistry.attachToDecomposeLifecycle() {
    val center = NSNotificationCenter.defaultCenter
    val mainQueue = NSOperationQueue.mainQueue

    // 1. 앱이 활성화될 때 (Foreground) -> RESUMED
    center.addObserverForName(
        name = UIApplicationDidBecomeActiveNotification,
        `object` = null,
        queue = mainQueue
    ) {
        resume()
    }

    // 2. 앱이 비활성화될 때 (잠시 멈춤, 알림 센터 내리기 등)
    center.addObserverForName(
        name = UIApplicationWillResignActiveNotification,
        `object` = null,
        queue = mainQueue
    ) { _ ->
    }

    // 3. 앱이 백그라운드로 갈 때 -> STOPPED
    center.addObserverForName(
        name = UIApplicationDidEnterBackgroundNotification,
        `object` = null,
        queue = mainQueue
    ) { _ ->
        stop()
    }

    // 4. 앱이 종료될 때 -> DESTROYED
    center.addObserverForName(
        name = UIApplicationWillTerminateNotification,
        `object` = null,
        queue = mainQueue
    ) {
        destroy()
    }

    // 초기 상태 설정: 현재 앱 상태를 확인하여 바로 시작
    val application = UIApplication.sharedApplication
    if (application.applicationState == platform.UIKit.UIApplicationState.UIApplicationStateActive) {
        resume()
    } else {
        stop()
    }
}