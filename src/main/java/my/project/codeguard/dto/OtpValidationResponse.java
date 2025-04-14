package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


@Schema(description = "Ответ на запрос валидации OTP кода")
@Data
@Builder
public class OtpValidationResponse {
    @Schema(description = """
            Результат валидации OTP кода.
            Если valid=true - код верный,
            если valid=false - код неверный или устарел""",
            example = "true",
            allowableValues = {"true", "false"})
    private boolean valid;

    @Schema(description = "Статус проверки",
            example = "VERIFIED",
            allowableValues = {"VERIFIED", "EXPIRED", "USED", "INVALID", "NOT_FOUND"})
    private String status;

    public static OtpValidationResponse invalid(String status) {
        return OtpValidationResponse.builder()
                .valid(false)
                .status(status)
                .build();
    }

    public static OtpValidationResponse valid() {
        return OtpValidationResponse.builder()
                .valid(true)
                .status("VERIFIED")
                .build();
    }
}