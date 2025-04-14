package my.project.codeguard.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(Operation.OperationId.class)
public class Operation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Column(name = "operation_id", nullable = false, length = 100)
    private String operationId;

    @Email(message = "Email should be valid")
    @Column(nullable = true)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    @Column(nullable = true)
    private String phone;


    @Column(nullable = true, length = 100)
    private String tgUsername;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class OperationId implements Serializable {
        private Long user;
        private String operationId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OperationId that = (OperationId) o;
            return Objects.equals(user, that.user) &&
                    Objects.equals(operationId, that.operationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, operationId);
        }
    }
}

