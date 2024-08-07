package com.example.backend.db.repositories;

import com.example.backend.db.daos.CourseDAO;
import com.example.backend.db.daos.QuestionDAO;
import com.example.backend.db.models.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository implements Repository<Question> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    QuestionDAO questionDAO;
    final ArrayList<String> columnNames = new ArrayList<>(List.of(
            "q.id", "q.fk_category_id", "difficulty", "points", "question", "question_type",
            "remark", "created_at", "updated_at", "fk_answer_id", "fk_keyword_id", "fk_image_id"));

    public QuestionRepository(QuestionDAO questionDAO) {
        setQuestionDAO(questionDAO);
    }

    public int getCountOfAllQuestions() {
        return getQuestionDAO().readQuestionCount();
    }

    public int getCountOfAllQuestions(StudyProgram studyProgram) {
        return getQuestionDAO().readQuestionCount(studyProgram.getId());
    }
    public int getCountOfAllQuestions(Course course) {
        return getQuestionDAO().readQuestionCount(course);
    }
    @Override
    public ArrayList<Question> getAll() {
        return getQuestionDAO().readAll();
    }

    // getting all questions for one category
    public ArrayList<Question> getAll(Category category) {
        return getQuestionDAO().readAll(category);
    }

    public ArrayList<Question> getAll(Course course, int minQuestionId) {
        return getQuestionDAO().readAll(course, minQuestionId);
    }

    /**
     * getting all questions for a dynamic search
     * @param question_searchOptions A question, that has all search-values.
     * @param courseName The course-name we are searching the questions for.
     * @return Return an ArrayList of questions, that match the given conditions.
     */
    public ArrayList<Question> getAll(Question question_searchOptions, String courseName) {
        // adding default values
        // status = 1 means: slider/spinner is activated and not on min or max
        return getAll(question_searchOptions, courseName, 1, 1);
    }
    public ArrayList<Question> getAll(Question question_searchOptions, String courseName, int pointsStatus, int difficultyStatus) {
        Field[] searchFields = Question.class.getDeclaredFields();
        ArrayList<SearchObject<?>> searchOptions = new ArrayList<>();
        Course course = new CourseDAO().read(courseName);
        int i = 0;

        // check every field of question_searchOptions for values
        for(Field field : searchFields) {
            String columnName = "";

            // setting columnName
            if(i < this.columnNames.size()) {
                columnName = this.columnNames.get(i);
            }

            // Make private fields accessible for reading
            field.setAccessible(true);

            try {
                // value of class-variable
                Object field_value = field.get(question_searchOptions);
                String field_name = field.getName();

                if (field_value == null) {
                    // ArrayLists & Date are null --> set them false
                    searchOptions.add(new SearchObject<>(columnName, field_name, null, false));
                }
                else if (field_value instanceof QuestionType type) {
                    if (type.getName() == null) {
                        searchOptions.add(new SearchObject<>(columnName, field_name, 0, false));
                    } else {
                        // pass the name and not the object "QuestionType"
                        searchOptions.add(new SearchObject<>(columnName, field_name, type.getName(), true));
                    }
                }
                else if(field_name.equals("points") && (float) field_value == 0) {
                    // field_name == points and points = 0 --> set false
                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, false));
                }
                else if((field_name.equals("id") || field_name.equals("difficulty"))
                        && (int) field_value == 0) {
                    // difficulty and question_id not set --> set false
                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, false));
                }
                else {
                    // field has a value --> set true
                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, true));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            i++;
        }

        return getQuestionDAO().readAll(searchOptions, course, pointsStatus, difficultyStatus);
    }

    @Override
    public Question get(int id) {
        return getQuestionDAO().read(id);
    }

    public int getMaxQuestionId() {
        return getQuestionDAO().getMaxQuestionId();
    }

    @Override
    public void add(Question question) throws IOException {
        getQuestionDAO().create(question);
    }

    @Override
    public void update(Question question) {
        getQuestionDAO().update(question);
    }

    @Override
    public void remove(Question question) {
        getQuestionDAO().delete(question.getId());
    }
}
