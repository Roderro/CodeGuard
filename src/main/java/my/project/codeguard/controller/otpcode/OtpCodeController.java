package my.project.codeguard.controller.otpcode;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import my.project.codeguard.dto.OperationDTO;
import my.project.codeguard.dto.OtpGenerationResponse;
import my.project.codeguard.dto.OtpValidationResponse;
import my.project.codeguard.entity.Operation;
import my.project.codeguard.entity.OtpCode;
import my.project.codeguard.entity.User;
import my.project.codeguard.security.UserDetailsImpl;
import my.project.codeguard.service.delivery.DeliveryService;
import my.project.codeguard.service.operation.OperationService;
import my.project.codeguard.service.otp.OtpCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/otp")
@Tag(name = "OTP коды", description = "Генерация и валидация OTP кодов")
public class OtpCodeController {
    private final OtpCodeService otpCodeService;
    private final OperationService operationService;
    private final DeliveryService deliveryService;


    public OtpCodeController(OtpCodeService otpCodeService, OperationService operationService, DeliveryService deliveryService) {
        this.otpCodeService = otpCodeService;
        this.operationService = operationService;
        this.deliveryService = deliveryService;
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Сгенерировать OTP код",
            description = "Создает OTP код и отправляет его на указанные каналы. Требуется авторизация"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Код отправлен на все каналы",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OtpGenerationResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "SuccessAllChannels",
                                            summary = "Успешная отправка на все каналы",
                                            value = """
                        {
                            "code": "123456",
                            "status": "SENT",
                            "successfulChannels": ["SMS", "EMAIL", "TELEGRAM"],
                            "failedChannels": {},
                            "expiresAt": "2023-12-31T23:59:59"
                        }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "207",
                    description = "Код отправлен на часть каналов",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OtpGenerationResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "PartialSuccess",
                                            summary = "Частичная отправка",
                                            value = """
                        {
                            "code": "654321",
                            "status": "PARTIAL",
                            "successfulChannels": ["SMS", "TELEGRAM"],
                            "failedChannels": {
                                "EMAIL": "SMTP server unavailable"
                            },
                            "expiresAt": "2023-12-31T23:59:59"
                        }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Ошибка отправки на все каналы",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OtpGenerationResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "FailedAllChannels",
                                            summary = "Ошибка отправки на все каналы",
                                            value = """
                        {
                            "code": "987654",
                            "status": "FAILED",
                            "successfulChannels": [],
                            "failedChannels": {
                                "SMS": "Invalid phone number",
                                "EMAIL": "SMTP server unavailable",
                                "TELEGRAM": "Bot blocked by user"
                            },
                            "expiresAt": "2023-12-31T23:59:59"
                        }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Требуется авторизация",
                    content = @Content()
            )
    })
    @PostMapping("/generate")
    public ResponseEntity<?> generateOtpCode(@Valid @RequestBody OperationDTO operationDTO,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Operation operation = operationService.createOperation(operationDTO, user);
        OtpCode otpCode = otpCodeService.generateOtp(operation);
        OtpGenerationResponse response = deliveryService.sentOtpCode(otpCode, operation);

        return switch (response.getStatus()) {
            case SENT -> ResponseEntity.ok(response);
            case PARTIAL -> ResponseEntity.status(HttpStatus.MULTI_STATUS)
                    .body(response);
            case FAILED -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(response);
        };
    }



    @io.swagger.v3.oas.annotations.Operation(
            summary = "Проверить OTP код",
            description = "Валидирует введенный пользователем OTP код. Требуется авторизация"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Код верный",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OtpValidationResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            summary = "Успешная валидация",
                                            value = """
                        {
                            "valid": true,
                            "status": "VERIFIED"
                        }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Код неверный или устарел",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OtpValidationResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Expired",
                                            summary = "Код устарел",
                                            value = """
                        {
                            "valid": false,
                            "status": "EXPIRED"
                        }"""
                                    ),
                                    @ExampleObject(
                                            name = "Used",
                                            summary = "Код уже использован",
                                            value = """
                        {
                            "valid": false,
                            "status": "USED"
                        }"""
                                    ),
                                    @ExampleObject(
                                            name = "Invalid",
                                            summary = "Неверный код",
                                            value = """
                        {
                            "valid": false,
                            "status": "INVALID"
                        }"""
                                    ),
                                    @ExampleObject(
                                            name = "Notfound",
                                            summary = "Операция с данным id не найдена",
                                            value = """
                        {
                            "valid": false,
                            "status": "NOT_FOUND"
                        }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Требуется авторизация",
                    content = @Content()
            )
    })
    @GetMapping("/validate")
    public ResponseEntity<OtpValidationResponse> validateOtp(
            @RequestParam String operationId,
            @RequestParam String code,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        OtpValidationResponse response = otpCodeService.validateOtp(
                userDetails.getUser().getId(),
                operationId,
                code
        );
        return response.isValid() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}
