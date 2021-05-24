package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.AccessRequest;
import github.smartsoft.telegrambot.repository.BaseRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessRequestService extends BaseRequestService<AccessRequest> {
    public AccessRequestService(
            BaseRequestRepository<AccessRequest> requestRepository) {
        super(requestRepository, AccessRequest.class);
    }
}