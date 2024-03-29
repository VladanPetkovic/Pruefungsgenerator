-----------------------------------------------------------------------------
-- Selecting

SELECT Q.QuestionID, Q.FK_Category_ID, Q.Difficulty, Q.Points, Q.Question,
       Q.MultipleChoice, Q.Language, Q.Remarks, Q.Answers,
       C.Category, I.ImageID, I.Link, I.ImageName, I.Position,
       K.KeywordID, K.Keyword
FROM Questions Q
         JOIN Categories C ON Q.FK_Category_ID = C.CategoryID
         LEFT JOIN hasIQ HIQ ON Q.QuestionID = HIQ.QuestionID
         LEFT JOIN Images I ON HIQ.ImageID = I.ImageID
         LEFT JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID
         LEFT JOIN Keywords K ON HKQ.KeywordID = K.KeywordID
WHERE Q.QuestionID = 1;

-- Select all Courses for a certain studyprogram
Select * FROM Courses
JOIN hasSC ON Courses.CourseID = hasSC.CourseID
JOIN StudyPrograms ON hasSC.ProgramID = StudyPrograms.ProgramID
WHERE hasSC.ProgramID = 1;

-- Select all Questions for a certain studyprogram and a certain Course
SELECT Questions.*, Courses.CourseName, StudyPrograms.ProgramAbbreviation
FROM Questions
    JOIN hasCC ON Questions.FK_Category_ID = hasCC.CategoryID
    JOIN Courses ON hasCC.CourseID = Courses.CourseID
    JOIN hasSC ON Courses.CourseID = hasSC.CourseID
    JOIN StudyPrograms ON hasSC.ProgramID = StudyPrograms.ProgramID
WHERE StudyPrograms.ProgramID = 1 AND Courses.CourseID = 2;

-- Select all Questions for a certain course
SELECT Q.*
FROM Questions Q
    JOIN hasCC HCC ON Q.FK_Category_ID = HCC.CategoryID
    JOIN Courses C ON HCC.CourseID = C.CourseID
WHERE C.CourseName = 'MACS1';

-- Select all Keywords for a certain Question
Select Keywords.* FROM Keywords
    JOIN hasKQ ON Keywords.KeywordID = hasKQ.KeywordID
    JOIN Questions ON hasKQ.QuestionID = Questions.QuestionID
WHERE hasKQ.QuestionID = 1;

-- Select all Images for a certain Question
SELECT Images.ImageID, Link, ImageName, Position
FROM Images
JOIN hasIQ ON Images.ImageID = hasIQ.ImageID
JOIN Questions ON hasIQ.QuestionID = Questions.QuestionID
WHERE hasIQ.QuestionID = 1;

-- Select all Categories for a certain Course
Select Categories.CategoryID, Category FROM Categories
JOIN hasCC ON Categories.CategoryID = hasCC.CategoryID
JOIN Courses ON hasCC.CourseID = Courses.CourseID
WHERE hasCC.CourseID = 6;

-----------------------------------------------------------------------------
-- Create statements
CREATE TABLE Categories (
    CategoryID INTEGER PRIMARY KEY,
    Category TEXT NOT NULL
);

CREATE TABLE Courses(
    CourseID INTEGER PRIMARY KEY,
    CourseName TEXT NOT NULL,
    CourseNumber INTEGER NOT NULL,
    Lector TEXT NOT NULL
);

CREATE TABLE StudyPrograms(
          ProgramID INTEGER PRIMARY KEY,
          ProgramName TEXT NOT NULL,
          ProgramAbbreviation TEXT NOT NULL
);

CREATE TABLE Images(
   ImageID INTEGER PRIMARY KEY,
   Link TEXT NOT NULL,
   ImageName TEXT,
   Position INTEGER NOT NULL
);

CREATE TABLE Keywords(
     KeywordID INTEGER PRIMARY KEY,
     Keyword TEXT NOT NULL
);

CREATE TABLE Questions (
       QuestionID INTEGER PRIMARY KEY,
       FK_Category_ID INTEGER NOT NULL,
       Difficulty INTEGER NOT NULL,
       Points FLOAT NOT NULL,
       Question TEXT NOT NULL,
       MultipleChoice INTEGER NOT NULL,
       Language TEXT NOT NULL,
       Remarks TEXT,
       Answers TEXT,
       FOREIGN KEY (FK_Category_ID) REFERENCES Categories(CategoryID)
);

CREATE TABLE hasSC(
  ProgramID INT,
  CourseID INT,
  PRIMARY KEY (ProgramID, CourseID),
  FOREIGN KEY (ProgramID) REFERENCES StudyPrograms(ProgramID),
  FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

CREATE TABLE hasCC(
  CourseID INT,
  CategoryID INT,
  PRIMARY KEY (CourseID, CategoryID),
  FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
  FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);

CREATE TABLE hasIQ(
  ImageID INT,
  QuestionID INT,
  PRIMARY KEY (ImageID, QuestionID),
  FOREIGN KEY (ImageID) REFERENCES Images(ImageID),
  FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);

CREATE TABLE hasKQ(
  KeywordID INT,
  QuestionID INT,
  PRIMARY KEY (KeywordID, QuestionID),
  FOREIGN KEY (KeywordID) REFERENCES Keywords(KeywordID),
  FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);
-----------------------------------------------------------------------------
-- Insert statements
INSERT INTO Categories (Category) VALUES('SQL-Datenbanken');
INSERT INTO Categories (Category) VALUES('Analysis');
INSERT INTO Categories (Category) VALUES('NoSQL');
INSERT INTO Categories (Category) VALUES('Algebra');

INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES('MACS1',11112,'macs1 lektor');
INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES('MACS2',11113,'macs2 lektor');
INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES('Verteilte Systeme',11114,'VERTS lektor');
INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES('MACS3',11123,'macs3 lektor');
INSERT INTO Courses (CourseName, CourseNumber, Lector) VALUES('Datenmanagement',112342,'DM lektor');

INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES('Bachelor Informatik','BIF');
INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES('Master Tissue Engineering and Regenerative medicine','MTE');
INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES('Biomedical Engineering','BME');
INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES('Wirtschaftinformatik','WIF');
INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation) VALUES('Master AI Engineering','MAE');

INSERT INTO Images (Link, ImageName, Position) VALUES('/fotos/foto.png','grafik1',0);
INSERT INTO Images (Link, ImageName, Position) VALUES('/fotos/graph.png','quadratische Funktion',0);

INSERT INTO Keywords (Keyword) VALUES('Integralrechnung');
INSERT INTO Keywords (Keyword) VALUES('SQL-Statements');
INSERT INTO Keywords (Keyword) VALUES('Gruppe/Körper');
INSERT INTO Keywords (Keyword) VALUES('Threads');
INSERT INTO Keywords (Keyword) VALUES('IPC-Kommunikation');
INSERT INTO Keywords (Keyword) VALUES('Pipes');
INSERT INTO Keywords (Keyword) VALUES('Vektoren');
INSERT INTO Keywords (Keyword) VALUES('Heine-Matrix');
INSERT INTO Keywords (Keyword) VALUES('Eigenwert');
INSERT INTO Keywords (Keyword) VALUES('NoSQL');

INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(4,6,7.5,'Berechnen Sie die Eigenwerte der Matrix:',0,'Deutsch',NULL,NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2,8,7,'Berechnen Sie die zweite Ableitung der Funktion: f(x) = x²',0,'Deutsch',NULL,NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(3,4,3.5,'Welche No-SQL datenbanken gibt es',0,'Deutsch',NULL,NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2,8,7,'Berechnen Sie die zweite Ableitung der Funktion: f(x) = x² + 2',0,'Deutsch',NULL,NULL);
VALUES(1, 6, 8.0, 'Was ist der Unterschied zwischen INNER JOIN und LEFT JOIN in SQL?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(3, 7, 9.5, 'Nennen Sie drei Vorteile von NoSQL-Datenbanken gegenüber relationalen Datenbanken.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2, 5, 6.5, 'Lösen Sie die folgende Gleichung: 2x + 5 = 15', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(4, 8, 7.0, 'Definieren Sie den Begriff "Eigenvector" in der linearen Algebra.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(1, 4, 5.5, 'Was ist der Zweck von SQL-Indizes?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(3, 6, 8.0, 'Erklären Sie den Begriff "Sharding" in Bezug auf NoSQL-Datenbanken.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2, 7, 9.0, 'Berechnen Sie das bestimmte Integral von sin(x) von 0 bis pi.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(4, 5, 6.0, 'Was sind Gruppen in der Algebra?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(1, 8, 7.5, 'Erklären Sie den Begriff "Subquery" in SQL.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(3, 4, 4.5, 'Welche NoSQL-Datenbank wird häufig für die Speicherung von Dokumenten verwendet?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2, 6, 7.0, 'Was ist die Ableitung von ln(x)?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(4, 7, 8.5, 'Erläutern Sie den Begriff "Polynomdivision" in der Algebra.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(3, 5, 6.5, 'Was sind die Vor- und Nachteile von dokumentenorientierten NoSQL-Datenbanken?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(1, 8, 9.0, 'Wie erstellt man eine gespeicherte Prozedur in SQL?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2, 4, 5.0, 'Lösen Sie die Gleichungssysteme: 2x + y = 5 und 3x - 2y = 1', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(4, 6, 7.5, 'Was ist eine invertierbare Matrix?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(3, 7, 8.0, 'Erklären Sie den Begriff "Caching" in Bezug auf NoSQL-Datenbanken.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(1, 5, 6.0, 'Was sind Joins in SQL?', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(2, 8, 9.5, 'Berechnen Sie das unbestimmte Integral von e^(2x)dx.', 0, 'Deutsch', NULL, NULL);
INSERT INTO Questions (FK_Category_ID, Difficulty, Points, Question, MultipleChoice, "Language", Remarks, Answers)
VALUES(4, 4, 5.5, 'Was sind komplexe Zahlen?', 0, 'Deutsch', NULL, NULL);

INSERT INTO hasSC VALUES(1,1);
INSERT INTO hasSC VALUES(1,2);
INSERT INTO hasSC VALUES(1,3);
INSERT INTO hasSC VALUES(1,4);
INSERT INTO hasSC VALUES(1,5);
INSERT INTO hasSC VALUES(4,1);
INSERT INTO hasSC VALUES(4,2);
INSERT INTO hasSC VALUES(4,5);

INSERT INTO hasCC VALUES(1,2);
INSERT INTO hasCC VALUES(1,4);
INSERT INTO hasCC VALUES(2,2);
INSERT INTO hasCC VALUES(2,4);
INSERT INTO hasCC VALUES(5,1);
INSERT INTO hasCC VALUES(5,3);

INSERT INTO hasIQ VALUES(2,1);
INSERT INTO hasIQ VALUES(1,3);

INSERT INTO hasKQ VALUES(7,1);
INSERT INTO hasKQ VALUES(8,1);
INSERT INTO hasKQ VALUES(9,1);
INSERT INTO hasKQ VALUES(1,2);
INSERT INTO hasKQ VALUES(2,3);
INSERT INTO hasKQ VALUES(3, 23);
INSERT INTO hasKQ VALUES(1, 22);
INSERT INTO hasKQ VALUES(2, 21);
INSERT INTO hasKQ VALUES(10, 20);
INSERT INTO hasKQ VALUES(7, 19);
INSERT INTO hasKQ VALUES(1, 18);
INSERT INTO hasKQ VALUES(2, 17);
INSERT INTO hasKQ VALUES(10, 16);
INSERT INTO hasKQ VALUES(3, 15);
INSERT INTO hasKQ VALUES(1, 14);
INSERT INTO hasKQ VALUES(10, 13);
INSERT INTO hasKQ VALUES(2, 12);
INSERT INTO hasKQ VALUES(3, 11);
INSERT INTO hasKQ VALUES(1, 10);
INSERT INTO hasKQ VALUES(10, 9);
INSERT INTO hasKQ VALUES(2, 8);
INSERT INTO hasKQ VALUES(9, 7);
INSERT INTO hasKQ VALUES(1, 6);
INSERT INTO hasKQ VALUES(10, 5);
INSERT INTO hasKQ VALUES(2, 5);
INSERT INTO hasKQ VALUES(2, 4);

-----------------------------------------------------------------------------
-- Dropping tables
-- Drop tables with foreign key relationships first
-- DROP TABLE IF EXISTS hasKQ;
-- DROP TABLE IF EXISTS hasIQ;
-- DROP TABLE IF EXISTS hasCC;
-- DROP TABLE IF EXISTS hasSC;
-- DROP TABLE IF EXISTS Questions;
-- DROP TABLE IF EXISTS Keywords;
-- DROP TABLE IF EXISTS Images;
-- DROP TABLE IF EXISTS StudyPrograms;
-- DROP TABLE IF EXISTS Courses;
-- DROP TABLE IF EXISTS Categories;