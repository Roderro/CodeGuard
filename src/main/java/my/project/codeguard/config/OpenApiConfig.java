package my.project.codeguard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CodeGuard API",
                version = "1.0",
                description = "API для двухфакторной аутентификации и управления OTP кодами"
        )
)
public class OpenApiConfig {
}
