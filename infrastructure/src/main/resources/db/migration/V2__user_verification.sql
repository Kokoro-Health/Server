ALTER TABLE user_security
    ADD verification_code VARCHAR(255);

ALTER TABLE user_security
    ADD verified BOOLEAN DEFAULT FALSE;

ALTER TABLE user_security
    ADD verification_code_requested_at TIMESTAMP WITHOUT TIME ZONE;