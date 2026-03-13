-- ============================================================
-- V1 - Initial schema for RLibrary
-- ============================================================

-- Library
CREATE TABLE IF NOT EXISTS library (
    id             SERIAL PRIMARY KEY,
    name           VARCHAR(200) NOT NULL,
    country        VARCHAR(100) NOT NULL,
    city           VARCHAR(100) NOT NULL,
    street         VARCHAR(100) NOT NULL,
    street_number  VARCHAR(20)  NOT NULL,
    description    VARCHAR(1000)
);

-- Author
CREATE TABLE IF NOT EXISTS author (
    id          SERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    nationality VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    birth_date  DATE         NOT NULL
);

-- Person (base table, JOINED inheritance)
CREATE TABLE IF NOT EXISTS person (
    id                  SERIAL PRIMARY KEY,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    birth_date          DATE         NOT NULL,
    function            VARCHAR(100),
    badge_code          VARCHAR(100),
    auth0id             VARCHAR(255) UNIQUE,
    user_role           VARCHAR(50),
    gender              VARCHAR(20),
    country             VARCHAR(100),
    phone_number        VARCHAR(20),
    preferred_language  VARCHAR(10),
    profile_picture_url VARCHAR(500),
    active_library_id   INTEGER REFERENCES library (id),
    account_status      VARCHAR(50)
);

-- Staff (joined sub-type of Person)
CREATE TABLE IF NOT EXISTS staff (
    id INTEGER PRIMARY KEY REFERENCES person (id)
);

-- LibraryManager (joined sub-type of Person)
CREATE TABLE IF NOT EXISTS library_manager (
    id INTEGER PRIMARY KEY REFERENCES person (id)
);

-- LibraryRule
CREATE TABLE IF NOT EXISTS library_rule (
    id               SERIAL PRIMARY KEY,
    max_lending_days INTEGER NOT NULL DEFAULT 0,
    open             BOOLEAN NOT NULL DEFAULT false,
    library_id       INTEGER UNIQUE REFERENCES library (id)
);

-- LibraryStats
CREATE TABLE IF NOT EXISTS library_stats (
    id           SERIAL PRIMARY KEY,
    total_books  INTEGER NOT NULL DEFAULT 0,
    total_loans  INTEGER NOT NULL DEFAULT 0,
    client_total INTEGER NOT NULL DEFAULT 0,
    library_id   INTEGER UNIQUE REFERENCES library (id)
);

-- LibraryHasManager
CREATE TABLE IF NOT EXISTS library_has_manager (
    id                 SERIAL PRIMARY KEY,
    library_id         INTEGER REFERENCES library (id),
    library_manager_id INTEGER REFERENCES person (id),
    start_date         DATE,
    end_date           DATE
);

-- LoanRule
CREATE TABLE IF NOT EXISTS loan_rule (
    id               SERIAL PRIMARY KEY,
    max_lending_days INTEGER NOT NULL DEFAULT 0,
    accept_loans     BOOLEAN NOT NULL DEFAULT true
);

-- Book
CREATE TABLE IF NOT EXISTS book (
    id               SERIAL PRIMARY KEY,
    library_id       INTEGER      REFERENCES library (id),
    title            VARCHAR(200) NOT NULL,
    author           VARCHAR(100) NOT NULL,
    description      VARCHAR(1000),
    isbn             VARCHAR(17)  NOT NULL,
    publication_year INTEGER,
    amount_of_pages  INTEGER,
    release_date     DATE,
    theme            VARCHAR(50),
    book_state       VARCHAR(50),
    age_category     VARCHAR(50),
    purchase_price   DOUBLE PRECISION,
    duplicates       INTEGER NOT NULL DEFAULT 0
);

-- Book <-> Author (many-to-many) — column names match Hibernate default naming
CREATE TABLE IF NOT EXISTS book_authors (
    books_id    INTEGER NOT NULL REFERENCES book (id),
    authors_id  INTEGER NOT NULL REFERENCES author (id),
    PRIMARY KEY (books_id, authors_id)
);

-- PersonalStats
CREATE TABLE IF NOT EXISTS personal_stats (
    id             SERIAL PRIMARY KEY,
    pages_total    INTEGER NOT NULL DEFAULT 0,
    days_active    INTEGER NOT NULL DEFAULT 0,
    favorite_theme VARCHAR(50),
    lending_total  INTEGER NOT NULL DEFAULT 0,
    person_id      INTEGER UNIQUE REFERENCES person (id)
);

-- Loan
CREATE TABLE IF NOT EXISTS loan (
    id           SERIAL PRIMARY KEY,
    loan_date    DATE        NOT NULL,
    return_date  DATE,
    status       VARCHAR(50) NOT NULL,
    person_id    INTEGER     NOT NULL REFERENCES person (id),
    loan_rule_id INTEGER REFERENCES loan_rule (id)
);

-- Loan <-> Book (many-to-many)
CREATE TABLE IF NOT EXISTS loan_books (
    loan_id INTEGER NOT NULL REFERENCES loan (id),
    book_id INTEGER NOT NULL REFERENCES book (id),
    PRIMARY KEY (loan_id, book_id)
);

-- BookComplaint
CREATE TABLE IF NOT EXISTS book_complaint (
    id          SERIAL PRIMARY KEY,
    book_id     INTEGER REFERENCES book (id),
    person_id   INTEGER REFERENCES person (id),
    description TEXT
);

-- Notification
CREATE TABLE IF NOT EXISTS notification (
    id         SERIAL PRIMARY KEY,
    type       VARCHAR(50)  NOT NULL,
    title      VARCHAR(255) NOT NULL,
    message    VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT false,
    person_id  INTEGER      NOT NULL REFERENCES person (id)
);

