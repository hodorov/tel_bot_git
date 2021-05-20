package github.smartsoft.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class TelBotGitApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TelBotGitApplication.class, args);
    }

}
