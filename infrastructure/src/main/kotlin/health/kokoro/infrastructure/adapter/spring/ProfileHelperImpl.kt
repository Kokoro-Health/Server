package health.kokoro.infrastructure.adapter.spring

import health.kokoro.domain.port.spring.ProfileHelper
import org.springframework.core.env.Environment
import org.springframework.stereotype.Repository

@Repository
class ProfileHelperImpl(
    private val env: Environment
): ProfileHelper {
    override fun isDev(): Boolean {
       return env.activeProfiles.contains(DEV_NAME)
    }

    override fun isProd(): Boolean {
       return env.activeProfiles.contains(PROD_NAME)
    }

    companion object {
        private const val DEV_NAME = "dev"
        private const val PROD_NAME = "prod"
    }
}