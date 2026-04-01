package health.kokoro.infrastructure.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
@ConfigurationProperties(prefix = "kokoro.storage")
class FileStorageConfig {
     lateinit var path: String

     @PostConstruct
     fun init() {
          val storageDir = File(path)
          if (!storageDir.exists()) {
               if (!storageDir.mkdirs()) {
                    throw IllegalStateException("Failed to create storage directory: $path")
               }
          }
          if (!storageDir.canWrite()) {
               throw IllegalStateException("Cannot write to storage directory: $path")
          }
     }
}
