package health.kokoro.api.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordValidator : ConstraintValidator<Password, String?> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Password cannot be empty")
                .addConstraintViolation()
            return false
        }

        val errors = mutableListOf<String>()

        if (value.length < PasswordRules.MIN_LENGTH) {
            errors.add("Minimum length is ${PasswordRules.MIN_LENGTH}")
        }
        if (value.length > PasswordRules.MAX_LENGTH) {
            errors.add("Maximum length is ${PasswordRules.MAX_LENGTH}")
        }
        if (!value.contains(Regex(PasswordRules.LOWERCASE_PATTERN))) {
            errors.add("Must contain at least one lowercase letter")
        }
        if (!value.contains(Regex(PasswordRules.UPPERCASE_PATTERN))) {
            errors.add("Must contain at least one uppercase letter")
        }
        if (!value.contains(Regex(PasswordRules.SPECIAL_CHAR_PATTERN))) {
            errors.add("Must contain at least one special character")
        }

        if (errors.isNotEmpty()) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(errors.joinToString(System.lineSeparator()))
                .addConstraintViolation()
            return false
        }

        return true
    }

    object PasswordRules {
        const val MIN_LENGTH = 8
        const val MAX_LENGTH = 200
        const val SPECIAL_CHAR_PATTERN = "[!?.\\-_%&|#]"
        const val LOWERCASE_PATTERN = "[a-z]"
        const val UPPERCASE_PATTERN = "[A-Z]"
    }
}