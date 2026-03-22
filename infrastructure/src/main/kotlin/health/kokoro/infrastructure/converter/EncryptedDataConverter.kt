package health.kokoro.infrastructure.converter

import health.kokoro.domain.model.security.EncryptedData
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.*

@Converter
class EncryptedDataConverter : AttributeConverter<EncryptedData, String> {

    override fun convertToDatabaseColumn(attribute: EncryptedData?): String {
        if (attribute == null) return ""
        return "${attribute.keyId}:${
            Base64.getEncoder().encodeToString(attribute.initializationVector)
        }:${Base64.getEncoder().encodeToString(attribute.cipherText)}"
    }

    override fun convertToEntityAttribute(dbData: String?): EncryptedData? {
        if (dbData.isNullOrBlank()) return null
        val parts = dbData.split(":")
        return EncryptedData(
            keyId = parts[0],
            initializationVector = Base64.getDecoder().decode(parts[1]),
            cipherText = Base64.getDecoder().decode(parts[2])
        )
    }
}
