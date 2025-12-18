package ckgod.snowball.invest

import platform.Foundation.NSBundle

actual object AppConfig {
    actual val API_BASE_URL: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("API_BASE_URL") as? String
            ?: "http://0.0.0.0:8080"

    actual val API_KEY: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("API_KEY") as? String
            ?: "exit"
}
