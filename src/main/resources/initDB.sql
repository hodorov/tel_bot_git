DROP TABLE IF EXISTS create_request ;
DROP TABLE IF EXISTS github_nickname;

CREATE TABLE github_nickname
(
    id              BIGSERIAL PRIMARY KEY,
    telegram_id     BIGINT    UNIQUE                                    NOT NULL,
    github_username TEXT                                                NOT NULL,
    is_teamlead     BOOLEAN   DEFAULT FALSE                             NOT NULL
);

CREATE TABLE create_request
(
    id              BIGSERIAL PRIMARY KEY,
    telegram_id     BIGINT    REFERENCES github_nickname (telegram_id)  NOT NULL,
    message_id      BIGINT,
    repo            TEXT                                                NOT NULL,
    status          TEXT                                                NOT NULL
);

CREATE TABLE access_request
(
    id              BIGSERIAL PRIMARY KEY,
    telegram_id     BIGINT    REFERENCES github_nickname (telegram_id)  NOT NULL,
    message_id      BIGINT,
    repo            TEXT                                                NOT NULL,
    status          TEXT                                                NOT NULL
);