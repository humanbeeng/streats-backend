package com.streats.backendphaseone.auth.controller

import com.streats.backendphaseone.auth.data.dto.AuthRequest
import com.streats.backendphaseone.auth.data.dto.AuthResponse
import com.streats.backendphaseone.auth.domain.usecase.AuthenticateUser
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
        val authResponse = authenticateUser.authenticate(authRequest.idToken)
        return if (authResponse.isVerified) {
            ResponseEntity.ok(authResponse)
        } else ResponseEntity.badRequest().build()
    }

}