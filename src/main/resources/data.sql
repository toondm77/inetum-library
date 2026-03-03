-- Test data for RLibrary Backend
-- Automatically loaded on application startup

-- Insert Libraries FIRST
INSERT INTO library (id, name, country, city, street, street_number, description) VALUES
(1, 'Central Library', 'Belgium', 'Brussels', 'Main Street', '12A', 'Primary public library for the city');

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
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('1984', 'George Orwell', 'Dystopian novel set in a totalitarian state', '978-0451524935', 1949, 328, '1949-06-08', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 12.99, 5, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Hobbit', 'J.R.R. Tolkien', 'Fantasy adventure of Bilbo Baggins', '978-0547928227', 1937, 310, '1937-09-21', 'IT', 'AVAILABLE', 'TEEN', 15.99, 8, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Pride and Prejudice', 'Jane Austen', 'Romantic novel of Elizabeth Bennet', '978-0141439518', 1813, 432, '1813-01-28', 'COMMUNICATION', 'AVAILABLE', 'ADULT', 9.99, 6, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Adventures of Tom Sawyer', 'Mark Twain', 'Classic American novel of boyhood', '978-0486400777', 1876, 274, '1876-12-01', 'BUSINESS', 'AVAILABLE', 'CHILD', 7.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('To Kill a Mockingbird', 'Harper Lee', 'Pulitzer Prize-winning novel about racial injustice', '978-0061120084', 1960, 324, '1960-07-11', 'COMMUNICATION', 'AVAILABLE', 'ADULT', 14.99, 7, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', 'Fantasy novel introducing the wizarding world', '978-0747532699', 1998, 309, '1998-06-26', 'IT', 'AVAILABLE', 'CHILD', 13.99, 10, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Shining', 'Stephen King', 'Psychological horror novel', '978-0385333312', 1977, 447, '1977-01-28', 'LAWS', 'AVAILABLE', 'ADULT', 16.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Murder on the Orient Express', 'Agatha Christie', 'Detective mystery novel', '978-0062693556', 1934, 256, '1934-01-01', 'BUSINESS', 'AVAILABLE', 'ADULT', 10.99, 5, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Alchemist', 'Paulo Coelho', 'Philosophical novel about pursuing your dreams', '978-0062315007', 1988, 224, '1988-01-01', 'FINANCE', 'AVAILABLE', 'ADULT', 11.99, 9, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Foundation', 'Isaac Asimov', 'Science fiction novel of galactic empire', '978-0553293357', 1951, 255, '1951-06-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 13.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Hitchhiker''s Guide to the Galaxy', 'Douglas Adams', 'Comic science fiction novel', '978-0345391803', 1979, 193, '1979-10-12', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 12.99, 6, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Fahrenheit 451', 'Ray Bradbury', 'Dystopian novel about book burning', '978-1451673264', 1953, 249, '1953-10-19', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 11.99, 5, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Brave New World', 'Aldous Huxley', 'Dystopian novel of a futuristic society', '978-0060085239', 1932, 268, '1932-08-30', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 12.99, 7, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Old Man and the Sea', 'Ernest Hemingway', 'Novel about an old fisherman''s struggle', '978-0684801223', 1952, 127, '1952-09-01', 'BUSINESS', 'AVAILABLE', 'ADULT', 10.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Great Gatsby', 'F.Scott Fitzgerald', 'Jazz Age classic about love and wealth', '978-0743273565', 1925, 180, '1925-04-10', 'FINANCE', 'AVAILABLE', 'ADULT', 11.99, 8, 1);

-- Insert Persons
INSERT INTO person (first_name, last_name, birth_date, functie, badge_code, account_status) VALUES
('Alice', 'Smith', '1990-05-15', 'MEMBER', 'B12345', 'ACTIVE'),
('Bob', 'Johnson', '1985-09-20', 'MEMBER', 'B67890', 'ACTIVE');

-- Insert Loans
INSERT INTO loan (loan_date, return_date, status, person_id) VALUES
('2026-02-01', '2026-02-15', 'LOANED', 1),
('2026-01-10', '2026-01-24', 'RETURNED', 2);

-- Link Loans to Books (loan_books join table)
INSERT INTO loan_books (loan_id, book_id) VALUES
(1, 1), -- Loan 1 for Book 1 (1984)
(1, 2), -- Loan 1 for Book 2 (The Hobbit)
(2, 3); -- Loan 2 for Book 3 (Pride and Prejudice)

-- 50 extra tech books for seeding
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Java Concurrency in Practice', 'Isaac Asimov', 'Comprehensive guide to Java concurrency and multithreading.', '978-0321349606', 2006, 384, '2006-05-19', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Clean Code: A Handbook of Agile Software Craftsmanship', 'Douglas Adams', 'Best practices for writing clean, maintainable code.', '978-0132350884', 2008, 464, '2008-08-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 42.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Design Patterns: Elements of Reusable Object-Oriented Software', 'Isaac Asimov', 'Classic book on software design patterns.', '978-0201633610', 1994, 395, '1994-10-31', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Pragmatic Programmer', 'Douglas Adams', 'Journey to mastery in modern software development.', '978-0201616224', 1999, 352, '1999-10-20', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 37.99, 5, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Refactoring: Improving the Design of Existing Code', 'Isaac Asimov', 'Techniques for refactoring and improving codebases.', '978-0201485677', 1999, 431, '1999-07-08', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 44.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Introduction to Algorithms', 'Douglas Adams', 'Comprehensive introduction to algorithms and data structures.', '978-0262033848', 2009, 1312, '2009-07-31', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 89.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Artificial Intelligence: A Modern Approach', 'Isaac Asimov', 'Definitive guide to AI concepts and techniques.', '978-0136042594', 2010, 1152, '2010-12-11', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 99.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Python Crash Course', 'Douglas Adams', 'Hands-on, project-based introduction to programming in Python.', '978-1593279288', 2019, 544, '2019-05-03', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 34.99, 6, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('You Don''t Know JS: Scope & Closures', 'Isaac Asimov', 'Deep dive into JavaScript scope and closures.', '978-1449335588', 2014, 98, '2014-03-10', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 19.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Effective Java', 'Douglas Adams', 'Best practices for Java programming.', '978-0134685991', 2018, 416, '2018-01-06', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 54.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Head First Design Patterns', 'Isaac Asimov', 'Visual guide to design patterns in software engineering.', '978-0596007126', 2004, 694, '2004-10-25', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 47.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Cracking the Coding Interview', 'Douglas Adams', 'Interview preparation for software engineers.', '978-0984782857', 2015, 687, '2015-07-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 5, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Art of Computer Programming Vol. 1', 'Isaac Asimov', 'Foundational algorithms and programming techniques.', '978-0201896831', 1997, 672, '1997-07-15', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 99.99, 1, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Structure and Interpretation of Computer Programs', 'Douglas Adams', 'Classic text on computer science and programming.', '978-0262510875', 1996, 657, '1996-07-25', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Programming Pearls', 'Isaac Asimov', 'Essays on programming and problem solving.', '978-0201657883', 1999, 256, '1999-10-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 29.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Code Complete', 'Douglas Adams', 'Comprehensive guide to software construction.', '978-0735619678', 2004, 960, '2004-06-09', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Mythical Man-Month', 'Isaac Asimov', 'Essays on software engineering and project management.', '978-0201835953', 1995, 322, '1995-08-12', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 34.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Working Effectively with Legacy Code', 'Douglas Adams', 'Strategies for maintaining and improving legacy codebases.', '978-0131177055', 2004, 456, '2004-09-30', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Continuous Delivery', 'Isaac Asimov', 'Principles and practices for continuous software delivery.', '978-0321601919', 2010, 512, '2010-07-27', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Clean Coder', 'Douglas Adams', 'A code of conduct for professional programmers.', '978-0137081073', 2011, 256, '2011-05-13', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 34.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Test-Driven Development: By Example', 'Isaac Asimov', 'Guide to TDD and unit testing in software development.', '978-0321146533', 2002, 240, '2002-11-18', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 29.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Domain-Driven Design', 'Douglas Adams', 'Tackling complexity in the heart of software.', '978-0321125217', 2003, 560, '2003-08-30', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 64.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Introduction to the Theory of Computation', 'Isaac Asimov', 'Fundamentals of computation theory and automata.', '978-1133187790', 2012, 504, '2012-01-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 79.99, 1, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Programming in Scala', 'Douglas Adams', 'Comprehensive introduction to Scala programming.', '978-0981531687', 2016, 852, '2016-03-15', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 54.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The C Programming Language', 'Isaac Asimov', 'Classic introduction to C programming.', '978-0131103627', 1988, 272, '1988-04-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('JavaScript: The Good Parts', 'Douglas Adams', 'Best practices and features of JavaScript.', '978-0596517748', 2008, 176, '2008-05-15', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 24.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Learning Python', 'Isaac Asimov', 'Comprehensive guide to Python programming.', '978-1449355739', 2013, 1648, '2013-06-12', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 69.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Spring in Action', 'Douglas Adams', 'Hands-on guide to Spring Framework.', '978-1617294945', 2018, 520, '2018-10-05', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Pro Git', 'Isaac Asimov', 'Comprehensive guide to Git version control.', '978-1484200773', 2014, 456, '2014-11-18', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 0.00, 5, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Docker Deep Dive', 'Douglas Adams', 'In-depth guide to Docker containers.', '978-1521822807', 2017, 432, '2017-06-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Kubernetes Up & Running', 'Isaac Asimov', 'Guide to deploying and managing containers with Kubernetes.', '978-1492046530', 2019, 368, '2019-09-10', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 44.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Microservices Patterns', 'Douglas Adams', 'Design patterns for building distributed systems.', '978-1617294549', 2018, 520, '2018-11-20', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Hands-On Machine Learning with Scikit-Learn, Keras, and TensorFlow', 'Isaac Asimov', 'Practical guide to machine learning with Python.', '978-1492032649', 2019, 819, '2019-10-15', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 64.99, 1, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Deep Learning', 'Douglas Adams', 'Comprehensive introduction to deep learning.', '978-0262035613', 2016, 800, '2016-11-18', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 89.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Fluent Python', 'Isaac Asimov', 'Clear, practical guide to writing effective Python code.', '978-1491946008', 2015, 792, '2015-08-20', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Grokking Algorithms', 'Douglas Adams', 'Illustrated guide for learning algorithms.', '978-1617292231', 2016, 256, '2016-05-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 4, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Linux Command Line', 'Isaac Asimov', 'Complete introduction to the Linux command line.', '978-1593273897', 2012, 480, '2012-01-15', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 34.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Phoenix Project', 'Douglas Adams', 'Novel about IT, DevOps, and helping your business win.', '978-0988262591', 2013, 368, '2013-01-10', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 44.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Site Reliability Engineering', 'Isaac Asimov', 'How Google runs production systems.', '978-1491929124', 2016, 552, '2016-03-23', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 69.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Cloud Native Java', 'Douglas Adams', 'Designing resilient systems with Spring Boot, Spring Cloud, and Cloud Foundry.', '978-1449374648', 2017, 648, '2017-05-10', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 1, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Refactoring UI', 'Isaac Asimov', 'Practical guide to designing beautiful user interfaces.', '978-1989028106', 2018, 218, '2018-11-06', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Building Microservices', 'Douglas Adams', 'Designing fine-grained systems.', '978-1491950357', 2015, 280, '2015-02-20', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 54.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The DevOps Handbook', 'Isaac Asimov', 'How to create world-class agility, reliability, and security in technology organizations.', '978-1942788003', 2016, 480, '2016-10-06', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 59.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Art of Scalability', 'Douglas Adams', 'Scalable web architecture, processes, and organizations.', '978-0134032801', 2015, 624, '2015-04-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 69.99, 1, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Release It!', 'Isaac Asimov', 'Design and deploy production-ready software.', '978-1680502398', 2018, 376, '2018-03-09', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Soft Skills: The software developer''s life manual', 'Douglas Adams', 'Essential skills for a successful software career.', '978-1617292392', 2015, 504, '2015-12-27', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Manager''s Path', 'Isaac Asimov', 'Guide for tech leads and engineering managers.', '978-1491973899', 2017, 244, '2017-03-13', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 44.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('The Unicorn Project', 'Douglas Adams', 'Novel about developers, digital disruption, and business.', '978-1942788768', 2019, 352, '2019-11-26', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 1, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Accelerate: The Science of Lean Software and DevOps', 'Isaac Asimov', 'Building and scaling high performing technology organizations.', '978-1942788331', 2018, 288, '2018-03-27', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Extreme Programming Explained', 'Douglas Adams', 'Embrace change in software development.', '978-0321278654', 2004, 224, '2004-11-15', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 34.99, 3, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Refactoring to Patterns', 'Isaac Asimov', 'Combining refactoring and design patterns.', '978-0321213358', 2004, 421, '2004-08-30', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 49.99, 2, 1);
INSERT INTO book (title, author, description, isbn, publication_year, amount_of_pages, release_date, theme, book_state, age_category, purchase_price, dupplicates, library_id) VALUES
('Continuous Integration', 'Douglas Adams', 'Improving software quality and reducing risk.', '978-0321336385', 2007, 336, '2007-06-01', 'COMPUTERSCIENCE', 'AVAILABLE', 'ADULT', 39.99, 1, 1);
-- End of 50 extra tech books

