CREATE TABLE journal_entry
(
    id         UUID NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    content    VARCHAR(255),
    user_id    UUID,
    CONSTRAINT pk_journal_entry PRIMARY KEY (id)
);

ALTER TABLE journal_entry
    ADD CONSTRAINT FK_JOURNAL_ENTRY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);