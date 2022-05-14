package app.streat.backend.auth.security.config

import app.streat.backend.auth.security.filters.AuthorizationFilter
import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.auth.utils.AuthConstants.ROLE_ADMIN
import app.streat.backend.auth.utils.AuthConstants.ROLE_USER
import app.streat.backend.auth.utils.AuthConstants.ROLE_VENDOR
import app.streat.backend.core.util.JWTUtil
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@EnableWebSecurity
@Configuration
class AuthConfig(
    private val streatsUserService: StreatsUserService,
    private val jwtUtil: JWTUtil
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(streatsUserService)
    }

    //    TODO : Restore ROLES
//    TODO : Remove Test
    override fun configure(http: HttpSecurity) {
        http.cors().disable()
        http.csrf().disable()
        http.addFilterBefore(AuthorizationFilter(jwtUtil), BasicAuthenticationFilter::class.java)
            .authorizeRequests().mvcMatchers("/admin/**").hasRole(ROLE_ADMIN)
            .and()
            .authorizeRequests().mvcMatchers("/shop/**").hasAnyRole(ROLE_USER, ROLE_VENDOR, ROLE_ADMIN)
            .and()
            .authorizeRequests().mvcMatchers("/cart/**").hasAnyRole(ROLE_ADMIN, ROLE_USER)
            .and()
            .authorizeRequests().mvcMatchers("/vendor/login").permitAll()
            .and()
            .authorizeRequests().mvcMatchers("/vendor/**").hasAnyRole(ROLE_ADMIN, ROLE_VENDOR)
            .and()
            .authorizeRequests().mvcMatchers("/test/**").hasAnyRole(ROLE_USER, ROLE_ADMIN, ROLE_VENDOR)
    }
}