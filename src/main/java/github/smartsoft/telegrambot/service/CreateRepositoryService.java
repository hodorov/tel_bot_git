package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.CreateRepository;
import github.smartsoft.telegrambot.entity.Status;
import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.repository.CreateRepositoryCRUD;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRepositoryService {
    private final CreateRepositoryCRUD createRepositoryCRUD;

    public CreateRepositoryService(CreateRepositoryCRUD createRepositoryCRUD) {
        this.createRepositoryCRUD = createRepositoryCRUD;
    }

    public void createRepository(String repoName,
                                 Username username,
                                 Integer messageId,
                                 Status status) {
        CreateRepository createRepository = new CreateRepository();
        createRepository.setRepo(repoName);
        createRepository.setTelegramId(username);
        if (messageId != null) {
            createRepository.setMessageId(messageId);
        }
        createRepository.setStatus(status);

        createRepositoryCRUD.save(createRepository);
    }

    @Transactional
    public CreateRepository getCreateRepository(long messageId) {
        if (createRepositoryCRUD.getByMessageId(messageId).isPresent()) {
            return createRepositoryCRUD.getByMessageId(messageId).get();
        }

        return null;
    }

    public void updateCreateRepositoryByStatus(long messageId, Status status) {
        if (createRepositoryCRUD.getByMessageId(messageId).isPresent()) {
            CreateRepository createRepository = createRepositoryCRUD.getByMessageId(messageId).get();
            createRepository.setStatus(status);
            createRepositoryCRUD.save(createRepository);
        }
    }
}
