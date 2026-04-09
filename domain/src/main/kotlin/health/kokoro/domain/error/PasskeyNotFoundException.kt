package health.kokoro.domain.error

import java.util.*

class PasskeyNotFoundException(passkeyId: UUID) : KokoroException("Passkey with id $passkeyId not found")
