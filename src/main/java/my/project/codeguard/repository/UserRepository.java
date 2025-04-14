package my.project.codeguard.repository;


import jakarta.validation.constraints.NotNull;
import my.project.codeguard.entity.User;
import my.project.codeguard.util.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRoleIsNot(@NotNull(message = "Role cannot be null") Role role);
}
