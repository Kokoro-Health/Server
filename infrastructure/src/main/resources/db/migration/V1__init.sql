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

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE energy_entry
    ADD CONSTRAINT FK_ENERGY_ENTRY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);