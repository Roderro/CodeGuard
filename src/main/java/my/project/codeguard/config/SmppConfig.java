package my.project.codeguard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmppConfig {

    @Bean
    @ConfigurationProperties(prefix = "smpp")
    public SmppProperties smppProperties() {
        return new SmppProperties();
    }

    @Data
    public static class SmppProperties {
        private String host;
        private int port;
        private String systemId;
        private String password;
        private String systemType;
        private String sourceAddr;
        private int connectionTimeout;
        private long receiveTimeout;
        private long commsTimeout;
    }
}
