package com.example.backend.app;

import java.util.ArrayList;
import com.example.backend.db.models.Question;

public class ExportCSV {
    private String fileName;
    private String directoryName;

    public ExportCSV(String fileName, String directoryName) {
        this.fileName = fileName;
        this.directoryName = directoryName;
    }


    public boolean export() {
        ArrayList<Question> questions = collectData();
        // TODO implement logic

        return true;
    }

    // add to parameters some options
    // TODO: think of large amount of data
    public ArrayList<Question> collectData() {
        return null;
    }
}
