package com.streats.backendphaseone.core.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import java.io.IOException
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig(private val resourceLoader: ResourceLoader) {

    @PostConstruct
    fun firebaseInit() {
        try {
            val localServiceResource = resourceLoader.getResource("classpath:service-account-file.json")
            val localServiceAccountFile = localServiceResource.inputStream
            val firebaseOptions =
                FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(localServiceAccountFile)).build()

            FirebaseApp.initializeApp(firebaseOptions)
        } catch (e: IOException) {
            throw e
        }
    }
}