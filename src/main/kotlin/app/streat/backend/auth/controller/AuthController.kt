package app.streat.backend.auth.controller

import app.streat.backend.auth.data.dto.AuthRequest
import app.streat.backend.auth.data.dto.AuthResponse
import app.streat.backend.auth.domain.usecase.AuthenticateUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authenticateUser: AuthenticateUser) {

    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        return try{
            val authResponse = authenticateUser.authenticate(authRequest.idToken)
            if (authResponse.isVerified) {
            ResponseEntity.ok(authResponse)
        } else ResponseEntity.badRequest().build()
        } catch(e: Exception){
            ResponseEntity.badRequest().build()
        }
    }

}