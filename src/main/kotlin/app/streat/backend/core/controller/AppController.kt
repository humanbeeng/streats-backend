package app.streat.backend.core.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppController {


    @GetMapping("/ping")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }
}