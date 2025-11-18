package com.example.usermanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Bean
    public OpenAPI userManagementOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl(baseUrl);
        localServer.setDescription("Server URL");

        Contact contact = new Contact();
        contact.setName("User Management Team");
        contact.setEmail("support@usermanagement.com");

        Info info = new Info()
                .title("User Management Service API")
                .version("1.0.0")
                .contact(contact)
                .description("RESTful API for managing users with full CRUD operations, pagination, filtering, and Kafka event streaming");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
