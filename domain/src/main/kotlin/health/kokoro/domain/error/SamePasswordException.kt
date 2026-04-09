package health.kokoro.domain.error

class SamePasswordException : KokoroException("New password cannot be the same as the old one")
