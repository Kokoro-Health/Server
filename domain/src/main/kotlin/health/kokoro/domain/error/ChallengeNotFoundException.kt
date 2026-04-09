package health.kokoro.domain.error

class ChallengeNotFoundException(val type: String) : KokoroException("No active $type challenge found")
