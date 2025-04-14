package my.project.codeguard.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp_code_config")
public class OtpCodeConfig {
    @Id
    private int id = 1;

    private Integer length;

    private Long lifetimeInSeconds;

    public OtpCodeConfig(Integer length, Long lifetimeInSeconds) {
        this.length = length;
        this.lifetimeInSeconds = lifetimeInSeconds;
    }

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
