package my.project.codeguard.util.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum Role {
    @Schema(description = "Обычный пользователь")
    ROLE_USER,
    @Schema(description = "Администратор системы")
    ROLE_ADMIN
}