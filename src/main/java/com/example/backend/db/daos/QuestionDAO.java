package com.example.backend.db.daos;

import com.example.backend.app.LogLevel;
import com.example.backend.app.Logger;
import com.example.backend.db.SQLiteDatabaseConnection;
import com.example.backend.db.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class QuestionDAO implements DAO<Question> {
    private final String selectColumns =
            "SELECT q.id AS question_id, q.fk_category_id, q.difficulty, q.points, q.question, q.fk_question_type_id, q.remark, q.created_at, q.updated_at, " +
            "a.id AS answer_id, a.answer, c.name AS category_name, " +
            "k.id AS keyword_id, k.keyword, qt.name AS question_type, " +
            "i.id AS image_id, i.image, i.name AS image_name, i.position, i.comment ";

    // questionCache needed for staging questions before sending them to the user
    ArrayList<Question> questionCache;
    public QuestionDAO() {
        this.questionCache = new ArrayList<>();
    }

    /**
     * Creates a new question in the database.
     *
     * @param question The question to be created.
     */
    @Override
    public void create(Question question) {
        String insertStmt =
                "INSERT INTO questions " +
                "(fk_category_id, difficulty, points, question, fk_question_type_id, remark, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";
        Logger.log(getClass().getName(), insertStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStmt)) {

            preparedStatement.setInt(1, question.getCategory().getId());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setFloat(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestion());
            preparedStatement.setInt(5, question.getType().getId());
            preparedStatement.setString(6, question.getRemark());
            preparedStatement.setString(7, question.getCreated_at().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all questions from the database.
     *
     * @return ArrayList of all questions.
     */
    @Override
    public ArrayList<Question> readAll() {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectQuestionsStmt =
            this.selectColumns +
            "FROM Questions q " +
            "JOIN categories c ON q.fk_category_id = c.id " +
            "LEFT JOIN has_aq ha ON q.id = ha.fk_question_id " +
            "LEFT JOIN answers a ON ha.fk_answer_id = a.id " +
            "LEFT JOIN has_kq hkq ON q.id = hkq.fk_question_id " +
            "LEFT JOIN keywords k ON hkq.fk_keyword_id = k.id " +
            "LEFT JOIN has_iq hiq ON q.id = hiq.fk_question_id " +
            "LEFT JOIN images i ON hiq.fk_image_id = i.id " +
            "LEFT JOIN question_types qt ON q.fk_question_type_id = qt.id;";
        Logger.log(getClass().getName(), selectQuestionsStmt, LogLevel.DEBUG);

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

    /**
     * Retrieves all questions belonging to a specific category from the database.
     *
     * @param category The category for which questions are to be retrieved.
     * @return ArrayList of questions belonging to the specified category.
     */
    public ArrayList<Question> readAll(Category category) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectQuestionsStmt =
                this.selectColumns +
                "FROM Questions q " +
                "JOIN categories c ON q.fk_category_id = c.id " +
                "LEFT JOIN has_aq ha ON q.id = ha.fk_question_id " +
                "LEFT JOIN answers a ON ha.fk_answer_id = a.id " +
                "LEFT JOIN has_kq hkq ON q.id = hkq.fk_question_id " +
                "LEFT JOIN keywords k ON hkq.fk_keyword_id = k.id " +
                "LEFT JOIN has_iq hiq ON q.id = hiq.fk_question_id " +
                "LEFT JOIN images i ON hiq.fk_image_id = i.id " +
                "LEFT JOIN question_types qt ON q.fk_question_type_id = qt.id " +
                "WHERE q.fk_category_id = ?;";
        Logger.log(getClass().getName(), selectQuestionsStmt, LogLevel.DEBUG);

        if (category != null) {
            try (Connection connection = SQLiteDatabaseConnection.connect();
                 PreparedStatement questionsStatement = connection.prepareStatement(selectQuestionsStmt)) {

                questionsStatement.setInt(1, category.getId());
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

    /**
     * Retrieves questions based on search options and a course from the database.
     *
     * @param searchOptions List of search options.
     * @param course        The course for which questions are to be retrieved.
     * @return ArrayList of questions based on the search options and course.
     */
    public ArrayList<Question> readAll(ArrayList<SearchObject<?>> searchOptions, Course course) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        // making a list of values for the preparedStmt
        ArrayList<Object> listForPreparedStmt = new ArrayList<>();

        // doing this for performance --> avoiding "n+1-select"
        StringBuilder selectQuestionsStmt = new StringBuilder(
                this.selectColumns +
                "FROM Questions q " +
                "JOIN categories c ON q.fk_category_id = c.id " +
                "LEFT JOIN has_aq ha ON q.id = ha.fk_question_id " +
                "LEFT JOIN answers a ON ha.fk_answer_id = a.id " +
                "LEFT JOIN has_kq hkq ON q.id = hkq.fk_question_id " +
                "LEFT JOIN keywords k ON hkq.fk_keyword_id = k.id " +
                "LEFT JOIN has_iq hiq ON q.id = hiq.fk_question_id " +
                "LEFT JOIN images i ON hiq.fk_image_id = i.id " +
                "LEFT JOIN question_types qt ON q.fk_question_type_id = qt.id " +
                "LEFT JOIN has_cc hcc ON q.fk_category_id = hcc.fk_category_id " +
                "LEFT JOIN courses co ON hcc.fk_course_id = co.id " +
                "WHERE co.id = ?");

        // init selectSTMT and listForPreparedStmt
        prepareQuery(searchOptions, selectQuestionsStmt, listForPreparedStmt, course.getId());

        Logger.log(getClass().getName(), String.valueOf(selectQuestionsStmt), LogLevel.DEBUG);

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
                    questionsStatement.setInt(count, (int) ((Category) prepObjects).getId());
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

    /**
     * Prepares a SQL query based on search options.
     *
     * @param searchOptions       List of search options.
     * @param stmt                StringBuilder to construct the SQL query.
     * @param listForPreparedStmt List to hold values for the prepared statement.
     * @param course_id           The ID of the course.
     */
    public void prepareQuery(ArrayList<SearchObject<?>> searchOptions, StringBuilder stmt, ArrayList<Object> listForPreparedStmt, int course_id) {
        int countKeywords = 0;
        int countImages = 0;

        // append course_id to the listForPreparedStmt
        listForPreparedStmt.add(course_id);
        stmt.append(" AND");

        for(SearchObject<?> searchObject : searchOptions) {
            if(searchObject.isSet() && Objects.equals(searchObject.getObjectName(), "keywords")) {
                // keywords passed
                ArrayList<Keyword> keywords = (ArrayList<Keyword>) searchObject.getValueOfObject();
                stmt.append("(");
                while(keywords.size() > countKeywords) {
                    stmt.append(" ").append("k.keyword").append(" = ? OR");
                    listForPreparedStmt.add(keywords.get(countKeywords).getKeyword());
                    countKeywords++;
                }
                stmt.delete(stmt.length() - 3, stmt.length());
                stmt.append(")").append(" AND");
            }
            else if(searchObject.isSet() && Objects.equals(searchObject.getObjectName(), "images")) {
                // images go like keywords
                ArrayList<Image> images = (ArrayList<Image>) searchObject.getValueOfObject();
                stmt.append("(");
                while(images.size() > countImages) {
                    stmt.append(" ").append("i.name").append(" = ? OR");
                    listForPreparedStmt.add(images.get(countImages).getName());
                    countImages++;
                }
                stmt.delete(stmt.length() - 3, stmt.length());
                stmt.append(")").append(" AND");
            }
            else if(searchObject.isSet() && !Objects.equals(searchObject.getColumn_name(), "")) {
                // append only objects with set flag and a columnName (otherwise we would insert into non-existing columns)
                stmt.append(" ").append(searchObject.getColumn_name()).append(" = ? AND");
                listForPreparedStmt.add(searchObject.getValueOfObject());
            }
        }

        // replace last ' AND' to ';'
        stmt.delete(stmt.length() - 4, stmt.length());
        stmt.append(';');
    }

    /**
     * Retrieves a question by its ID from the database.
     *
     * @param questionId The ID of the question to retrieve.
     * @return The Question object corresponding to the given ID.
     */
    @Override
    public Question read(int questionId) {
        // deleting old questions, if they are existing in this cache
        this.questionCache.clear();

        String selectStmt =
            this.selectColumns +
            "FROM Questions q " +
            "JOIN categories c ON q.fk_category_id = c.id " +
            "LEFT JOIN has_aq ha ON q.id = ha.fk_question_id " +
            "LEFT JOIN answers a ON ha.fk_answer_id = a.id " +
            "LEFT JOIN has_kq hkq ON q.id = hkq.fk_question_id " +
            "LEFT JOIN keywords k ON hkq.fk_keyword_id = k.id " +
            "LEFT JOIN has_iq hiq ON q.id = hiq.fk_question_id " +
            "LEFT JOIN images i ON hiq.fk_image_id = i.id " +
            "LEFT JOIN question_types qt ON q.fk_question_type_id = qt.id " +
            "WHERE q.id = ?;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

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

    /**
     * This function gets the id from the latest created question.
     * @return the latest id, which was created.
     */
    public int getMaxQuestionId() {
        this.questionCache.clear();

        String selectStmt = "SELECT id FROM questions ORDER BY created_at DESC LIMIT 1;";
        Logger.log(getClass().getName(), selectStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectStmt)) {

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Updates an existing question in the database.
     *
     * @param question The question to be updated.
     */
    @Override
    public void update(Question question) {

        // TODO: update answers
        String updateStmt =
                "UPDATE questions " +
                "SET fk_category_id = ?, difficulty = ?, points = ?, question = ?, " +
                "remark = ?, updated_at = ? " +
                "WHERE id = ?;";

        Logger.log(getClass().getName(), updateStmt, LogLevel.DEBUG);

        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStmt)) {

            preparedStatement.setInt(1, question.getCategory().getId());
            preparedStatement.setInt(2, question.getDifficulty());
            preparedStatement.setFloat(3, question.getPoints());
            preparedStatement.setString(4, question.getQuestion());
            preparedStatement.setString(5, question.getRemark());
            preparedStatement.setString(6, String.valueOf(question.getUpdated_at()));
            preparedStatement.setInt(7, question.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a question from the database.
     *
     * @param id The ID of the question to delete.
     */
    @Override
    public void delete(int id) {
        String deleteStmt = "DELETE FROM questions WHERE id = ?;";
        String deleteHasIQStmt = "DELETE FROM has_iq WHERE fk_question_id = ?;";
        String deleteHasKQStmt = "DELETE FROM has_kq WHERE fk_question_id = ?;";
        String deleteHasAQStmt = "DELETE FROM has_aq WHERE fk_question_id = ?;";
        try (Connection connection = SQLiteDatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteStmt);
             PreparedStatement secondPreparedStatement = connection.prepareStatement(deleteHasIQStmt);
             PreparedStatement thirdPreparedStatement = connection.prepareStatement(deleteHasKQStmt);
             PreparedStatement fourthPreparedStatement = connection.prepareStatement(deleteHasAQStmt)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            secondPreparedStatement.setInt(1, id);
            secondPreparedStatement.executeUpdate();

            thirdPreparedStatement.setInt(1, id);
            thirdPreparedStatement.executeUpdate();

            fourthPreparedStatement.setInt(1, id);
            fourthPreparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Question object from a ResultSet.
     *
     * @param resultSet The ResultSet containing question data.
     * @return The Question object created from the ResultSet.
     */
    public Question createModelFromResultSet(ResultSet resultSet) throws SQLException {
        int question_id = resultSet.getInt("question_id");

        // checking, if question exists in our questionCache
        for(Question question : this.questionCache) {
            // adding keyword & image and answers if one question has multiple keywords and images
            if (question.getId() == question_id) {
                // keyword not null
                if (resultSet.getInt("keyword_id") != 0) {
                    Keyword newKeyword = new Keyword(
                            resultSet.getInt("keyword_id"),
                            resultSet.getString("keyword"));
                    question.getKeywords().add(newKeyword);
                }
                // image not null
                if (resultSet.getInt("image_id") != 0) {
                    Image newImage = new Image(
                            resultSet.getInt("image_id"),
                            resultSet.getBytes("image"),
                            resultSet.getString("image_name"),
                            resultSet.getInt("position"),
                            resultSet.getString("comment"));
                    question.getImages().add(newImage);
                }
                // answer not null
                if (resultSet.getInt("answer_id") != 0) {
                    Answer newAnswer = new Answer(
                            resultSet.getInt("answer_id"),
                            resultSet.getString("answer"));
                    question.getAnswers().add(newAnswer);
                }
                // we do not want to create a duplicate question
                // so, we return here
                return null;
            }
        }

        Category questionCategory = new Category(
                resultSet.getInt("fk_category_id"),
                resultSet.getString("category_name"));

        QuestionType questionType = new QuestionType(
                resultSet.getInt("fk_question_type_id"),
                resultSet.getString("question_type"));

        ArrayList<Keyword> keywords = new ArrayList<>();
        Keyword newKeyword = new Keyword(
                resultSet.getInt("keyword_id"),
                resultSet.getString("keyword"));
        keywords.add(newKeyword);

        ArrayList<Image> images = new ArrayList<>();
        Image newImage = new Image(
                resultSet.getInt("image_id"),
                resultSet.getBytes("image"),
                resultSet.getString("image_name"),
                resultSet.getInt("position"),
                resultSet.getString("comment"));
        images.add(newImage);

        ArrayList<Answer> answers = new ArrayList<>();
        Answer newAnswer = new Answer(
                resultSet.getInt("answer_id"),
                resultSet.getString("answer"));
        answers.add(newAnswer);

        return new Question(
                question_id,
                questionCategory,
                resultSet.getInt("difficulty"),
                resultSet.getFloat("points"),
                resultSet.getString("question"),
                questionType,
                resultSet.getString("remark"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("updated_at"),
                answers,
                keywords,
                images
        );
    }
}
