package com.streats.backendphaseone.auth.security.config

import com.streats.backendphaseone.auth.security.filters.AuthorizationFilter
import com.streats.backendphaseone.auth.service.StreatsUserService
import com.streats.backendphaseone.auth.util.JWTUtil
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@EnableWebSecurity
@Configuration
class AuthConfig(private val streatsUserService: StreatsUserService) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(streatsUserService)
    }

    override fun configure(http: HttpSecurity) {
        http.cors().disable()
        http.csrf().disable()
        http.addFilterBefore(AuthorizationFilter(JWTUtil()), BasicAuthenticationFilter::class.java)

            .authorizeRequests().mvcMatchers("/admin/**").hasRole("ADMIN")
            .and()
            .authorizeRequests().mvcMatchers("/shops/**").hasAnyRole("USER")
            .and()
            .authorizeRequests().mvcMatchers("/cart/**").hasRole("USER")
            .and()
            .authorizeRequests().mvcMatchers("/auth/**").permitAll()
            .and()
            .authorizeRequests().mvcMatchers("/vendor/**").hasAnyRole("ADMIN", "VENDOR")
    }
}