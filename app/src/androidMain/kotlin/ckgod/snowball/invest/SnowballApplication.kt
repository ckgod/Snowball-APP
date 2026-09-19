package ckgod.snowball.invest

import android.app.Application
import ckgod.snowball.invest.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Koin 을 프로세스 수명에 맞춘다.
 *
 * 전에는 `MainActivity.onCreate` 에서 `startKoin`, `onDestroy` 에서 `stopKoin` 을 불렀다.
 * 화면을 접었다 펴면 구성 변경으로 Activity 가 재생성되면서 DI 컨테이너가 매번 껐다 켜졌다.
 * `retainedComponent` 로 컴포넌트 트리를 살려두는 지금 구조에서는 그게 더 위험하다 —
 * 살아남은 컴포넌트가 이미 닫힌 컨테이너의 싱글턴을 붙들고 있게 된다.
 */
class SnowballApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SnowballApplication)
            modules(appModule)
        }
    }
}
