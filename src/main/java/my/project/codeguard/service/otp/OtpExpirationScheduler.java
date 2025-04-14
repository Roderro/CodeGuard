package my.project.codeguard.service.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.codeguard.entity.OtpCode;
import my.project.codeguard.repository.OtpCodeRepository;
import my.project.codeguard.util.enums.OtpCodeStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OtpExpirationScheduler {
    private final OtpCodeRepository otpCodeRepository;

    public OtpExpirationScheduler(OtpCodeRepository otpCodeRepository) {
        this.otpCodeRepository = otpCodeRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void markExpiredOtp() {
        LocalDateTime now = LocalDateTime.now();
        List<OtpCode> activeOtps = otpCodeRepository.findByOtpCodeStatus(OtpCodeStatus.ACTIVE);

        activeOtps.stream()
                .filter(otp -> now.isAfter(otp.getExpiresAt()))
            .forEach(otp -> {
            otp.setOtpCodeStatus(OtpCodeStatus.EXPIRED);
            otpCodeRepository.save(otp);
            log.debug("Marked OTP {} as EXPIRED", otp.getId());
        });

        log.info("Checked {} active OTPs for expiration", activeOtps.size());
    }
}