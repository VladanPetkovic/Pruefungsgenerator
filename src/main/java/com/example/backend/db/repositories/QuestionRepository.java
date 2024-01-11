package com.example.backend.db.repositories;

import com.example.backend.db.daos.KeywordDAO;
import com.example.backend.db.daos.QuestionDAO;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.SearchObject;
import com.example.backend.db.models.Topic;
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
            "QuestionID", "FK_Topic_ID", "Difficulty", "Points",
            "Question", "MultipleChoice", "Language", "Remarks", "Answers"));

    public QuestionRepository(QuestionDAO questionDAO) {
        setQuestionDAO(questionDAO);
    }

    @Override
    public ArrayList<Question> getAll() {
        return getQuestionDAO().readAll();
    }

    public ArrayList<Question> getAll(Topic topic) {
        return getQuestionDAO().readAll(topic.getTopic());
    }

    public ArrayList<Question> getAll(Question question_searchOptions) {
        Field[] searchFields = Question.class.getDeclaredFields();
        ArrayList<SearchObject<?>> searchOptions = new ArrayList<>();
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

                if(field_value == null) {
                    searchOptions.add(new SearchObject<>(columnName, field_value, false));
                } else if(field_value instanceof Integer && (int) field_value == 0) {
                    searchOptions.add(new SearchObject<>(columnName, field_value, false));
                } else {
                    searchOptions.add(new SearchObject<>(columnName, field_value, true));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            i++;
        }

        return getQuestionDAO().readAll(searchOptions);
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
