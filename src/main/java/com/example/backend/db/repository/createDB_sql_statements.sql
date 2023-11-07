-- Create a database file or open an existing one

CREATE TABLE IF NOT EXISTS Topics (
    TopicID INTEGER PRIMARY KEY,
    Topic TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Images(
    ImageID INTEGER PRIMARY KEY,
    Link TEXT NOT NULL,
    Imagename TEXT
);

CREATE TABLE IF NOT EXISTS Keywords(
    KeywordID INTEGER PRIMARY KEY,
    Keyword TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Questions (
    QuestionID INTEGER PRIMARY KEY,
    FK_Topic_ID INTEGER NOT NULL,
    Difficulty TEXT NOT NULL,
    Points INTEGER NOT NULL,
    Question TEXT NOT NULL,
    MultipleChoice INT NOT NULL,
    Language TEXT NOT NULL,
    Remarks TEXT,
    FOREIGN KEY (FK_Topic_ID) REFERENCES Topics(TopicID)
);

CREATE TABLE IF NOT EXISTS has(
    ImageID INT,
    QuestionID INT,
    PRIMARY KEY (ImageID, QuestionID),
    FOREIGN KEY (ImageID) REFERENCES Images(ImageID),
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)
);
