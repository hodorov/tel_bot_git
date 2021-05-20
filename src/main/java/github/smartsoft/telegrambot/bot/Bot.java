package github.smartsoft.telegrambot.bot;

import github.smartsoft.telegrambot.entity.CreateRepository;
import github.smartsoft.telegrambot.entity.Status;
import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.service.*;
import github.smartsoft.telegrambot.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@PropertySource("classpath:telegram.properties")
public class Bot extends TelegramLongPollingBot {
    private final UsernameService usernameService;
    private final CreateRepositoryService createRepositoryService;
    private final AccessRepositoryService accessRepositoryService;
    private final GithubService githubService;
    private final MessageTextService messageTextService;
    private final TelegramUtils utils;

    public Bot(UsernameService usernameService,
               CreateRepositoryService createRepositoryService,
               AccessRepositoryService accessRepositoryService,
               GithubService githubService,
               MessageTextService messageTextService,
               TelegramUtils utils) {
        this.usernameService = usernameService;
        this.createRepositoryService = createRepositoryService;
        this.accessRepositoryService = accessRepositoryService;
        this.githubService = githubService;
        this.messageTextService = messageTextService;
        this.utils = utils;
    }

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.teamlead}")
    private Long teamleadChatId;

    @Override
    public void onUpdateReceived(Update update) {
        String sendText = null;
        Message message;
        String text;
        Username username;
        Long chatId = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            message = update.getMessage();
            text = message.getText();
            chatId = message.getFrom().getId().longValue();
            String chatName = message.getFrom().getUserName();
            username = usernameService.getUsername(chatId);
            sendText = messageTextService.messageHandler(message.getText(),
                    username);
            String teamleadChat;
            Integer messageId;

            if (sendText.contains("Логин добавлен")) {
                usernameService.createUsername(chatId, utils.userName(text));
            }
            if (sendText.contains("изменен")) {
                usernameService.updateUsername(chatId, utils.userName(text));
            }
            if (sendText.contains("создан")) {
                createRepositoryService.createRepository(
                        utils.repositoryName(text),
                        username,
                        null,
                        Status.ACCEPTED);
                githubService.createGitRepository(
                        utils.repositoryName(text));
            }
            if (sendText.contains("Доступ к репозиторию")) {
                accessRepositoryService.accessRepository(
                        utils.repositoryName(text),
                        username,
                        null,
                        Status.ACCEPTED);
                githubService.accessGitRepository(
                        utils.repositoryName(text));
            }
            if (sendText.contains("Запрос на создание зарегистрирован")) {
                teamleadChat = utils.teamleadText(
                        "Создание репозитория", text, username, chatName);
                messageId =  sendMessage(
                        teamleadChat,
                        teamleadChatId,
                        utils.createInlineKeyboardMarkup("crOne", "crTwo"));
                createRepositoryService.createRepository(
                        utils.repositoryName(text),
                        username,
                        messageId,
                        Status.WAIT);
            }
            if (sendText.contains("Запрос на доступ зарегистрирован")) {
                teamleadChat = utils.teamleadText(
                        "Доступ к репозиторию", text, username, chatName);
                messageId =  sendMessage(
                        teamleadChat,
                        teamleadChatId,
                        utils.createInlineKeyboardMarkup("acOne", "acTwo"));
                accessRepositoryService.accessRepository(
                        utils.repositoryName(text),
                        username,
                        messageId,
                        Status.WAIT);
            }
        } else if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
            text = update.getCallbackQuery().getData();
            Integer messageId = message.getMessageId();
            CreateRepository createRepository = createRepositoryService
                    .getCreateRepository(messageId);
            username = createRepository.getTelegramId();
            chatId = username.getTelegramId();
            String repositoryName = createRepository.getRepo();
            String teamleadText = null;

            if (text.equals("crYes")) {
                githubService.createGitRepository(repositoryName);
                createRepositoryService.updateCreateRepositoryByStatus(
                        messageId,
                        Status.ACCEPTED);
                teamleadText = "Создание репозитория " + repositoryName + " принято";
                sendText = "Репозиторий " + repositoryName + " создан";
            }
            if (text.equals("crNo")) {
                createRepositoryService.updateCreateRepositoryByStatus(
                        messageId,
                        Status.REJECTED);
                teamleadText = "Создание репозитория " + repositoryName + " отклонено";
                sendText = "В создании репозитория " + repositoryName + " отказано";
            }
            if (text.equals("acYes")) {
                githubService.accessGitRepository(repositoryName);
                accessRepositoryService.updateAccessRepositoryByStatus(
                        messageId,
                        Status.ACCEPTED);
                teamleadText = "Доступ к " + repositoryName + " предоставлен";
                sendText = "Доступ к репозиторию " + repositoryName + " предоставлен";
            }
            if (text.equals("crNo")) {
                accessRepositoryService.updateAccessRepositoryByStatus(
                        messageId,
                        Status.REJECTED);
                teamleadText = "Доступ к " + repositoryName + " отклонен";
                sendText = "В доступе к репозиторию " + repositoryName + " отказано";
            }
            if (teamleadText != null) {
                editMessage(teamleadText, messageId);
            }
        }
        if (chatId != null && sendText != null && !sendText.isEmpty()) {
            sendMessage(sendText, chatId, null);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private Integer sendMessage(String sendText,
                                long chatId,
                                InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage
                .setChatId(chatId)
                .setText(sendText);
        if (markup != null) {
            sendMessage.setReplyMarkup(markup);
        }
        try {
            return execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            log.debug(e.toString());
        }
        return null;
    }

    private void editMessage(String sendText,
                             Integer messageId) {
        System.out.println(sendText);
        System.out.println(messageId);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText
                .setMessageId(messageId)
                .setChatId(teamleadChatId)
                .setText(sendText);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.debug(e.toString());
        }
    }
}