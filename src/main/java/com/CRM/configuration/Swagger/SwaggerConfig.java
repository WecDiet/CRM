package com.CRM.configuration.Swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Info info = new Info()
                .title("CRM API")
                .version("1.0.0")
                .description("API documentation for the CRM system")
                .contact(new Contact().name("Huynh Quoc Viet"));

        return new OpenAPI()
                .info(info)
                .addServersItem(devServer);

    }

}
