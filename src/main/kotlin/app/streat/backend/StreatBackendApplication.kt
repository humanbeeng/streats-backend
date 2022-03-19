package app.streat.backend

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition
class StreatBackendApplication

fun main(args: Array<String>) {
    runApplication<StreatBackendApplication>(*args)
}
