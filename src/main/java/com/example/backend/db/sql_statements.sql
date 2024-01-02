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

-- Select all Images for a certain Question
SELECT Images.ImageID, Link, Imagename, Position
FROM Images
JOIN hasIQ ON Images.ImageID = hasIQ.ImageID
JOIN Questions ON hasIQ.QuestionID = Questions.QuestionID
WHERE hasIQ.QuestionID = 1;

-- Select all Topics for a certain Course
Select Topics.TopicID, Topic FROM Topics
JOIN hasCT ON Topics.TopicID = hasCT.TopicID
JOIN Courses ON hasCT.CourseID = Courses.CourseID
WHERE hasCT.CourseID = 6;

-----------------------------------------------------------------------------
-- Insert and Create statements
CREATE TABLE Topics (
    TopicID INTEGER PRIMARY KEY,
    Topic TEXT NOT NULL
);
INSERT INTO Topics VALUES(1,'SQL-Datenbanken');
INSERT INTO Topics VALUES(2,'Analysis');
INSERT INTO Topics VALUES(4,'NoSQL');
INSERT INTO Topics VALUES(5,'Algebra');
CREATE TABLE Courses(
    CourseID INTEGER PRIMARY KEY,
    CourseName TEXT NOT NULL,
    CourseNumber INTEGER NOT NULL,
    Lector TEXT NOT NULL
);
INSERT INTO Courses VALUES(2,'MACS1',11112,'macs1 lektor');
INSERT INTO Courses VALUES(3,'MACS2',11113,'macs2 lektor');
INSERT INTO Courses VALUES(4,'Verteilte Systeme',11114,'VERTS lektor');
INSERT INTO Courses VALUES(5,'MACS3',11123,'macs3 lektor');
INSERT INTO Courses VALUES(6,'Datenmanagement',112342,'DM lektor');
CREATE TABLE StudyPrograms(
          ProgramID INTEGER PRIMARY KEY,
          ProgramName TEXT NOT NULL,
          ProgramAbbreviation TEXT
);
INSERT INTO StudyPrograms VALUES(1,'Bachelor Informatik','BIF');
INSERT INTO StudyPrograms VALUES(2,'Master Tissue Engineering and Regenerative medicine','MTE');
INSERT INTO StudyPrograms VALUES(5,'Biomedical Engineering','BME');
INSERT INTO StudyPrograms VALUES(6,'Wirtschaftinformatik','WIF');
CREATE TABLE Images(
   ImageID INTEGER PRIMARY KEY,
   Link TEXT NOT NULL,
   Imagename TEXT,
   Position INTEGER NOT NULL
);
INSERT INTO Images VALUES(1,'/fotos/foto.png','grafik1',0);
INSERT INTO Images VALUES(2,'/fotos/graph.png','quadratische Funktion',0);
CREATE TABLE Keywords(
     KeywordID INTEGER PRIMARY KEY,
     Keyword TEXT NOT NULL
);
INSERT INTO Keywords VALUES(1,'Integralrechnung');
INSERT INTO Keywords VALUES(2,'SQL-Statements');
INSERT INTO Keywords VALUES(3,'Gruppe/Körper');
INSERT INTO Keywords VALUES(4,'Threads');
INSERT INTO Keywords VALUES(5,'IPC-Kommunikation');
INSERT INTO Keywords VALUES(6,'Pipes');
INSERT INTO Keywords VALUES(7,'Vektoren');
INSERT INTO Keywords VALUES(8,'Heine-Matrix');
INSERT INTO Keywords VALUES(9,'Eigenwert');
CREATE TABLE Questions (
       QuestionID INTEGER PRIMARY KEY,
       FK_Topic_ID INTEGER NOT NULL,
       Difficulty TEXT NOT NULL,
       Points INTEGER NOT NULL,
       Question TEXT NOT NULL,
       MultipleChoice INT NOT NULL,
       Language TEXT NOT NULL,
       Remarks TEXT,
       Answers TEXT,
       FOREIGN KEY (FK_Topic_ID) REFERENCES Topics(TopicID)
);
INSERT INTO Questions VALUES(1,5,'6',7,'Berechnen Sie die Eigenwerte der Matrix:',0,'Deutsch',NULL,NULL);
INSERT INTO Questions VALUES(2,2,'8',7,'Berechnen Sie die zweite Ableitung der Funktion: f(x) = x²',0,'Deutsch',NULL,NULL);
INSERT INTO Questions VALUES(3,4,'4',3,'Welche No-SQL datenbanken gibt es',0,'Deutsch',NULL,NULL);
CREATE TABLE hasSC(
  ProgramID INT,
  CourseID INT,
  PRIMARY KEY (ProgramID, CourseID),
  FOREIGN KEY (ProgramID) REFERENCES StudyPrograms(ProgramID),
  FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
INSERT INTO hasSC VALUES(1,2);
INSERT INTO hasSC VALUES(1,3);
INSERT INTO hasSC VALUES(1,5);
INSERT INTO hasSC VALUES(1,4);
INSERT INTO hasSC VALUES(1,6);
CREATE TABLE hasCT(
  CourseID INT,
  TopicID INT,
  PRIMARY KEY (CourseID, TopicID),
  FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
  FOREIGN KEY (TopicID) REFERENCES Topics(TopicID)
);
INSERT INTO hasCT VALUES(2,5);
INSERT INTO hasCT VALUES(3,5);
INSERT INTO hasCT VALUES(2,2);
INSERT INTO hasCT VALUES(3,2);
INSERT INTO hasCT VALUES(6,1);
INSERT INTO hasCT VALUES(6,4);
CREATE TABLE hasIQ(
  ImageID INT,
  QuestionID INT,
  PRIMARY KEY (ImageID, QuestionID),
  FOREIGN KEY (ImageID) REFERENCES Images(ImageID),
  FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);
INSERT INTO hasIQ VALUES(1,1);
INSERT INTO hasIQ VALUES(2,2);
CREATE TABLE hasKQ(
  KeywordID INT,
  QuestionID INT,
  PRIMARY KEY (KeywordID, QuestionID),
  FOREIGN KEY (KeywordID) REFERENCES Keywords(KeywordID),
  FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);
INSERT INTO hasKQ VALUES(1,2);
INSERT INTO hasKQ VALUES(7,1);
INSERT INTO hasKQ VALUES(9,1);
INSERT INTO hasKQ VALUES(2,3);
-----------------------------------------------------------------------------
-- Deleting
DELETE FROM Topics
WHERE TopicID = 3;