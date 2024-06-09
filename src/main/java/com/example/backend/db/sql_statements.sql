-----------------------------------------------------------------------------
-- Selecting

-- Select all answers, categories, keywords, images, question_types for one question
SELECT q.id AS question_id, q.fk_category_id, q.difficulty, q.points, q.question, q.fk_question_type_id, q.remark, q.created_at, q.updated_at,
       a.id AS answer_id, a.answer, c.name AS category_name,
       k.id AS keyword_id, k.keyword, qt.name AS question_type,
       i.id AS image_id, i.image, i.name AS image_name, i.position, i.comment
FROM questions q
         LEFT JOIN has_aq ha ON q.id = ha.fk_question_id
         LEFT JOIN answers a ON ha.fk_answer_id = a.id
         LEFT JOIN categories c ON q.fk_category_id = c.id
         LEFT JOIN has_kq hkq ON q.id = hkq.fk_question_id
         LEFT JOIN keywords k ON hkq.fk_keyword_id = k.id
         LEFT JOIN has_iq hiq ON q.id = hiq.fk_question_id
         LEFT JOIN images i ON hiq.fk_image_id = i.id
         LEFT JOIN question_types qt ON q.fk_question_type_id = qt.id
WHERE q.id = 1;

-- Select all Courses for a certain studyprogram
SELECT *
FROM courses
         JOIN has_sc ON courses.id = has_sc.fk_course_id
         JOIN study_programs ON has_sc.fk_program_id = study_programs.id
WHERE has_sc.fk_program_id = 1;

-- Select all Questions for a certain studyprogram and a certain Course
SELECT questions.*, courses.name AS CourseName, study_programs.abbreviation AS ProgramAbbreviation
FROM questions
         JOIN has_cc ON questions.fk_category_id = has_cc.fk_category_id
         JOIN courses ON has_cc.fk_course_id = courses.id
         JOIN has_sc ON courses.id = has_sc.fk_course_id
         JOIN study_programs ON has_sc.fk_program_id = study_programs.id
WHERE study_programs.id = 1 AND courses.id = 2;

-- Select all Questions for a certain course
SELECT q.*
FROM questions q
         JOIN has_cc hcc ON q.fk_category_id = hcc.fk_category_id
         JOIN courses c ON hcc.fk_course_id = c.id
WHERE c.name = 'MACS1';

-- Select all Keywords for a certain Question
SELECT keywords.*
FROM keywords
         JOIN has_kq ON keywords.id = has_kq.fk_keyword_id
         JOIN questions ON has_kq.fk_question_id = questions.id
WHERE has_kq.fk_question_id = 1;


-- Select all Images for a certain Question
SELECT images.*
FROM images
         JOIN has_iq ON images.id = has_iq.fk_image_id
         JOIN questions ON has_iq.fk_question_id = questions.id
WHERE has_iq.fk_question_id = 1;

-- Select all Categories for a certain Course
SELECT categories.*
FROM categories
         JOIN has_cc ON categories.id = has_cc.fk_category_id
         JOIN courses ON has_cc.fk_course_id = courses.id
WHERE has_cc.fk_course_id = 1;

-----------------------------------------------------------------------------
-- Create statements
CREATE TABLE categories (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE courses (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    number INTEGER NOT NULL,
    lector TEXT NOT NULL
);

CREATE TABLE study_programs (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    abbreviation TEXT NOT NULL
);

CREATE TABLE images (
    id INTEGER PRIMARY KEY,
    image BLOB NOT NULL,
    name TEXT,
    position INTEGER NOT NULL,
    comment TEXT
);

CREATE TABLE keywords (
    id INTEGER PRIMARY KEY,
    keyword TEXT NOT NULL
);

CREATE TABLE questions (
    id INTEGER PRIMARY KEY,
    fk_category_id INTEGER NOT NULL,
    difficulty INTEGER NOT NULL,
    points FLOAT NOT NULL,
    question TEXT NOT NULL,
    fk_question_type_id INTEGER NOT NULL,
    remark TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT,
    FOREIGN KEY (fk_category_id) REFERENCES categories(id),
    FOREIGN KEY (fk_question_type_id) REFERENCES question_types(id)
);

CREATE TABLE question_types (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE answers (
    id INTEGER PRIMARY KEY,
    answer TEXT NOT NULL UNIQUE
);

CREATE TABLE has_aq (
    fk_answer_id INT,
    fk_question_id INT,
    PRIMARY KEY (fk_answer_id, fk_question_id),
    FOREIGN KEY (fk_answer_id) REFERENCES answers(id),
    FOREIGN KEY (fk_question_id) REFERENCES questions(id)
);

CREATE TABLE has_sc (
    fk_program_id INT,
    fk_course_id INT,
    PRIMARY KEY (fk_program_id, fk_course_id),
    FOREIGN KEY (fk_program_id) REFERENCES study_programs(id),
    FOREIGN KEY (fk_course_id) REFERENCES courses(id)
);

CREATE TABLE has_cc (
    fk_course_id INT,
    fk_category_id INT,
    PRIMARY KEY (fk_course_id, fk_category_id),
    FOREIGN KEY (fk_course_id) REFERENCES courses(id),
    FOREIGN KEY (fk_category_id) REFERENCES categories(id)
);

CREATE TABLE has_iq (
    fk_image_id INT,
    fk_question_id INT,
    PRIMARY KEY (fk_image_id, fk_question_id),
    FOREIGN KEY (fk_image_id) REFERENCES images(id),
    FOREIGN KEY (fk_question_id) REFERENCES questions(id)
);

CREATE TABLE has_kq (
    fk_keyword_id INT,
    fk_question_id INT,
    PRIMARY KEY (fk_keyword_id, fk_question_id),
    FOREIGN KEY (fk_keyword_id) REFERENCES keywords(id),
    FOREIGN KEY (fk_question_id) REFERENCES questions(id)
);

-----------------------------------------------------------------------------
-- Insert statements
INSERT INTO categories (name) VALUES('SQL-Datenbanken');
INSERT INTO categories (name) VALUES('Analysis');
INSERT INTO categories (name) VALUES('NoSQL');
INSERT INTO categories (name) VALUES('Algebra');

INSERT INTO courses (name, number, lector) VALUES('MACS1',11112,'macs1 lektor');
INSERT INTO courses (name, number, lector) VALUES('MACS2',11113,'macs2 lektor');
INSERT INTO courses (name, number, lector) VALUES('Verteilte Systeme',11114,'VERTS lektor');
INSERT INTO courses (name, number, lector) VALUES('MACS3',11123,'macs3 lektor');
INSERT INTO courses (name, number, lector) VALUES('Datenmanagement',112342,'DM lektor');

INSERT INTO study_programs (name, abbreviation) VALUES('Bachelor Informatik','BIF');
INSERT INTO study_programs (name, abbreviation) VALUES('Master Tissue Engineering and Regenerative medicine','MTE');
INSERT INTO study_programs (name, abbreviation) VALUES('Biomedical Engineering','BME');
INSERT INTO study_programs (name, abbreviation) VALUES('Wirtschaftinformatik','WIF');
INSERT INTO study_programs (name, abbreviation) VALUES('Master AI Engineering','MAE');

-- hier würde ein insert-statement für images kommen

INSERT INTO keywords (keyword) VALUES('Integralrechnung');
INSERT INTO keywords (keyword) VALUES('SQL-Statements');
INSERT INTO keywords (keyword) VALUES('Gruppe/Körper');
INSERT INTO keywords (keyword) VALUES('Threads');
INSERT INTO keywords (keyword) VALUES('IPC-Kommunikation');
INSERT INTO keywords (keyword) VALUES('Pipes');
INSERT INTO keywords (keyword) VALUES('Vektoren');
INSERT INTO keywords (keyword) VALUES('Heine-Matrix');
INSERT INTO keywords (keyword) VALUES('Eigenwert');
INSERT INTO keywords (keyword) VALUES('NoSQL');
INSERT INTO keywords (keyword) VALUES('Differentialrechnung');

INSERT INTO question_types (name) VALUES('OPEN');
INSERT INTO question_types (name) VALUES('MULTIPLE_CHOICE');
INSERT INTO question_types (name) VALUES('TRUE_FALSE');
INSERT INTO question_types (name) VALUES('SHORT_ANSWER');
INSERT INTO question_types (name) VALUES('ESSAY');

INSERT INTO answers (answer) VALUES('');
INSERT INTO answers (answer) VALUES('2');

INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(4, 6, 7.5, 'Berechnen Sie die Eigenwerte der Matrix:', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 8, 7, 'Berechnen Sie die zweite Ableitung der Funktion: f(x) = x²', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(3, 4, 3.5, 'Welche No-SQL datenbanken gibt es', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(1, 6, 8.0, 'Was ist der Unterschied zwischen INNER JOIN und LEFT JOIN in SQL?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 8, 7, 'Berechnen Sie die zweite Ableitung der Funktion: f(x) = x² + 2', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(3, 7, 9.5, 'Nennen Sie drei Vorteile von NoSQL-Datenbanken gegenüber relationalen Datenbanken.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 5, 6.5, 'Lösen Sie die folgende Gleichung: 2x + 5 = 15', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(4, 8, 7.0, 'Definieren Sie den Begriff "Eigenvector" in der linearen Algebra.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(1, 4, 5.5, 'Was ist der Zweck von SQL-Indizes?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(3, 6, 8.0, 'Erklären Sie den Begriff "Sharding" in Bezug auf NoSQL-Datenbanken.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 7, 9.0, 'Berechnen Sie das bestimmte Integral von sin(x) von 0 bis pi.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(4, 5, 6.0, 'Was sind Gruppen in der Algebra?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(1, 8, 7.5, 'Erklären Sie den Begriff "Subquery" in SQL.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(3, 4, 4.5, 'Welche NoSQL-Datenbank wird häufig für die Speicherung von Dokumenten verwendet?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 6, 7.0, 'Was ist die Ableitung von ln(x)?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(4, 7, 8.5, 'Erläutern Sie den Begriff "Polynomdivision" in der Algebra.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(3, 5, 6.5, 'Was sind die Vor- und Nachteile von dokumentenorientierten NoSQL-Datenbanken?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(1, 8, 9.0, 'Wie erstellt man eine gespeicherte Prozedur in SQL?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 4, 5.0, 'Lösen Sie die Gleichungssysteme: 2x + y = 5 und 3x - 2y = 1', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(4, 6, 7.5, 'Was ist eine invertierbare Matrix?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(3, 7, 8.0, 'Erklären Sie den Begriff "Caching" in Bezug auf NoSQL-Datenbanken.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(1, 5, 6.0, 'Was sind Joins in SQL?', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(2, 8, 9.5, 'Berechnen Sie das unbestimmte Integral von e^(2x)dx.', 1);
INSERT INTO questions (fk_category_id, difficulty, points, question, fk_question_type_id) VALUES(4, 4, 5.5, 'Was sind komplexe Zahlen?', 1);

INSERT INTO has_aq VALUES(1, 1);
INSERT INTO has_aq VALUES(2, 2);
INSERT INTO has_aq VALUES(1, 3);
INSERT INTO has_aq VALUES(1, 4);
INSERT INTO has_aq VALUES(1, 5);
INSERT INTO has_aq VALUES(1, 6);
INSERT INTO has_aq VALUES(1, 7);
INSERT INTO has_aq VALUES(1, 8);
INSERT INTO has_aq VALUES(1, 9);
INSERT INTO has_aq VALUES(1, 10);
INSERT INTO has_aq VALUES(1, 11);
INSERT INTO has_aq VALUES(1, 12);
INSERT INTO has_aq VALUES(1, 13);
INSERT INTO has_aq VALUES(1, 14);
INSERT INTO has_aq VALUES(1, 15);
INSERT INTO has_aq VALUES(1, 16);
INSERT INTO has_aq VALUES(1, 17);
INSERT INTO has_aq VALUES(1, 18);
INSERT INTO has_aq VALUES(1, 19);
INSERT INTO has_aq VALUES(1, 20);
INSERT INTO has_aq VALUES(1, 21);
INSERT INTO has_aq VALUES(1, 22);
INSERT INTO has_aq VALUES(1, 23);
INSERT INTO has_aq VALUES(1, 24);

INSERT INTO has_sc VALUES(1, 1);
INSERT INTO has_sc VALUES(1, 2);
INSERT INTO has_sc VALUES(1, 3);
INSERT INTO has_sc VALUES(1, 4);
INSERT INTO has_sc VALUES(1, 5);
INSERT INTO has_sc VALUES(4, 1);
INSERT INTO has_sc VALUES(4, 2);
INSERT INTO has_sc VALUES(4, 5);

INSERT INTO has_cc VALUES(1, 2);
INSERT INTO has_cc VALUES(1, 4);
INSERT INTO has_cc VALUES(2, 2);
INSERT INTO has_cc VALUES(2, 4);
INSERT INTO has_cc VALUES(5, 1);
INSERT INTO has_cc VALUES(5, 3);

INSERT INTO has_kq VALUES(7, 1);
INSERT INTO has_kq VALUES(8, 1);
INSERT INTO has_kq VALUES(9, 1);
INSERT INTO has_kq VALUES(11, 2);
INSERT INTO has_kq VALUES(2, 3);
INSERT INTO has_kq VALUES(10, 3);
INSERT INTO has_kq VALUES(3, 24);
INSERT INTO has_kq VALUES(1, 23);
INSERT INTO has_kq VALUES(2, 22);
INSERT INTO has_kq VALUES(10, 21);
INSERT INTO has_kq VALUES(3, 20);
INSERT INTO has_kq VALUES(7, 20);
INSERT INTO has_kq VALUES(8, 20);
INSERT INTO has_kq VALUES(3, 19);
INSERT INTO has_kq VALUES(2, 18);
INSERT INTO has_kq VALUES(10, 17);
INSERT INTO has_kq VALUES(3, 16);
INSERT INTO has_kq VALUES(7, 16);
INSERT INTO has_kq VALUES(11, 15);
INSERT INTO has_kq VALUES(10, 14);
INSERT INTO has_kq VALUES(2, 13);
INSERT INTO has_kq VALUES(3, 12);
INSERT INTO has_kq VALUES(1, 11);
INSERT INTO has_kq VALUES(10, 10);
INSERT INTO has_kq VALUES(2, 9);
INSERT INTO has_kq VALUES(9, 8);
INSERT INTO has_kq VALUES(3, 7);
INSERT INTO has_kq VALUES(10, 6);
INSERT INTO has_kq VALUES(11, 5);
INSERT INTO has_kq VALUES(2, 4);

-----------------------------------------------------------------------------
-- DROP TABLE IF EXISTS has_kq;
-- DROP TABLE IF EXISTS has_iq;
-- DROP TABLE IF EXISTS has_cc;
-- DROP TABLE IF EXISTS has_sc;
-- DROP TABLE IF EXISTS has_aq;
-- DROP TABLE IF EXISTS answers;
-- DROP TABLE IF EXISTS question_types;
-- DROP TABLE IF EXISTS questions;
-- DROP TABLE IF EXISTS keywords;
-- DROP TABLE IF EXISTS images;
-- DROP TABLE IF EXISTS study_programs;
-- DROP TABLE IF EXISTS courses;
-- DROP TABLE IF EXISTS categories;
