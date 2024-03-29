package com.example.backend.db.repositories;

import com.example.backend.db.daos.CourseDAO;
import com.example.backend.db.daos.QuestionDAO;
import com.example.backend.db.models.Course;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.SearchObject;
import com.example.backend.db.models.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository implements Repository<Question> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    QuestionDAO questionDAO;
    final ArrayList<String> columnNames = new ArrayList<>(List.of(
            "QuestionID", "FK_Category_ID", "Difficulty", "Points",
            "Question", "MultipleChoice", "Language", "Remarks", "Answers"));

    public QuestionRepository(QuestionDAO questionDAO) {
        setQuestionDAO(questionDAO);
    }

    @Override
    public ArrayList<Question> getAll() {
        return getQuestionDAO().readAll();
    }

    // getting all questions for one category
    public ArrayList<Question> getAll(Category category) {
        return getQuestionDAO().readAll(category);
    }

    // getting all questions for a dynamic search
    public ArrayList<Question> getAll(Question question_searchOptions, String courseName, boolean considerMC) {
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

                if(field_value == null) {
                    // ArrayLists are null --> set them false
                    searchOptions.add(new SearchObject<>(columnName, field_name, null, false));
                }
                else if(field_name.equals("multipleChoice") && !considerMC) {
                    // field_name == MC and considerMC is false --> set false
                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, false));
                }
                else if(field_name.equals("points") && (float) field_value == 0) {
                    // field_name == points and points = 0 --> set false
                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, false));
                }
                else if((field_name.equals("question_id") || field_name.equals("difficulty"))
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

        return getQuestionDAO().readAll(searchOptions, course);
    }

    @Override
    public Question get(int id) {
        return getQuestionDAO().read(id);
    }

    @Override
    public void add(Question question) {
        getQuestionDAO().create(question);
    }

    @Override
    public void update(Question question) {
        getQuestionDAO().update(question);
    }

    @Override
    public void remove(Question question) {
        getQuestionDAO().delete(question.getQuestion_id());
    }
}
