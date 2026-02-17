-- Test data for RLibrary Backend
-- Automatically loaded on application startup

-- Insert Authors
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('George', 'Orwell', 'British', 'English novelist and essayist', '1903-06-25');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('J.R.R.', 'Tolkien', 'British', 'English writer, poet, and philologist', '1892-01-03');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Jane', 'Austen', 'British', 'English novelist, best known for her six major novels', '1775-12-16');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Mark', 'Twain', 'American', 'American writer and humorist', '1835-11-30');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Harper', 'Lee', 'American', 'American novelist, author of To Kill a Mockingbird', '1926-04-28');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('J.K.', 'Rowling', 'British', 'British author, creator of Harry Potter', '1965-07-31');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Stephen', 'King', 'American', 'American author of horror and fantasy', '1947-09-21');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Agatha', 'Christie', 'British', 'English mystery writer', '1890-01-15');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Paulo', 'Coelho', 'Brazilian', 'Brazilian lyricist and novelist', '1947-08-24');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Isaac', 'Asimov', 'American', 'American writer and biochemist', '1920-01-02');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Douglas', 'Adams', 'British', 'English author of The Hitchhiker''s Guide to the Galaxy', '1952-03-11');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Ray', 'Bradbury', 'American', 'American author of science fiction and fantasy', '1920-08-22');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Aldous', 'Huxley', 'British', 'English writer, author of Brave New World', '1894-07-26');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('Ernest', 'Hemingway', 'American', 'American novelist and short-story writer', '1899-07-21');
INSERT INTO author (first_name, last_name, nationality, description, birth_date) VALUES
('F.Scott', 'Fitzgerald', 'American', 'American author, author of The Great Gatsby', '1896-09-24');

-- Insert Books
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('1984', 'George Orwell', 'Dystopian novel set in a totalitarian state', '978-0451524935', 1949, 328, '1949-06-08', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 12.99, 5);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Hobbit', 'J.R.R. Tolkien', 'Fantasy adventure of Bilbo Baggins', '978-0547928227', 1937, 310, '1937-09-21', 'IT', 'AVAILABLE', 'TEEN', 15.99, 8);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('Pride and Prejudice', 'Jane Austen', 'Romantic novel of Elizabeth Bennet', '978-0141439518', 1813, 432, '1813-01-28', 'COMMUNICATION', 'AVAILABLE', 'ADULT', 9.99, 6);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Adventures of Tom Sawyer', 'Mark Twain', 'Classic American novel of boyhood', '978-0486400777', 1876, 274, '1876-12-01', 'BUSINESS', 'AVAILABLE', 'CHILD', 7.99, 4);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('To Kill a Mockingbird', 'Harper Lee', 'Pulitzer Prize-winning novel about racial injustice', '978-0061120084', 1960, 324, '1960-07-11', 'COMMUNICATION', 'AVAILABLE', 'ADULT', 14.99, 7);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', 'Fantasy novel introducing the wizarding world', '978-0747532699', 1998, 309, '1998-06-26', 'IT', 'AVAILABLE', 'CHILD', 13.99, 10);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Shining', 'Stephen King', 'Psychological horror novel', '978-0385333312', 1977, 447, '1977-01-28', 'LAWS', 'AVAILABLE', 'ADULT', 16.99, 3);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('Murder on the Orient Express', 'Agatha Christie', 'Detective mystery novel', '978-0062693556', 1934, 256, '1934-01-01', 'BUSINESS', 'AVAILABLE', 'ADULT', 10.99, 5);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Alchemist', 'Paulo Coelho', 'Philosophical novel about pursuing your dreams', '978-0062315007', 1988, 224, '1988-01-01', 'FINANCE', 'AVAILABLE', 'ADULT', 11.99, 9);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('Foundation', 'Isaac Asimov', 'Science fiction novel of galactic empire', '978-0553293357', 1951, 255, '1951-06-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 13.99, 4);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Hitchhiker''s Guide to the Galaxy', 'Douglas Adams', 'Comic science fiction novel', '978-0345391803', 1979, 193, '1979-10-12', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 12.99, 6);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('Fahrenheit 451', 'Ray Bradbury', 'Dystopian novel about book burning', '978-1451673264', 1953, 249, '1953-10-19', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 11.99, 5);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('Brave New World', 'Aldous Huxley', 'Dystopian novel of a futuristic society', '978-0060085239', 1932, 268, '1932-08-30', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 12.99, 7);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Old Man and the Sea', 'Ernest Hemingway', 'Novel about an old fisherman''s struggle', '978-0684801223', 1952, 127, '1952-09-01', 'BUSINESS', 'AVAILABLE', 'ADULT', 10.99, 4);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates) VALUES
('The Great Gatsby', 'F.Scott Fitzgerald', 'Jazz Age classic about love and wealth', '978-0743273565', 1925, 180, '1925-04-10', 'FINANCE', 'AVAILABLE', 'ADULT', 11.99, 8);
