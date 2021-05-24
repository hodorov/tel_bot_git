package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.CreateRequest;
import github.smartsoft.telegrambot.repository.BaseRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRequestService extends BaseRequestService<CreateRequest> {
    public CreateRequestService(
            BaseRequestRepository<CreateRequest> requestRepository) {
        super(requestRepository, CreateRequest.class);
    }
}
