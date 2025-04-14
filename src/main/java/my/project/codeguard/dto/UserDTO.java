package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.codeguard.util.enums.Role;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для представления данных пользователя")
public class UserDTO {

    @Schema(
            description = "Уникальное имя пользователя",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 50
    )
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(
            description = "Роль пользователя в системе",
            example = "ROLE_USER",
            requiredMode = Schema.RequiredMode.REQUIRED,
            implementation = Role.class
    )
    @NotNull(message = "Role cannot be null")
    private Role role;
}
