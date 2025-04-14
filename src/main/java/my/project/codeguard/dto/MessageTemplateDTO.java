package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Шаблон сообщения с OTP кодом")
public class MessageTemplateDTO {

    @Schema(
            description = "Текст шаблона, должен содержать {code}",
            example = "Ваш код: {code}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Template cannot be blank")
    @Pattern(regexp = ".*\\{code\\}.*", message = "Template must contain '{code}' substring")
    private String template;
}
