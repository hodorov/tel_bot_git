package github.smartsoft.telegrambot.utils;

import github.smartsoft.telegrambot.entity.Username;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramUtils {
    public String repositoryName(String messageText) {
        return messageText.substring(8).trim();
    }

    public String userName(String messageText) {
        return messageText.substring(10).trim();
    }

    public String teamleadText(String start, String text, Username username, String chatName) {
        return String.format(
                start + " %s (%s, %s)",
                repositoryName(text),
                username.getGithubUsername(),
                "@" + chatName);
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup(String one, String two) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        keyboardButtons.add(createInlineKeyboardButton("Принять", one));
        keyboardButtons.add(createInlineKeyboardButton("Отклонить", two));

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtons);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardButton createInlineKeyboardButton(String text, String callData) {
        return new InlineKeyboardButton().setText(text).setCallbackData(callData);
    }
}
