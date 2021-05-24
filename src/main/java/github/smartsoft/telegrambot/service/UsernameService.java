package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.repository.UsernameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsernameService {
    UsernameRepository usernameRepository;

    public void createUsername(long chatId, String gitName) {
        Username username = new Username();
        username.setTelegramId(chatId);
        username.setGithubUsername(gitName);

        usernameRepository.save(username);
    }

    public Username getUsername(long telegramId) {
        if (usernameRepository.getByTelegramId(telegramId).isPresent()) {
            return usernameRepository.getByTelegramId(telegramId).get();
        }
        return null;
    }

    public void updateUsername(long telegramId, String name) {
        if (usernameRepository.getByTelegramId(telegramId).isPresent()) {
            Username username = usernameRepository.getByTelegramId(telegramId).get();
            username.setGithubUsername(name);
            usernameRepository.save(username);
        }
    }
}
