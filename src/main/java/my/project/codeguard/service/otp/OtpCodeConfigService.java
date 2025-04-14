package my.project.codeguard.service.otp;

import my.project.codeguard.entity.OtpCodeConfig;
import my.project.codeguard.repository.OtpCodeConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpCodeConfigService {
    private final OtpCodeConfigRepository otpCodeConfigRepository;

    public OtpCodeConfigService(OtpCodeConfigRepository otpCodeConfigRepository) {
        this.otpCodeConfigRepository = otpCodeConfigRepository;
    }

    @Transactional(readOnly = true)
    public OtpCodeConfig getCurrentConfig() {
        return otpCodeConfigRepository.getReferenceById(1);
    }

    @Transactional()
    public void setLifetime(long lifetime) {
        var config = otpCodeConfigRepository.getReferenceById(1);
        config.setLifetimeInSeconds(lifetime);
    }

    @Transactional()
    public void setLength(int length) {
        var config = otpCodeConfigRepository.getReferenceById(1);
        config.setLength(length);
    }

    public boolean isEmpty() {
        return otpCodeConfigRepository.count() == 0;
    }

    public void setDefaultConfig() {
        var config = new OtpCodeConfig(6, 30L);
        otpCodeConfigRepository.save(config);
    }
}
