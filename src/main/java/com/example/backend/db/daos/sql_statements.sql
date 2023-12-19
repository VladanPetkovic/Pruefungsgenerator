-----------------------------------------------------------------------------
-- Selecting

-- Select all Courses for a certain studyprogram
Select * FROM Courses
JOIN hasSC ON Courses.CourseID = hasSC.CourseID
JOIN StudyPrograms ON hasSC.ProgramID = StudyPrograms.ProgramID
WHERE hasSC.ProgramID = 1;

-- Select all Questions for a certain studyprogram and a certain Course
SELECT Questions.*, Courses.CourseName, StudyPrograms.ProgramAbbreviation
FROM Questions
    JOIN hasCT ON Questions.FK_Topic_ID = hasCT.TopicID
    JOIN Courses ON hasCT.CourseID = Courses.CourseID
    JOIN hasSC ON Courses.CourseID = hasSC.CourseID
    JOIN StudyPrograms ON hasSC.ProgramID = StudyPrograms.ProgramID
WHERE StudyPrograms.ProgramID = 1 AND Courses.CourseID = 2;

-- Select all Keywords for a certain Question
Select Keywords.* FROM Keywords
    JOIN hasKQ ON Keywords.KeywordID = hasKQ.KeywordID
    JOIN Questions ON hasKQ.QuestionID = Questions.QuestionID
WHERE hasKQ.QuestionID = 1;

-----------------------------------------------------------------------------
-- Creating tables
CREATE TABLE IF NOT EXISTS Topics (
    TopicID INTEGER PRIMARY KEY,
    Topic TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Courses(
    CourseID INTEGER PRIMARY KEY,
    CourseName TEXT NOT NULL,
    CourseNumber INTEGER NOT NULL,
    Lector TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS StudyPrograms(
    ProgramID INTEGER PRIMARY KEY,
    ProgramName TEXT NOT NULL,
    ProgramAbbreviation TEXT
);

CREATE TABLE IF NOT EXISTS Images(
    ImageID INTEGER PRIMARY KEY,
    Link TEXT NOT NULL,
    Imagename TEXT,
    Position INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS Keywords(
    KeywordID INTEGER PRIMARY KEY,
    Keyword TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Questions (
    QuestionID INTEGER PRIMARY KEY,
    FK_Topic_ID INTEGER NOT NULL,
    Difficulty INTEGER NOT NULL,
    Points INTEGER NOT NULL,
    Question TEXT NOT NULL,
    MultipleChoice INT NOT NULL,
    Language TEXT NOT NULL,
    Remarks TEXT,
    Answers TEXT,
    FOREIGN KEY (FK_Topic_ID) REFERENCES Topics(TopicID)
);


CREATE TABLE IF NOT EXISTS hasSC(
    ProgramID INT,
    CourseID INT,
    PRIMARY KEY (ProgramID, CourseID),
    FOREIGN KEY (ProgramID) REFERENCES StudyPrograms(ProgramID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

CREATE TABLE IF NOT EXISTS hasCT(
    CourseID INT,
    TopicID INT,
    PRIMARY KEY (CourseID, TopicID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
    FOREIGN KEY (TopicID) REFERENCES Topics(TopicID)
);

CREATE TABLE IF NOT EXISTS hasIQ(
    ImageID INT,
    QuestionID INT,
    PRIMARY KEY (ImageID, QuestionID),
    FOREIGN KEY (ImageID) REFERENCES Images(ImageID),
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);


CREATE TABLE IF NOT EXISTS hasKQ(
    KeywordID INT,
    QuestionID INT,
    PRIMARY KEY (KeywordID, QuestionID),
    FOREIGN KEY (KeywordID) REFERENCES Keywords(KeywordID),
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);

-----------------------------------------------------------------------------
-- Insert statements --> already used
INSERT INTO Topics (Topic)
VALUES ('Analysis');

INSERT INTO Courses (CourseName, CourseNumber, Lector)
VALUES ('Datenbanken', 11111, 'datenbanken lektor');

INSERT INTO StudyPrograms (ProgramName, ProgramAbbreviation)
VALUES ('Master Tissue Engineering and Regenerative medicine', 'MTE');

INSERT INTO Images (Link, Imagename, Position)
VALUES ('/fotos/graph.png', 'quadratische Funktion', 0);

INSERT INTO Keywords (Keyword)
VALUES ('Integralrechnung'), ('SQL-Statements'), ('Gruppe/Körper'),
       ('Threads'), ('IPC-Kommunikation'), ('Pipes'),
       ('Vektoren'), ('Heine-Matrix'), ('Eigenwert');

INSERT INTO Questions (FK_Topic_ID, Difficulty, Points, Question, MultipleChoice, Language)
VALUES (
           (SELECT TopicID FROM Topics WHERE Topic = 'Algebra'),
           6, 7, 'Berechnen Sie die Eigenwerte der Matrix:', false, 'Deutsch'
       );

INSERT INTO hasSC (ProgramID, CourseID)
VALUES
    ((SELECT ProgramID FROM StudyPrograms WHERE ProgramAbbreviation = 'BIF'),
     (SELECT CourseID FROM Courses WHERE CourseName = 'MACS1')),
    ((SELECT ProgramID FROM StudyPrograms WHERE ProgramAbbreviation = 'BIF'),
     (SELECT CourseID FROM Courses WHERE CourseName = 'MACS2')),
    ((SELECT ProgramID FROM StudyPrograms WHERE ProgramAbbreviation = 'BIF'),
     (SELECT CourseID FROM Courses WHERE CourseName = 'MACS3')),
    ((SELECT ProgramID FROM StudyPrograms WHERE ProgramAbbreviation = 'BIF'),
     (SELECT CourseID FROM Courses WHERE CourseName = 'Verteilte Systeme'));

INSERT INTO hasCT (CourseID, TopicID)
VALUES
    ((SELECT CourseID FROM Courses WHERE CourseName = 'MACS1'),
     (SELECT TopicID FROM Topics WHERE Topic = 'Algebra')),
    ((SELECT CourseID FROM Courses WHERE CourseName = 'MACS2'),
     (SELECT TopicID FROM Topics WHERE Topic = 'Algebra')),
    ((SELECT CourseID FROM Courses WHERE CourseName = 'MACS1'),
     (SELECT TopicID FROM Topics WHERE Topic = 'Analysis')),
    ((SELECT CourseID FROM Courses WHERE CourseName = 'MACS2'),
     (SELECT TopicID FROM Topics WHERE Topic = 'Analysis')),
    ((SELECT CourseID FROM Courses WHERE CourseName = 'Datenmanagement'),
     (SELECT TopicID FROM Topics WHERE Topic = 'SQL-Datenbanken')),
    ((SELECT CourseID FROM Courses WHERE CourseName = 'Datenmanagement'),
     (SELECT TopicID FROM Topics WHERE Topic = 'NoSQL'));

INSERT INTO hasIQ (ImageID, QuestionID)
VALUES
    ((SELECT ImageID FROM Images WHERE Imagename = 'grafik1'),
     (SELECT QuestionID FROM Questions WHERE QuestionID = 1)),
    ((SELECT ImageID FROM Images WHERE Imagename = 'quadratische Funktion'),
     (SELECT QuestionID FROM Questions WHERE QuestionID = 2));

INSERT INTO hasKQ (KeywordID, QuestionID)
VALUES
    ((SELECT KeywordID FROM Keywords WHERE Keyword = 'Integralrechnung'),
     2),
    ((SELECT KeywordID FROM Keywords WHERE Keyword = 'Vektoren'),
     1),
    ((SELECT KeywordID FROM Keywords WHERE Keyword = 'Eigenwert'),
     1),
    ((SELECT KeywordID FROM Keywords WHERE Keyword = 'SQL-Statements'),
     3);
-----------------------------------------------------------------------------
-- Deleting
DELETE FROM Topics
WHERE TopicID = 3;