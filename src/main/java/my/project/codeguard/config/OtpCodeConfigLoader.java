package my.project.codeguard.config;

import my.project.codeguard.service.otp.OtpCodeConfigService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OtpCodeConfigLoader implements CommandLineRunner {
    private final OtpCodeConfigService otpCodeConfigService;

    public OtpCodeConfigLoader(OtpCodeConfigService otpCodeConfigService) {
        this.otpCodeConfigService = otpCodeConfigService;
    }

    @Override
    public void run(String... args){
        if (otpCodeConfigService.isEmpty()) {
            otpCodeConfigService.setDefaultConfig();
        }
    }
}
