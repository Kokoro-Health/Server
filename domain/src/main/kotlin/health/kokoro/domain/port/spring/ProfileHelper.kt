package health.kokoro.domain.port.spring

interface ProfileHelper {
    fun isDev(): Boolean
    fun isProd(): Boolean
}