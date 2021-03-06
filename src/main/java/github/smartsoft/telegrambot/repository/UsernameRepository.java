package github.smartsoft.telegrambot.repository;

import github.smartsoft.telegrambot.entity.Username;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface UsernameRepository extends JpaRepository<Username, Long> {
    Optional<Username> getByTelegramId(@NotNull long telegramId);
}
