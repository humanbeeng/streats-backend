package app.streat.backend.core.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ping")
class AppController {

    @GetMapping
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }
}