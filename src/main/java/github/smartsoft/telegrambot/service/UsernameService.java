package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.repository.UsernameCRUD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsernameService {
    @Autowired
    UsernameCRUD usernameCRUD;

    public void createUsername(long chatId, String gitName) {
        Username username = new Username();
        username.setTelegramId(chatId);
        username.setGithubUsername(gitName);

        usernameCRUD.save(username);
    }

    public Username getUsername(long telegramId) {
        if (usernameCRUD.getByTelegramId(telegramId).isPresent()) {
            return usernameCRUD.getByTelegramId(telegramId).get();
        }
        return null;
    }

    public void updateUsername(long telegramId, String name) {
        if (usernameCRUD.getByTelegramId(telegramId).isPresent()) {
            Username username = usernameCRUD.getByTelegramId(telegramId).get();
            username.setGithubUsername(name);
            usernameCRUD.save(username);
        }
    }
}
