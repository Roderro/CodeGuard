package my.project.codeguard.service.otp;

import lombok.extern.slf4j.Slf4j;
import my.project.codeguard.dto.OtpGenerationResponse;
import my.project.codeguard.dto.OtpValidationResponse;
import my.project.codeguard.entity.Operation;
import my.project.codeguard.entity.OtpCode;
import my.project.codeguard.entity.OtpCodeConfig;
import my.project.codeguard.repository.OtpCodeRepository;
import my.project.codeguard.service.delivery.DeliveryChannel;
import my.project.codeguard.service.delivery.DeliveryChannelFactory;
import my.project.codeguard.service.user.UserService;
import my.project.codeguard.util.enums.OtpCodeStatus;
import my.project.codeguard.util.enums.OtpDeliveryStatus;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
@Service
@Slf4j
public class OtpCodeService {
    private final OtpCodeRepository otpCodeRepository;
    private final OtpCodeConfigService otpCodeConfigService;


    public OtpCodeService(OtpCodeRepository otpCodeRepository, OtpCodeConfigService otpCodeConfigService) {
        this.otpCodeRepository = otpCodeRepository;
        this.otpCodeConfigService = otpCodeConfigService;
    }


    @Transactional
    public OtpCode generateOtp(Operation operation) {
        OtpCodeConfig config = otpCodeConfigService.getCurrentConfig();
        String code = generateRandomCode(config.getLength());
        // Создаем запись OTP
        OtpCode otpCode = new OtpCode();
        otpCode.setOperation(operation);
        otpCode.setCode(code);
        otpCode.setOtpCodeStatus(OtpCodeStatus.ACTIVE);
        otpCode.setExpiresAt(LocalDateTime.now().plusSeconds(config.getLifetimeInSeconds()));
        return otpCodeRepository.save(otpCode);
    }

    private String generateRandomCode(Integer length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


    @Transactional
    public OtpValidationResponse validateOtp(Long userid, String operationId, String gottenCode) {
        Optional<OtpCode> otpCodeOpt = otpCodeRepository
                .findByOperation_User_IdAndOperation_OperationId(userid, operationId);

        if (otpCodeOpt.isEmpty()) {
            return OtpValidationResponse.invalid("NOT_FOUND");
        }

        OtpCode otpCode = otpCodeOpt.get();

        // Проверяем статус
        if (otpCode.getOtpCodeStatus() != OtpCodeStatus.ACTIVE) {
            return OtpValidationResponse.invalid(otpCode.getOtpCodeStatus().toString());
        }

        // Проверяем код
        if (!otpCode.getCode().equals(gottenCode)) {
            return OtpValidationResponse.invalid("INVALID");
        }

        // Проверяем срок действия
        if (LocalDateTime.now().isAfter(otpCode.getExpiresAt())) {
            otpCode.setOtpCodeStatus(OtpCodeStatus.EXPIRED);
            otpCodeRepository.save(otpCode);
            return OtpValidationResponse.invalid("EXPIRED");
        }

        // Успешная валидация
        otpCode.setOtpCodeStatus(OtpCodeStatus.USED);
        otpCode.setUsedAt(LocalDateTime.now());
        otpCodeRepository.save(otpCode);

        return OtpValidationResponse.valid();
    }
}
