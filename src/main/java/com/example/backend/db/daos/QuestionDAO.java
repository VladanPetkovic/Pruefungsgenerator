package com.example.backend.db.daos;

import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class QuestionDAO implements DAO<Question> {
    // questionCache needed for staging questions before sending them to the user
    ArrayList<Question> questionCache;
    public QuestionDAO() {
        this.questionCache = new ArrayList<>();
    }

    @Override
    public void create(Question question) {
        String insertStmt =
                "INSERT INTO Questions " +
                "(FK_Category_ID, Difficulty, Points, Question, MultipleChoice, Language, Remarks, Answers) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setInt(1, question.getCategory().getCategory_id());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setFloat(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestionString());
            preparedStatement.setBoolean(5, question.getMultipleChoice());
            preparedStatement.setString(6, question.getLanguage());
            preparedStatement.setString(7, question.getRemarks());
            preparedStatement.setString(8, question.getAnswers());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Question> readAll() {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectQuestionsStmt =
            "SELECT Q.QuestionID, Q.FK_Category_ID, Q.Difficulty, Q.Points, Q.Question, " +
            "       Q.MultipleChoice, Q.Language, Q.Remarks, Q.Answers, " +
            "       C.Category, I.ImageID, I.Link, I.ImageName, I.Position, " +
            "       K.KeywordID, K.Keyword " +
            "FROM Questions Q " +
            "         JOIN Categories C ON Q.FK_Category_ID = C.CategoryID " +
            "         LEFT JOIN hasIQ HIQ ON Q.QuestionID = HIQ.QuestionID " +
            "         LEFT JOIN Images I ON HIQ.ImageID = I.ImageID " +
            "         LEFT JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID " +
            "         LEFT JOIN Keywords K ON HKQ.KeywordID = K.KeywordID;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt);
             ResultSet questionsResultSet = questionsStatement.executeQuery()) {

            while (questionsResultSet.next()) {
                Question newQuestion = createModelFromResultSet(questionsResultSet);
                if(newQuestion != null) {
                    this.questionCache.add(newQuestion);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.questionCache;
    }

    public ArrayList<Question> readAll(Category category) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectQuestionsStmt =
            "SELECT Q.QuestionID, Q.FK_Category_ID, Q.Difficulty, Q.Points, Q.Question, " +
                "Q.MultipleChoice, Q.Language, Q.Remarks, Q.Answers, " +
                "C.Category, I.ImageID, I.Link, I.ImageName, I.Position, " +
                "K.KeywordID, K.Keyword " +
            "FROM Questions Q " +
                "JOIN Categories C ON Q.FK_Category_ID = C.CategoryID " +
                "LEFT JOIN hasIQ HIQ ON Q.QuestionID = HIQ.QuestionID " +
                "LEFT JOIN Images I ON HIQ.ImageID = I.ImageID " +
                "LEFT JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID " +
                "LEFT JOIN Keywords K ON HKQ.KeywordID = K.KeywordID " +
            "WHERE FK_Category_ID = ?;";

        if (category != null) {
            try (Connection connection = SQLiteDatabaseConnection.connect();
                 PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt)) {

                questionsStatement.setInt(1, category.getCategory_id());
                try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                    while (questionsResultSet.next()) {
                        Question newQuestion = createModelFromResultSet(questionsResultSet);
                        if(newQuestion != null) {
                            this.questionCache.add(newQuestion);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return this.questionCache;
    }

    public ArrayList<Question> readAll(Course course) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectQuestionsStmt =
            "SELECT Q.QuestionID, Q.FK_Category_ID, Q.Difficulty, Q.Points, Q.Question, " +
                "Q.MultipleChoice, Q.Language, Q.Remarks, Q.Answers, " +
                "C.Category, I.ImageID, I.Link, I.ImageName, I.Position, " +
                "K.KeywordID, K.Keyword " +
            "FROM Questions Q " +
                "JOIN Categories C ON Q.FK_Category_ID = C.CategoryID " +
                "LEFT JOIN hasIQ HIQ ON Q.QuestionID = HIQ.QuestionID " +
                "LEFT JOIN Images I ON HIQ.ImageID = I.ImageID " +
                "LEFT JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID " +
                "LEFT JOIN Keywords K ON HKQ.KeywordID = K.KeywordID " +
                "LEFT JOIN hasCC HCC ON Q.FK_Category_ID = HCC.CategoryID " +
                "LEFT JOIN Courses Co ON HCC.CourseID = Co.CourseID " +
            "WHERE Co.CourseID = ?";

        if (course != null) {
            try (Connection connection = SQLiteDatabaseConnection.connect();
                 PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt)) {

                questionsStatement.setInt(1, course.getCourse_id());
                try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                    while (questionsResultSet.next()) {
                        Question newQuestion = createModelFromResultSet(questionsResultSet);
                        if(newQuestion != null) {
                            this.questionCache.add(createModelFromResultSet(questionsResultSet));
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return this.questionCache;
    }

    public ArrayList<Question> readAll(ArrayList<SearchObject<?>> searchOptions, Course course) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        // making a list of values for the preparedStmt
        ArrayList<Object> listForPreparedStmt = new ArrayList<>();

        // doing this for performance --> avoiding "n+1-select"
        StringBuilder selectQuestionsStmt = new StringBuilder(
            "SELECT Q.QuestionID, Q.FK_Category_ID, Q.Difficulty, Q.Points, Q.Question, " +
                "Q.MultipleChoice, Q.Language, Q.Remarks, Q.Answers, " +
                "C.Category, I.ImageID, I.Link, I.ImageName, I.Position, " +
                "K.KeywordID, K.Keyword " +
            "FROM Questions Q " +
                "JOIN Categories C ON Q.FK_Category_ID = C.CategoryID " +
                "LEFT JOIN hasIQ HIQ ON Q.QuestionID = HIQ.QuestionID " +
                "LEFT JOIN Images I ON HIQ.ImageID = I.ImageID " +
                "LEFT JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID " +
                "LEFT JOIN Keywords K ON HKQ.KeywordID = K.KeywordID " +
            "WHERE ");

        // init selectSTMT and listForPreparedStmt
        prepareQuery(searchOptions, selectQuestionsStmt, listForPreparedStmt);

        System.out.println(selectQuestionsStmt);

        // if no searchOptions passed --> return all Questions for the Course
        if(listForPreparedStmt.isEmpty()) {
            return readAll(course);
        }

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement questionsStatement = connection.prepareStatement(String.valueOf(selectQuestionsStmt))) {

            // insert into prepared stmt
            int count = 1;
            for(Object prepObjects : listForPreparedStmt) {
                if(prepObjects instanceof String) {
                    questionsStatement.setString(count, (String) prepObjects);
                } else if(prepObjects instanceof Integer) {
                    questionsStatement.setInt(count, (int) prepObjects);
                } else if(prepObjects instanceof Float) {
                    questionsStatement.setFloat(count, (Float) prepObjects);
                } else if(prepObjects instanceof Category) {
                    questionsStatement.setInt(count, (int) ((Category) prepObjects).getCategory_id());
                }

                count++;
            }

            try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                while (questionsResultSet.next()) {
                    Question newQuestion = createModelFromResultSet(questionsResultSet);
                    if(newQuestion != null) {
                        this.questionCache.add(createModelFromResultSet(questionsResultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.questionCache;
    }

    public void prepareQuery(ArrayList<SearchObject<?>> searchOptions, StringBuilder stmt, ArrayList<Object> listForPreparedStmt) {
        int countKeywords = 0;
        int countImages = 0;

        for(SearchObject<?> searchObject : searchOptions) {
            // append only objects with set flag and a columnName (otherwise we would insert into non-existing columns)
            if(searchObject.isSet() && !Objects.equals(searchObject.getColumn_name(), "")) {
                stmt.append(" ").append(searchObject.getColumn_name()).append(" = ? AND");
                listForPreparedStmt.add(searchObject.getValueOfObject());
            }

            // keywords passed
            if(searchObject.isSet() && Objects.equals(searchObject.getObjectName(), "keywords")) {
                ArrayList<Keyword> keywords = (ArrayList<Keyword>) searchObject.getValueOfObject();
                stmt.append("(");
                while(keywords.size() > countKeywords) {
                    stmt.append(" ").append("K.Keyword").append(" = ? OR");
                    listForPreparedStmt.add(keywords.get(countKeywords).getKeyword_text());
                    countKeywords++;
                }
                stmt.delete(stmt.length() - 3, stmt.length());
                stmt.append(")").append(" AND");
            }

            // images go like keywords
            if(searchObject.isSet() && Objects.equals(searchObject.getObjectName(), "images")) {
                ArrayList<Image> images = (ArrayList<Image>) searchObject.getValueOfObject();
                stmt.append("(");
                while(images.size() > countImages) {
                    stmt.append(" ").append("I.ImageName").append(" = ? OR");
                    listForPreparedStmt.add(images.get(countImages).getImageName());
                    countImages++;
                }
                stmt.delete(stmt.length() - 3, stmt.length());
                stmt.append(")").append(" AND");
            }

        }

        // replace last ' AND' to ';'
        stmt.delete(stmt.length() - 4, stmt.length());
        stmt.append(';');
    }

    @Override
    public Question read(int questionId) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectStmt =
            "SELECT Q.QuestionID, Q.FK_Category_ID, Q.Difficulty, Q.Points, Q.Question, " +
                "Q.MultipleChoice, Q.Language, Q.Remarks, Q.Answers, " +
                "C.Category, I.ImageID, I.Link, I.ImageName, I.Position, " +
                "K.KeywordID, K.Keyword " +
            "FROM Questions Q " +
                "JOIN Categories C ON Q.FK_Category_ID = C.CategoryID " +
                "LEFT JOIN hasIQ HIQ ON Q.QuestionID = HIQ.QuestionID " +
                "LEFT JOIN Images I ON HIQ.ImageID = I.ImageID " +
                "LEFT JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID " +
                "LEFT JOIN Keywords K ON HKQ.KeywordID = K.KeywordID " +
            "WHERE Q.QuestionID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {

            preparedStatement.setInt(1, questionId);
            try (ResultSet questionsResultSet = preparedStatement.executeQuery()) {
                while (questionsResultSet.next()) {
                    Question newQuestion = createModelFromResultSet(questionsResultSet);
                    if(newQuestion != null) {
                        this.questionCache.add(createModelFromResultSet(questionsResultSet));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!this.questionCache.isEmpty()) {
            return this.questionCache.get(0);
        }

        return null;
    }

    @Override
    public void update(Question question) {
        String updateStmt =
                "UPDATE Questions " +
                        "SET FK_Category_ID = ?, Difficulty = ?, Points = ?, Question = ?, " +
                        "MultipleChoice = ?, Language = ?, Remarks = ?, Answers = ? " +
                        "WHERE QuestionID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setInt(1, question.getCategory().getCategory_id());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setFloat(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestionString());
            preparedStatement.setBoolean(5, question.getMultipleChoice());
            preparedStatement.setString(6, question.getLanguage());
            preparedStatement.setString(7, question.getRemarks());
            preparedStatement.setString(8, question.getAnswers());
            preparedStatement.setInt(9, question.getQuestion_id());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM Questions WHERE QuestionID = ?;";
        String deleteHasIQStmt = "DELETE FROM hasIQ WHERE QuestionID = ?;";
        String deleteHasKQStmt = "DELETE FROM hasKQ WHERE QuestionID = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasIQStmt);
             PreparedStatement thirdPreparedStatement = connection.prepareStatement(deleteHasKQStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();

            thirdPreparedStatement.setInt(1, id);
            thirdPreparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Question createModelFromResultSet(ResultSet resultSet) throws SQLException {
        int question_id = resultSet.getInt("QuestionID");

        // checking, if question exists in our questionCache
        for(Question question : this.questionCache) {
            // adding keyword and image if one question has multiple keywords and images
            if(question.getQuestion_id() == question_id) {
                // keyword not null
                if(resultSet.getInt("KeywordID") != 0) {
                    Keyword newKeyword = new Keyword(
                            resultSet.getInt("KeywordID"),
                            resultSet.getString("Keyword")
                    );
                    question.getKeywords().add(newKeyword);
                }
                // image not null
                if(resultSet.getInt("ImageID") != 0) {
                    Image newImage = new Image(
                            resultSet.getInt("ImageID"),
                            resultSet.getString("Link"),
                            resultSet.getString("ImageName"),
                            resultSet.getInt("Position")
                    );
                    question.getImages().add(newImage);
                }
                // we do not want to create a duplicate question
                // so, we return here
                return null;
            }
        }

        Category questionCategory = new Category(
                resultSet.getInt("FK_Category_ID"),
                resultSet.getString("Category"));

        ArrayList<Keyword> keywords = new ArrayList<>();
        Keyword newKeyword = new Keyword(
                resultSet.getInt("KeywordID"),
                resultSet.getString("Keyword")
        );
        keywords.add(newKeyword);

        ArrayList<Image> images = new ArrayList<>();
        Image newImage = new Image(
                resultSet.getInt("ImageID"),
                resultSet.getString("Link"),
                resultSet.getString("ImageName"),
                resultSet.getInt("Position")
        );
        images.add(newImage);

        return new Question(
                question_id,
                questionCategory,
                resultSet.getInt("Difficulty"),
                resultSet.getFloat("Points"),
                resultSet.getString("Question"),
                resultSet.getBoolean("MultipleChoice"),
                resultSet.getString("Language"),
                resultSet.getString("Remarks"),
                resultSet.getString("Answers"),
                keywords,
                images
        );
    }
}
