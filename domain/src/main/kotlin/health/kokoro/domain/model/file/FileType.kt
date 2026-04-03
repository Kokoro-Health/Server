package health.kokoro.domain.model.file

enum class FileType(
    val extensions: List<String>
) {
    IMAGE(listOf("png", "jpg", "jpeg", "svg", "webp"))
}