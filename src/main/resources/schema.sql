DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_dates CASCADE;
DROP TABLE IF EXISTS proxies CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(100)               NOT NULL,
    telegram_id    BIGINT                     NOT NULL,
    role           VARCHAR(20) DEFAULT 'USER' NOT NULL,
    issue_date     TIMESTAMP WITHOUT TIME ZONE,
    orders_limit   BIGINT,
    orders_counter BIGINT,
    is_active      BOOLEAN
);

CREATE TABLE IF NOT EXISTS user_dates
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id           BIGINT                  NOT NULL,
    name_surname       VARCHAR(255)            NOT NULL,
    phone    VARCHAR(255) NOT NULL,
    email              VARCHAR(255)            NOT NULL,
    passport VARCHAR(255),
    appointment_number VARCHAR(255)            NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    city_type               VARCHAR(255)            NOT NULL,
    is_registered BOOLEAN NOT NULL DEFAULT 'false',
    FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS proxies
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR(100) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    ip_address VARCHAR(100) NOT NULL,
    port       INTEGER      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    valid      BOOLEAN      NOT NULL DEFAULT 'TRUE'
);

