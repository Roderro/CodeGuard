package my.project.codeguard.repository;

import my.project.codeguard.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    Optional<Operation> findByUserIdAndOperationId(Long userId, String operationId);
}
