package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.BaseRequest;
import github.smartsoft.telegrambot.entity.Status;
import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.repository.BaseRequestRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class BaseRequestService<T extends BaseRequest> {
    private final BaseRequestRepository<T> requestRepository;
    private final Class<T> tClass;

    public BaseRequestService(BaseRequestRepository<T> requestRepository, Class<T> tClass) {
        this.requestRepository = requestRepository;
        this.tClass = tClass;
    }

    @SneakyThrows
    public void create(String repoName,
                       Username username,
                       Integer messageId,
                       Status status) {
        T request = tClass.getDeclaredConstructor().newInstance();
        request.setRepo(repoName);
        request.setTelegramId(username);
        if (messageId != null) {
            request.setMessageId(messageId);
        }
        request.setStatus(status);

        requestRepository.save(request);
    }

    @Transactional
    public T get(long messageId) {
        if (requestRepository.getByMessageId(messageId).isPresent()) {
            return requestRepository.getByMessageId(messageId).get();
        }

        return null;
    }

    public void update(long messageId, Status status) {
        if (requestRepository.getByMessageId(messageId).isPresent()) {
            T baseRepository = requestRepository.getByMessageId(messageId).get();
            baseRepository.setStatus(status);
            requestRepository.save(baseRepository);
        }
    }
}
