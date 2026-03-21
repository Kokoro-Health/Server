ALTER TABLE settings
    ADD date_format VARCHAR(255) DEFAULT 'yyyy-MM-dd';

ALTER TABLE settings
    ADD time_zone VARCHAR(255) DEFAULT 'UTC';
