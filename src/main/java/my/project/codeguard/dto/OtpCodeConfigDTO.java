package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Настройки генерации OTP кодов")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpCodeConfigDTO {


    @Schema(
            description = "Время жизни кода в секундах",
            example = "300",
            minimum = "10",
            maximum = "86400"
    )
    @Min(value = 10, message = "Minimum lifetime is 10 seconds")
    @Max(value = 86400, message = "Maximum lifetime is 86400 seconds (24 hours)")
    private Long lifetimeInSeconds;

    @Schema(
            description = "Длина кода",
            example = "6",
            minimum = "4",
            maximum = "8"
    )
    @Min(value = 4, message = "Minimum length is 4 characters")
    @Max(value = 8, message = "Maximum length is 8 characters")
    private Integer length;
}
