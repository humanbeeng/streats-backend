package com.streats.backendphaseone.auth.data.dto


data class AuthResponse(
    val accessToken: String,
    val isVerified: Boolean
)