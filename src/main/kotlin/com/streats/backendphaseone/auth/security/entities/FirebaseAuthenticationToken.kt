package com.streats.backendphaseone.auth.security.entities

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class FirebaseAuthenticationToken : UsernamePasswordAuthenticationToken {
    constructor(
        principal: String,
        credentials: String?,
        authorities: Collection<GrantedAuthority>
    ) : super(principal, credentials, authorities)

    constructor(
        principal: String,
        credentials: String
    ) : super(principal, credentials)


}

