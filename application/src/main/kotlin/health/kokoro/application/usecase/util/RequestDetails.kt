package health.kokoro.application.usecase.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

class RequestDetails(private val request: HttpServletRequest) {
    fun getUserAgent(): String {
        return request.getHeader(HttpHeaders.USER_AGENT) ?: ""
    }

    fun getIpAddress(): String {
        val forwardedFor = request.getHeader("X-Forwarded-For")
        if (!forwardedFor.isNullOrBlank()) {
            return forwardedFor.split(",")[0].trim()
        }
        return request.remoteAddr ?: "unknown"
    }
}