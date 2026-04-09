package health.kokoro.domain.error

class EmailAlreadyExistsException(val email: String) : KokoroException("Email $email is already in use")
