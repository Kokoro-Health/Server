ALTER TABLE settings
    ADD timezone VARCHAR(255) DEFAULT 'UTC';

ALTER TABLE settings
DROP
COLUMN time_zone;