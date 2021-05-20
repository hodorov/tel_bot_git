package github.smartsoft.telegrambot.repository;

import github.smartsoft.telegrambot.entity.AccessRepository;
import github.smartsoft.telegrambot.entity.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepositoryCRUD<T extends BaseRepository> extends JpaRepository<T, Long> {
    Optional<T> getByMessageId(@NotNull long messageId);
}
