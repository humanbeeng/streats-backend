package app.streat.backend.auth.controller

import app.streat.backend.auth.data.dto.AuthRequestDTO
import app.streat.backend.auth.data.dto.LoginRequestDTO
import app.streat.backend.auth.data.dto.LoginResponseDTO
import app.streat.backend.auth.domain.usecase.AuthenticateUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authenticateUser: AuthenticateUser) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO): ResponseEntity<LoginResponseDTO> {
        return try {
            val loginRequest = loginRequestDTO.toLoginRequest()
            val loginResponse = authenticateUser.login(loginRequest)
            if (loginResponse.isVerified) {
                ResponseEntity.ok(loginResponse)
            } else ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping
    fun authenticate(
        @RequestBody authRequestDTO: AuthRequestDTO
    ): ResponseEntity<Unit> {
        return try {
            authenticateUser.authenticate(authRequestDTO.toAuthRequest())
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

}