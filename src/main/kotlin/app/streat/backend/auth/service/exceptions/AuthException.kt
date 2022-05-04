package app.streat.backend.auth.service.exceptions

sealed class AuthException(override val message: String) : Exception(message) {

    class UserNotFoundException(override val message: String) : AuthException(message)



}
