package my.project.codeguard.repository;


import my.project.codeguard.entity.OtpCodeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpCodeConfigRepository extends JpaRepository<OtpCodeConfig,Integer> {
}
