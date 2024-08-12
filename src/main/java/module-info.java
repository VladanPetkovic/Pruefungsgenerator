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
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.boot;
    requires spring.jdbc;
    requires spring.beans;
    requires spring.core;
    requires jakarta.persistence;
    requires org.apache.logging.log4j;
    requires spring.data.jpa;

    opens com.example.frontend to javafx.fxml, spring.core;
    exports com.example.frontend;
    exports com.example.frontend.controller;
    opens com.example.frontend.controller to javafx.fxml;
    exports com.example.frontend.components;
    opens com.example.frontend.components to javafx.fxml;
    exports com.example.frontend.modals;
    opens com.example.frontend.modals to javafx.fxml;
}