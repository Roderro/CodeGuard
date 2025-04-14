package my.project.codeguard.repository;

import my.project.codeguard.entity.TelegramUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByUsername(String username);
}
