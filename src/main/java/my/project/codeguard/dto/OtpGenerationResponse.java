package my.project.codeguard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import my.project.codeguard.util.enums.OtpDeliveryStatus;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@Builder
public class OtpGenerationResponse {

    @Schema(description = "Сгенерированный код", example = "123456")
    private final String code;

    @Schema(
            description = "Статус доставки",
            enumAsRef = true,
            example = "SENT",
            allowableValues = {"SENT", "PARTIAL", "FAILED"}
    )
    private final OtpDeliveryStatus status;

    @Schema(
            description = "Список успешных каналов доставки",
            example = "[\"SMS\", \"EMAIL\"]"
    )
    private final List<String> successfulChannels;

    @Schema(
            description = "Ошибки доставки по каналам (канал: описание ошибки)",
            example = "{\"TELEGRAM\": \"Bot blocked by user\"}"
    )
    private final Map<String, String> failedChannels;

    @Schema(
            description = "Время истечения срока действия кода",
            example = "2023-12-31T23:59:59"
    )
    private final LocalDateTime expiresAt;
}