package ckgod.snowball.invest

import ckgod.snowball.invest.BuildConfig as GeneratedBuildConfig

actual object AppConfig {
    actual val API_BASE_URL: String
        get() = GeneratedBuildConfig.API_BASE_URL
    actual val API_KEY: String
        get() = GeneratedBuildConfig.API_KEY
}
