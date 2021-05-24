package github.smartsoft.telegrambot.repository;

import github.smartsoft.telegrambot.entity.CreateRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface CreateRequestRepository extends BaseRequestRepository<CreateRequest> {
}
