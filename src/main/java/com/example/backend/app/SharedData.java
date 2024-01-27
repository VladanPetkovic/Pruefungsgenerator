package com.example.backend.app;

import com.example.backend.db.models.Course;
import com.example.backend.db.models.Question;
import com.example.backend.db.models.StudyProgram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SharedData {
    @Getter
    @Setter
    //stores the users course selection from the Home Screen
    private static Course selectedCourse;

    @Getter
    @Setter
    //stores the course the user wants to add
    private static Course newCourse = new Course();

    @Getter
    @Setter
    //stores the users study program selection from the Home Screen
    private static StudyProgram selectedStudyProgram;

    @Getter
    @Setter
    //stores the study program the user wants to add
    private static StudyProgram newStudyProgram = new StudyProgram();

    @Getter
    @Setter
    private static com.example.backend.db.models.Question filterQuestion = new Question();


    public static void reset() {
        selectedCourse = null;
        newCourse = new Course();
        selectedStudyProgram = null;
        newStudyProgram = new StudyProgram();
        filterQuestion = new Question();
    }

}
