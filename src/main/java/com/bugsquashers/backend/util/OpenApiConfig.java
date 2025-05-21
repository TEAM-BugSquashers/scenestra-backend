package com.bugsquashers.backend.util;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "SCENESTRA API 명세서",
                description = "API 명세서",
                version = "v1",
                contact = @Contact(
                        name = "BugSquashers"

                )
        )
)

@Configuration
public class OpenApiConfig {

}