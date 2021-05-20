package github.smartsoft.telegrambot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:github.properties")
public class GithubService {
    RestTemplate template = new RestTemplate();
    Map<String, String> body = new HashMap<>();
    HttpEntity<?> entity = new HttpEntity<>(body, getHeaders());

    @Value("${repository.token}")
    private String botToken;

    public void createGitRepository(String repoName) {
        body.put("name", repoName);
        body.put("private", "true");
        template.postForObject(
                "https://api.github.com/user/repos",
                entity,
                Object.class);
    }

    public void accessGitRepository(String repoName) {
        //body.put("permission", "admin");
        template.put(
                "https://api.github.com/repos/RinatWorker/blogouter/collaborators/Renatko91",
                entity);
    }

    public boolean existsAccessGitRepository(String gitName, String repoName) {
        HashMap<String, String> accessit = template.getForObject(
                "https://api.github.com/repos/RinatWorker/{r}/collaborators/{g}/permission?access_token={q}",
                HashMap.class,
                repoName,
                gitName,
                botToken);
        if (!accessit.get("permission").equals("none")) {
            return true;
        }
        return false;
    }

    public boolean existsGitRepository(String repoName) {
        List<HashMap<String, String>> repoGit = template.getForObject(
                "https://api.github.com/user/repos?access_token={q}",
                List.class,
                botToken);
        if (repoGit != null && !repoGit.isEmpty()) {
            for (HashMap<String, String> repo : repoGit) {
                if (repo.get("name").equals(repoName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean existsGitUsername(String username) {
        try {
            HashMap<String, String> userGit = template.getForObject(
                    "https://api.github.com/users/" + username,
                    HashMap.class);
            if (!userGit.containsKey("message")) {
                return true;
            }
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
        return false;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization",
                "token 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");

        return headers;
    }
}
