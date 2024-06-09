module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires poi.ooxml;
    requires poi;
    requires poi.ooxml.schemas;
    requires layout;
    requires kernel;
    requires io;
    requires itextpdf;

    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
    exports com.example.frontend.controller;
    opens com.example.frontend.controller to javafx.fxml;
    exports com.example.frontend.components;
    opens com.example.frontend.components to javafx.fxml;
    exports com.example.frontend.modals;
    opens com.example.frontend.modals to javafx.fxml;
}