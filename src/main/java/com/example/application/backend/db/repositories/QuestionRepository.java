package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT COUNT(q) FROM Question q JOIN q.category cat JOIN cat.courses c JOIN c.studyPrograms sp WHERE sp.id = :studyProgramId")
    long getCountByStudyProgramId(@Param("studyProgramId") Long studyProgramId);

    @Query("SELECT COUNT(q) FROM Question q JOIN q.category cat JOIN cat.courses c WHERE c.id = :courseId")
    long getCountByCourseId(@Param("courseId") Long courseId);

    List<Question> findQuestionsByCategoryId(Long categoryId);

    @Query("SELECT q FROM Question q JOIN q.category cat JOIN cat.courses c WHERE c.id = :courseId AND q.id > :minQuestionId")
    List<Question> findByCourseIdAndIdGreaterThan(@Param("courseId") Long courseId, @Param("minQuestionId") Long minQuestionId, Pageable pageable);

    @Query("SELECT MAX(q.id) FROM Question q")
    Long getMaxQuestionId();

//
//    /**
//     * getting all questions for a dynamic search
//     * @param question_searchOptions A question, that has all search-values.
//     * @param courseName The course-name we are searching the questions for.
//     * @return Return an ArrayList of questions, that match the given conditions.
//     */
//    public ArrayList<Question> getAll(Question question_searchOptions, String courseName) {
//        // adding default values
//        // status = 1 means: slider/spinner is activated and not on min or max
//        return getAll(question_searchOptions, courseName, 1, 1);
//    }
//    public ArrayList<Question> getAll(Question question_searchOptions, String courseName, int pointsStatus, int difficultyStatus) {
//        Field[] searchFields = Question.class.getDeclaredFields();
//        ArrayList<SearchObject<?>> searchOptions = new ArrayList<>();
//        Course course = new CourseDAO().read(courseName);
//        int i = 0;
//
//        // check every field of question_searchOptions for values
//        for(Field field : searchFields) {
//            String columnName = "";
//
//            // setting columnName
//            if(i < this.columnNames.size()) {
//                columnName = this.columnNames.get(i);
//            }
//
//            // Make private fields accessible for reading
//            field.setAccessible(true);
//
//            try {
//                // value of class-variable
//                Object field_value = field.get(question_searchOptions);
//                String field_name = field.getName();
//
//                if (field_value == null) {
//                    // ArrayLists & Date are null --> set them false
//                    searchOptions.add(new SearchObject<>(columnName, field_name, null, false));
//                }
//                else if (field_value instanceof QuestionType type) {
//                    if (type.getName() == null) {
//                        searchOptions.add(new SearchObject<>(columnName, field_name, 0, false));
//                    } else {
//                        // pass the name and not the object "QuestionType"
//                        searchOptions.add(new SearchObject<>(columnName, field_name, type.getName(), true));
//                    }
//                }
//                else if(field_name.equals("points") && (float) field_value == 0) {
//                    // field_name == points and points = 0 --> set false
//                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, false));
//                }
//                else if((field_name.equals("id") || field_name.equals("difficulty"))
//                        && (int) field_value == 0) {
//                    // difficulty and question_id not set --> set false
//                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, false));
//                }
//                else {
//                    // field has a value --> set true
//                    searchOptions.add(new SearchObject<>(columnName, field_name, field_value, true));
//                }
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//
//            i++;
//        }
//
//        return getQuestionDAO().readAll(searchOptions, course, pointsStatus, difficultyStatus);
//    }
}
