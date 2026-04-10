package health.kokoro.application.usecase.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

class RequestDetails(private val request: HttpServletRequest) {
    fun getUserAgent(): String {
        return request.getHeader(HttpHeaders.USER_AGENT)
    }

    fun getIpAddress(): String {
        return request.remoteAddr
    }
}