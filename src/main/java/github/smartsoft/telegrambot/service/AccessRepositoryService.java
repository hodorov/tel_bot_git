package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.AccessRepository;
import github.smartsoft.telegrambot.entity.Status;
import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.repository.AccessRepositoryCRUD;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessRepositoryService {
    private final AccessRepositoryCRUD accessRepositoryCRUD;

    public AccessRepositoryService(AccessRepositoryCRUD accessRepositoryCRUD) {
        this.accessRepositoryCRUD = accessRepositoryCRUD;
    }

    public void accessRepository(String repoName,
                                 Username username,
                                 Integer messageId,
                                 Status status) {
        AccessRepository accessRepository = new AccessRepository();
        accessRepository.setRepo(repoName);
        accessRepository.setTelegramId(username);
        if (messageId != null) {
            accessRepository.setMessageId(messageId);
        }
        accessRepository.setStatus(status);

        accessRepositoryCRUD.save(accessRepository);
    }

    @Transactional
    public AccessRepository getAccessRepository(long messageId) {
        if (accessRepositoryCRUD.getByMessageId(messageId).isPresent()) {
            return accessRepositoryCRUD.getByMessageId(messageId).get();
        }

        return null;
    }

    public void updateAccessRepositoryByStatus(long messageId, Status status) {
        if (accessRepositoryCRUD.getByMessageId(messageId).isPresent()) {
            AccessRepository accessRepository = accessRepositoryCRUD.getByMessageId(messageId).get();
            accessRepository.setStatus(status);
            accessRepositoryCRUD.save(accessRepository);
        }
    }
}
