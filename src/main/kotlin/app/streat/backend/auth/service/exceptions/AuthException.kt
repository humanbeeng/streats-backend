package app.streat.backend.auth.service.exceptions

sealed class AuthException(override val message: String) : Exception(message) {

    object UserNotFoundException : AuthException("User not found")


}
