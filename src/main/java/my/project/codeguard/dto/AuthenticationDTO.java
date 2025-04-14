package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.codeguard.util.validation.annotation.UniqueUsername;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные для аутентификации")
public class AuthenticationDTO {

    @Schema(description = "Имя пользователя", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Username cannot be blank")
    @Size(min = 2, max = 50, message = "Username must be between 3 and 50 characters")
    @UniqueUsername
    private String username;

    @Schema(description = "Пароль", example = "securePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8,max = 50, message = "Password must be at least 8 characters")
    private String password;
}
