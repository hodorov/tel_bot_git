package github.smartsoft.telegrambot.entity;

public enum MessageText {
    ENTER_LOGIN ("Укажите логин на гитхабе с помощью команды /username"),
    LOGIN_NOT_ENTER ("Логин не указан, воспользуйтесь командой /username"),
    LOGIN_CHANGED ("Логин %s изменен на %s"),
    LOGIN_ADDED ("Логин добавлен"),
    REPO_ADDED ("Репозиторий %s создан"),
    ACCESS_ADDED ("Доступ к репозиторию %s предоставлен"),
    LOGIN_EXISTS ("Такой логин уже существует"),
    REPO_EXISTS ("Репозиторий уже существует"),
    ACCESS_EXISTS ("Доступ уже предоставлен"),
    CREATE_REPO_REQ ("Запрос зарегистрирован"),
    ACCESS_REPO_REQ ("Запрос зарегистрирован"),
    NOT_GITHUB_USER ("На github нет такого пользователя"),
    CREATE_REPO ("Создание репозитория"),
    ACCESS_REPO ("Доступ к репозиторию"),
    NONE ("");

    String text;

    MessageText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
