package github.smartsoft.telegrambot.bot;

import github.smartsoft.telegrambot.entity.CreateRequest;
import github.smartsoft.telegrambot.entity.MessageText;
import github.smartsoft.telegrambot.entity.Status;
import github.smartsoft.telegrambot.entity.Username;
import github.smartsoft.telegrambot.service.*;
import github.smartsoft.telegrambot.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
@PropertySource("classpath:telegram.properties")
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.teamlead}")
    private long teamleadChatId;

    private final UsernameService usernameService;
    private final CreateRequestService createRequestService;
    private final AccessRequestService accessRequestService;
    private final GithubService githubService;
    private final MessageTextService messageTextService;
    private final TelegramUtils utils;

    @Override
    public void onUpdateReceived(Update update) {
        String create = MessageText.valueOf("CREATE_REPO").toString();
        String access = MessageText.valueOf("ACCESS_REPO").toString();
        String teamleadText = null;
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
            MessageText messageText = messageTextService.messageHandler(message.getText(),
                    username);
            sendText = messageText.toString().contains("%s") ?
                    String.format(messageText.toString(), utils.repositoryName(text)) :
                    messageText.toString();

            Integer messageId;

            if (messageText.equals(MessageText.LOGIN_ADDED)) {
                usernameService.createUsername(chatId, utils.userName(text));
            }
            if (messageText.equals(MessageText.LOGIN_CHANGED)) {
                sendText = String.format(
                        sendText,
                        username.getGithubUsername(),
                        utils.userName(text));
                usernameService.updateUsername(chatId, utils.userName(text));
            }
            if (messageText.equals(MessageText.REPO_ADDED)) {
                createRequestService.create(
                        utils.repositoryName(text),
                        username,
                        null,
                        Status.ACCEPTED);
                githubService.createGitRepository(
                        utils.repositoryName(text));
            }
            if (messageText.equals(MessageText.ACCESS_ADDED)) {
                accessRequestService.create(
                        utils.repositoryName(text),
                        username,
                        null,
                        Status.ACCEPTED);
                githubService.accessGitRepository(
                        utils.repositoryName(text), username.getGithubUsername());
            }
            if (messageText.equals(MessageText.CREATE_REPO_REQ)) {
                teamleadText = utils.teamleadText(
                        create, text, username, chatName);
                messageId =  sendMessage(
                        teamleadText,
                        teamleadChatId,
                        utils.createInlineKeyboardMarkup("crYes", "crNo"));
                createRequestService.create(
                        utils.repositoryName(text),
                        username,
                        messageId,
                        Status.WAIT);
            }
            if (messageText.equals(MessageText.ACCESS_REPO_REQ)) {
                teamleadText = utils.teamleadText(
                        access, text, username, chatName);
                messageId =  sendMessage(
                        teamleadText,
                        teamleadChatId,
                        utils.createInlineKeyboardMarkup("acYes", "acNo"));
                accessRequestService.create(
                        utils.repositoryName(text),
                        username,
                        messageId,
                        Status.WAIT);
            }
        } else if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
            text = update.getCallbackQuery().getData();
            Integer messageId = message.getMessageId();
            CreateRequest createRepository = createRequestService.get(messageId);
            username = createRepository.getTelegramId();
            chatId = username.getTelegramId();
            String repositoryName = createRepository.getRepo();

            if (text.equals("crYes")) {
                githubService.createGitRepository(repositoryName);
                githubService.accessGitRepository(
                        repositoryName,
                        username.getGithubUsername());
                createRequestService.update(messageId, Status.ACCEPTED);
                teamleadText = create + repositoryName + " принято";
                sendText = "Репозиторий " + repositoryName + " создан";
            }
            if (text.equals("crNo")) {
                createRequestService.update(messageId, Status.REJECTED);
                teamleadText = create + repositoryName + " отклонено";
                sendText = "В создании репозитория " + repositoryName + " отказано";
            }
            if (text.equals("acYes")) {
                githubService.accessGitRepository(
                        repositoryName,
                        username.getGithubUsername());
                accessRequestService.update(
                        messageId,
                        Status.ACCEPTED);
                teamleadText = "Доступ к " + repositoryName + " предоставлен";
                sendText = access + repositoryName + " предоставлен";
            }
            if (text.equals("acNo")) {
                accessRequestService.update(
                        messageId,
                        Status.REJECTED);
                teamleadText = "Доступ к " + repositoryName + " отклонен";
                sendText = access + repositoryName + " отказано";
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