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
    public QuestionDAO() {}

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
            preparedStatement.setInt(5, question.getMultipleChoice());
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
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt = "SELECT * FROM Questions;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt);
             ResultSet questionsResultSet = questionsStatement.executeQuery()) {

            while (questionsResultSet.next()) {
                questions.add(createModelFromResultSet(questionsResultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    public ArrayList<Question> readAll(Category category) {
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt = "SELECT * FROM Questions WHERE FK_Category_ID = ?;";

        if (category != null) {
            try (Connection connection = SQLiteDatabaseConnection.connect();
                 PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt)) {

                questionsStatement.setInt(1, category.getCategory_id());
                try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                    while (questionsResultSet.next()) {
                        questions.add(createModelFromResultSet(questionsResultSet));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return questions;
    }

    public ArrayList<Question> readAll(Course course) {
        ArrayList<Question> questions = new ArrayList<>();

        String selectQuestionsStmt =
                "SELECT Q.* " +
                "FROM Questions Q " +
                "JOIN hasCC HCC ON Q.FK_Category_ID = HCC.CategoryID " +
                "JOIN Courses C ON HCC.CourseID = C.CourseID " +
                "WHERE C.CourseID = ?;";

        if (course != null) {
            try (Connection connection = SQLiteDatabaseConnection.connect();
                 PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt)) {

                questionsStatement.setInt(1, course.getCourse_id());
                try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                    while (questionsResultSet.next()) {
                        questions.add(createModelFromResultSet(questionsResultSet));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return questions;
    }

    public ArrayList<Question> readAll(ArrayList<SearchObject<?>> searchOptions, Course course) {
        ArrayList<Question> questions = new ArrayList<>();
        // making a list of values for the preparedStmt
        ArrayList<Object> listForPreparedStmt = new ArrayList<>();

        StringBuilder selectQuestionsStmt = new StringBuilder("SELECT * FROM Questions WHERE");

        // TODO: make this a function
        for(SearchObject<?> searchObject : searchOptions) {
            // append only objects with set flag and a columnName (otherwise we would insert into non-existing columns)
            if(searchObject.isSet() && !Objects.equals(searchObject.getColumn_name(), "")) {
                selectQuestionsStmt.append(" ").append(searchObject.getColumn_name()).append(" = ? AND");
                listForPreparedStmt.add(searchObject.getValueOfObject());
            }

            // keywords passed
            if(searchObject.isSet() && Objects.equals(searchObject.getObjectName(), "keywords")) {
                int insertPosition = selectQuestionsStmt.indexOf("*");
                String oldWhereClause = selectQuestionsStmt.substring(selectQuestionsStmt.indexOf("WHERE"));
                selectQuestionsStmt.delete(insertPosition, selectQuestionsStmt.length());
                selectQuestionsStmt.append(
                        "Q.* " +
                        "FROM Questions Q " +
                        "JOIN hasKQ HKQ ON Q.QuestionID = HKQ.QuestionID " +
                        "JOIN Keywords K ON HKQ.KeywordID = K.KeywordID " + oldWhereClause);
                selectQuestionsStmt.append(" ").append("K.KeywordID").append(" = ? AND");
                ArrayList<Keyword> keywords = (ArrayList<Keyword>) searchObject.getValueOfObject();
                listForPreparedStmt.add(keywords.get(0).getKeyword_id());
            }

            // images go like keywords
        }

        // replace last ' AND' to ';'
        selectQuestionsStmt.delete(selectQuestionsStmt.length() - 4, selectQuestionsStmt.length());
        selectQuestionsStmt.append(';');

        System.out.println(selectQuestionsStmt);

        // if no searchOptions passed --> return all Questions for the Course
        if(listForPreparedStmt.isEmpty()) {
            return readAll();
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
                }

                count++;
            }

            try (ResultSet questionsResultSet = questionsStatement.executeQuery()) {
                while (questionsResultSet.next()) {
                    questions.add(createModelFromResultSet(questionsResultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    @Override
    public Question read(int questionId) {
        Question question = null;

        String selectStmt = "SELECT * FROM Questions WHERE QuestionID = ?;";

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectStmt)) {

            preparedStatement.setInt(1, questionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    question = createModelFromResultSet(resultSet);
                } else {
                    System.out.println("Question not found with ID: " + questionId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return question;
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
            preparedStatement.setInt(5, question.getMultipleChoice());
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

        CategoryDAO categoryDAO = new CategoryDAO();
        Category questionCategory = categoryDAO.read(resultSet.getInt("FK_Category_ID"));

        KeywordDAO keywordDAO = new KeywordDAO();
        ArrayList<Keyword> keywords = keywordDAO.readAllForOneQuestion(question_id);

        ImageDAO imageDAO = new ImageDAO();
        ArrayList<Image> images = imageDAO.readAllForOneQuestion(question_id);

        return new Question(
                question_id,
                questionCategory,
                resultSet.getInt("Difficulty"),
                resultSet.getFloat("Points"),
                resultSet.getString("Question"),
                resultSet.getInt("MultipleChoice"),
                resultSet.getString("Language"),
                resultSet.getString("Remarks"),
                resultSet.getString("Answers"),
                keywords,
                images
        );
    }
}
