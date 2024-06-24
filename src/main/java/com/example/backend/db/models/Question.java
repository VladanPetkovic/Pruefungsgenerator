package com.example.backend.db.models;

import com.example.backend.db.SQLiteDatabaseConnection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question implements Serializable {
    private int id;
    private Category category;
    private int difficulty;
    private float points;
    private String question;
    private QuestionType type;
    private String remark;
    private Timestamp created_at;
    private Timestamp updated_at;
    private ArrayList<Answer> answers;
    private ArrayList<Keyword> keywords;
    private ArrayList<Image> images;

    public Question(Category category, int difficulty, float points, String question,
                    QuestionType type, String remark, Timestamp created_at,
                    Timestamp updated_at, ArrayList<Answer> answers,
                    ArrayList<Keyword> keywords, ArrayList<Image> images) {
        setCategory(category);
        setDifficulty(difficulty);
        setPoints(points);
        setQuestion(question);
        setType(type);
        setRemark(remark);
        setCreated_at(created_at);
        setUpdated_at(updated_at);
        setAnswers(answers);
        setKeywords(keywords);
        setImages(images);
    }

    public String getAnswersAsString() {
        StringBuilder answersCombined = new StringBuilder();

        // return the first answer of answers, if only one is available
        if (answers.size() == 1) {
            return answers.get(0).getAnswer();
        }

        for (Answer answer : answers) {
            if (!Objects.equals(answer.getAnswer(), "")) {
                answersCombined.append(answer.getAnswer());
                answersCombined.append("\n");
            }
        }

        return String.valueOf(answersCombined);
    }

    /**
     * Returns the created question-id.
     * @param question provided question for creation
     * @return id of the created question
     */
    public static int createNewQuestionInDatabase(Question question)  throws IOException  {
        SQLiteDatabaseConnection.QUESTION_REPOSITORY.add(question);
        // get the created question_id
        int new_question_id = SQLiteDatabaseConnection.QUESTION_REPOSITORY.getMaxQuestionId();
        // create one or multiple answers
        Answer.createAnswers(question, new_question_id);
        // create one or multiple keywords
        Keyword.createKeywords(question, new_question_id);
        // create one or multiple images
        Image.createImages(question, new_question_id);
        return new_question_id;
    }

    /**
     * This function returns the formatted string of a timestamp (created_at, updated_at)
     * @param time the time to format
     * @return A String, which we can use in the frontend
     */
    public String getTimeStampFormatted(Timestamp time) {
        Instant instant = time.toInstant();
        LocalDateTime updatedAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return updatedAt.format(formatter);
    }

    public void removeDuplicates() {
        ArrayList<Answer> uniqueAnswers = new ArrayList<>();
        for (Answer answer : this.answers) {
            if (!containsAnswerWithId(answer.getId(), uniqueAnswers)) {
                uniqueAnswers.add(answer);
            }
        }
        this.answers = uniqueAnswers;

        ArrayList<Keyword> uniqueKeywords = new ArrayList<>();
        for (Keyword keyword : this.keywords) {
            if (!containsKeywordWithId(keyword.getId(), uniqueKeywords)) {
                uniqueKeywords.add(keyword);
            }
        }
    }

    private boolean containsAnswerWithId(int answer_id, ArrayList<Answer> answers) {
        for (Answer answer : answers) {
            if (answer.getId() == answer_id) {
                return true;
            }
        }
        return false;
    }
    private boolean containsKeywordWithId(int keyword_id, ArrayList<Keyword> keywords) {
        for (Keyword keyword : keywords) {
            if (keyword.getId() == keyword_id) {
                return true;
            }
        }
        return false;
    }
}
