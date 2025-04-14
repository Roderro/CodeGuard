package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Schema(description = "Данные операции для генерации OTP")
@Data
public class OperationDTO {


    @Schema(description = "Уникальный идентификатор операции",
            example = "login-123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 1, max = 100)
    private String operationId;

    @Schema(description = "Email для отправки кода", example = "user@example.com")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Телефон для отправки кода", example = "+79001234567")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;

    @Schema(description = "Telegram username для отправки кода, пользователь должен стартануть бота",
            example = "username")
    @Size(min = 1, max = 100)
    private String tgUsername;
}
