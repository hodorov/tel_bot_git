package github.smartsoft.telegrambot.service;

import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.utils.TelegramUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageTextService {
    private final GithubService githubService;
    private final TelegramUtils utils;

    public MessageTextService(GithubService githubService, TelegramUtils utils) {
        this.githubService = githubService;
        this.utils = utils;
    }

    public String messageHandler(String text, Username username) {
        if (text.startsWith("/start")) {
            return "Укажите логин на гитхабе с помощью команды /username";
        }
        if (text.startsWith("/username ")) {
            if (username == null) {
                return "Логин добавлен";
            }
            if (!githubService.existsGitUsername(utils.userName(text))) {
                return "На github нет такого пользователя";
            }
            if (username.getGithubUsername().equals(utils.userName(text))) {
                return "Такой логин уже существует";
            }
            return String.format(
                    "Логин %s изменен на %s",
                    username.getGithubUsername(),
                    utils.userName(text));
        }
        if (text.startsWith("/create ") || text.startsWith("/access ")) {
            if (username == null) {
                return "Логин не существует, воспользуйтесь командой /username";
            }
        }
        if (text.startsWith("/create ")) {
            if (githubService.existsGitRepository(utils.repositoryName(text))) {
                return "Репозиторий уже существует";
            }
            if (username.isTeamlead()) {
                return "Репозиторий " + utils.repositoryName(text) + " создан";
            }
            return "Запрос на создание зарегистрирован";
        }
        if (text.startsWith("/access ")) {
            if (githubService.existsAccessGitRepository(
                    username.getGithubUsername(),
                    utils.repositoryName(text))) {
                return "Доступ уже предоставлен";
            }
            if (username.isTeamlead()) {
                return "Доступ к репозиторию " + utils.repositoryName(text) + " предоставлен";
            }
            return "Запрос на доступ зарегистрирован";
        }
        return "";
    }
}
