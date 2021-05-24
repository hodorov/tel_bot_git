package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.MessageText;
import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.utils.TelegramUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageTextService {
    private final GithubService githubService;
    private final TelegramUtils utils;

    public MessageText messageHandler(String text, Username username) {
        if (text.startsWith("/start")) {
            return MessageText.ENTER_LOGIN;
        }
        if (text.startsWith("/username ")) {
            if (username == null) {
                return MessageText.LOGIN_ADDED;
            }
            if (!githubService.existsGitUsername(utils.userName(text))) {
                return MessageText.NOT_GITHUB_USER;
            }
            if (username.getGithubUsername().equals(utils.userName(text))) {
                return MessageText.LOGIN_EXISTS;
            }
            return MessageText.LOGIN_CHANGED;
//            return String.format(
//                    "Логин %s изменен на %s",
//                    username.getGithubUsername(),
//                    utils.userName(text));
        }
        if (text.startsWith("/create ") || text.startsWith("/access ")) {
            if (username == null) {
                return MessageText.LOGIN_NOT_ENTER;
            }
        }
        if (text.startsWith("/create ")) {
            if (githubService.existsGitRepository(utils.repositoryName(text))) {
                return MessageText.REPO_EXISTS;
            }
            if (username.isTeamlead()) {
                return MessageText.REPO_ADDED;
//                return "Репозиторий " + utils.repositoryName(text) + " создан";
            }
            return MessageText.CREATE_REPO_REQ;
        }
        if (text.startsWith("/access ")) {
            if (githubService.existsAccessGitRepository(
                    username.getGithubUsername(),
                    utils.repositoryName(text))) {
                return MessageText.ACCESS_EXISTS;
            }
            if (username.isTeamlead()) {
                return MessageText.ACCESS_ADDED;
            }
            return MessageText.ACCESS_REPO_REQ;
        }
        return MessageText.NONE;
    }
}
