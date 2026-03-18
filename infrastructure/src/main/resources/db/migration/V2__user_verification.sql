ALTER TABLE user_security
    ADD verification_code VARCHAR(255);

ALTER TABLE user_security
    ADD verified BOOLEAN;