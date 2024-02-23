package com.milko.user_provider.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "User Provider Api",
                description = "This microservice is responsible for handling users, addresses, etc.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Milko Eugene"
                )
        )
)
public class OpenApiConfig {

}