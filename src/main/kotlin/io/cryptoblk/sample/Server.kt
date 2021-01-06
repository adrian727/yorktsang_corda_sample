package io.cryptoblk.sample

import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.autoconfigure.SpringBootApplication


/**
 * Our Spring Boot application.
 */
@SpringBootApplication
open class Server {
    companion object {
        /**
         * Main entry point to the application
         */
        @JvmStatic fun main(args: Array<String>) {
            val app = SpringApplication(Server::class.java)
            app.setBannerMode(Banner.Mode.OFF)
            app.webApplicationType = SERVLET
            app.run(*args)
        }
    }
}

/**
 * Starts our Spring Boot application.
 */
fun main(args: Array<String>) {
    val app = SpringApplication(Server::class.java)
    app.setBannerMode(Banner.Mode.OFF)
    app.webApplicationType = SERVLET
    app.run(*args)
}