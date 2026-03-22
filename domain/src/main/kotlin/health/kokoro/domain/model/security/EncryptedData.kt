package health.kokoro.domain.model.security

data class EncryptedData(
    val cipherText: ByteArray,
    val initializationVector: ByteArray,
    val keyId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedData

        if (!cipherText.contentEquals(other.cipherText)) return false
        if (!initializationVector.contentEquals(other.initializationVector)) return false
        if (keyId != other.keyId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cipherText.contentHashCode()
        result = 31 * result + initializationVector.contentHashCode()
        result = 31 * result + keyId.hashCode()
        return result
    }

}
