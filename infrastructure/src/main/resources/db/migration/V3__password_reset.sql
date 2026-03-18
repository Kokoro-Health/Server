ALTER TABLE user_security
    ADD password_reset_code VARCHAR(255);

ALTER TABLE user_security
    ADD password_reset_code_requested_at TIMESTAMP WITHOUT TIME ZONE;