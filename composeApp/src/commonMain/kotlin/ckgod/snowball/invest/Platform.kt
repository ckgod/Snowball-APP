package ckgod.snowball.invest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform