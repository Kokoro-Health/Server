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

ALTER TABLE energy_entry
    ADD CONSTRAINT FK_ENERGY_ENTRY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);