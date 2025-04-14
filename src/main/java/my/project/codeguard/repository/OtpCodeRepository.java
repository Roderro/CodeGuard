package my.project.codeguard.repository;

import my.project.codeguard.entity.OtpCode;
import my.project.codeguard.util.enums.OtpCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    List<OtpCode> findByOtpCodeStatus(OtpCodeStatus status);

    Optional<OtpCode> findByOperation_User_IdAndOperation_OperationId(Long userId, String operationId);
}
