package my.project.codeguard.controller.otpcode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import my.project.codeguard.dto.OtpCodeConfigDTO;
import my.project.codeguard.entity.OtpCodeConfig;
import my.project.codeguard.service.otp.OtpCodeConfigService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/otp/config")
@Tag(name = "Конфигурация OTP", description = "Управление настройками OTP кодов")
public class OtpCodeConfigController {
    private final OtpCodeConfigService otpCodeConfigService;
    private final ModelMapper modelMapper;


    public OtpCodeConfigController(OtpCodeConfigService otpCodeConfigService, ModelMapper modelMapper) {
        this.otpCodeConfigService = otpCodeConfigService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Получить текущую конфигурацию",
            description = "Возвращает текущие настройки генерации OTP кодов. Требуется роль ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфигурация получена",
                    content = @Content(schema = @Schema(implementation = OtpCodeConfigDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content()
            )
    })

    @GetMapping
    public ResponseEntity<?> getConfig() {
        var config = otpCodeConfigService.getCurrentConfig();
        return ResponseEntity.ok(modelMapper.map(config, OtpCodeConfigDTO.class));
    }


    @Operation(
            summary = "Изменить конфигурацию",
            description = "Обновляет настройки генерации OTP кодов. Требуется роль ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфигурация обновлена",
                    content = @Content(schema = @Schema(implementation = OtpCodeConfigDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content()
            )
    })
    @PatchMapping
    public ResponseEntity<?> changeConfig(@Valid @RequestBody OtpCodeConfigDTO otpCodeConfigDTO) {
        if (otpCodeConfigDTO.getLifetimeInSeconds() == null &&
                otpCodeConfigDTO.getLength() == null) {
            throw new IllegalArgumentException("Должно быть указано хотя бы одно поле для обновления");
        }
        if (otpCodeConfigDTO.getLifetimeInSeconds() != null) {
            otpCodeConfigService.setLifetime(otpCodeConfigDTO.getLifetimeInSeconds());
        }
        if (otpCodeConfigDTO.getLength() != null) {
            otpCodeConfigService.setLength(otpCodeConfigDTO.getLength());
        }
        var currentConfigDTO = convertToOtpCodeConfigDTO(otpCodeConfigService.getCurrentConfig());
        return ResponseEntity.ok(currentConfigDTO);
    }

    private OtpCodeConfigDTO convertToOtpCodeConfigDTO(OtpCodeConfig config) {
        return modelMapper.map(config, OtpCodeConfigDTO.class);
    }
}
