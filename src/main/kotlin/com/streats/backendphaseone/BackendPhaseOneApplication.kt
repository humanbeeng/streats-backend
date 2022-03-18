package com.streats.backendphaseone

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition
class BackendPhaseOneApplication

fun main(args: Array<String>) {
    runApplication<BackendPhaseOneApplication>(*args)
}
