module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires java.sql;
    requires java.desktop;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml.schemas;
    requires layout;
    requires kernel;
    requires io;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.boot;
    requires spring.jdbc;
    requires spring.beans;
    requires spring.core;
    requires org.apache.logging.log4j;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.tx;
    requires jlatexmath;
    requires javafx.web;
    requires html2pdf;
    requires org.jsoup;

    opens com.example.application to javafx.fxml, spring.core;
    exports com.example.application;
    exports com.example.application.frontend.controller;
    opens com.example.application.frontend.controller to javafx.fxml;
    exports com.example.application.frontend.components;
    opens com.example.application.frontend.components to javafx.fxml;
    exports com.example.application.frontend.modals;
    opens com.example.application.frontend.modals to javafx.fxml;
    exports com.example.application.backend.db.services;
    opens com.example.application.backend.db.models;
}