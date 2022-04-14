package app.streat.backend.auth.security.filters

import app.streat.backend.auth.security.entities.FirebaseAuthenticationToken
import app.streat.backend.core.util.JWTUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter(private val jwtUtil: JWTUtil) : OncePerRequestFilter() {

    /**
     * Same contract as for `doFilter`, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See [.shouldNotFilterAsyncDispatch] for details.
     *
     * Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader.isNullOrBlank()) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid Access Token")
            return
        }



        if (authorizationHeader.isNotBlank() && authorizationHeader.startsWith("Bearer ")) {
            val incomingAccessToken = authorizationHeader.substring("Bearer ".length)

            if (jwtUtil.verifyAccessToken(incomingAccessToken).not()) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid Access Token")
                return
            }
            val decodedToken = jwtUtil.getDecodedToken(incomingAccessToken)

            val roles = decodedToken.getClaim("roles").asList(String::class.java)

            val authorities: MutableList<GrantedAuthority> = mutableListOf()

            roles.forEach { authorities.add(SimpleGrantedAuthority("ROLE_${it}")) }

            val principal = decodedToken.subject

            val firebaseAuthenticationToken =
                FirebaseAuthenticationToken(principal, null, authorities)

            SecurityContextHolder.getContext().authentication = firebaseAuthenticationToken

            filterChain.doFilter(request, response)
        } else
            filterChain.doFilter(request, response)

    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath.equals("/auth")
                || request.servletPath.equals("/orders/callback")
                || request.servletPath.equals("/auth/admin")


    }
}