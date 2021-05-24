package github.smartsoft.telegrambot.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:github.properties")
public class GithubService {
    @Value("${git.token}")
    private String gitToken;
    @Value("${git.name}")
    private String gitName;

    RestTemplate template = new RestTemplate();
    Map<String, String> body = new HashMap<>();
    HttpEntity<?> entity = new HttpEntity<>(body, getHeaders());

    public GithubService() {
    }

    public void createGitRepository(String repoName) {
        body.put("name", repoName);
        body.put("private", "true");
        template.postForObject(
                "https://api.github.com/user/repos",
                entity,
                Object.class);
    }

    public void accessGitRepository(String repoName, String gitUserName) {
        //body.put("permission", "admin");
        template.put(
                "https://api.github.com/repos/{g}/{r}/collaborators/{u}",
                entity,
                gitName,
                repoName,
                gitUserName);
    }

    public boolean existsAccessGitRepository(String gitUserName, String repoName) {
        HashMap<String, String> accessit = template.getForObject(
                "https://api.github.com/repos/{g}/{r}/collaborators/{u}/permission?access_token={t}",
                HashMap.class,
                gitName,
                repoName,
                gitUserName,
                gitToken);
        if (!accessit.get("permission").equals("none")) {
            return true;
        }
        return false;
    }

    public boolean existsGitRepository(String repoName) {
        List<HashMap<String, String>> repoGit = template.getForObject(
                "https://api.github.com/user/repos?access_token={t}",
                List.class,
                gitToken);
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
                    "https://api.github.com/users/{u}",
                    HashMap.class,
                    username);
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
                "token " + gitToken);
        return headers;
    }
}
