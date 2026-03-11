CREATE TABLE energy_entry
(
    id         UUID NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    amount     INTEGER,
    user_id    UUID,
    reason     VARCHAR(255),
    CONSTRAINT pk_energy_entry PRIMARY KEY (id)
);

CREATE TABLE notification_settings
(
    id               UUID NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    marketing_emails BOOLEAN,
    security_alerts  BOOLEAN,
    reminder_emails  BOOLEAN,
    CONSTRAINT pk_notification_settings PRIMARY KEY (id)
);

CREATE TABLE settings
(
    id                       UUID NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    created_at               TIMESTAMP WITHOUT TIME ZONE,
    user_id                  UUID,
    language                 VARCHAR(255),
    theme                    VARCHAR(255),
    notification_settings_id UUID,
    CONSTRAINT pk_settings PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                  UUID         NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    first_name          VARCHAR(255) NOT NULL,
    middle_name         VARCHAR(255),
    last_name           VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    profile_picture_url VARCHAR(255),
    password_hash       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE settings
    ADD CONSTRAINT uc_settings_notification_settings UNIQUE (notification_settings_id);

ALTER TABLE settings
    ADD CONSTRAINT uc_settings_user UNIQUE (user_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE energy_entry
    ADD CONSTRAINT FK_ENERGY_ENTRY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE settings
    ADD CONSTRAINT FK_SETTINGS_ON_NOTIFICATION_SETTINGS FOREIGN KEY (notification_settings_id) REFERENCES notification_settings (id);

ALTER TABLE settings
    ADD CONSTRAINT FK_SETTINGS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);