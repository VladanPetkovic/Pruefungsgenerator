package com.example.application.frontend.components;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class CustomDoubleSpinner extends Spinner<Double> {

    public CustomDoubleSpinner(){
        this(1,10,1,0.5);
    }

    public CustomDoubleSpinner(double min, double max, double initialValue, double amountToStepBy) {

        setEditable(true);
        setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy));

        // Get the TextField portion of the Spinner
        TextField textField = getEditor();

        // Create a TextFormatter to allow only double input
        TextFormatter<Double> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("-?\\d*(\\,\\d*)?")) {
                return change;
            } else {
                return null;
            }
        });

        // Set the TextFormatter to the TextField
        textField.setTextFormatter(formatter);

        setOnMouseClicked(event -> {
            if (getEditor().getText().isEmpty()) {
                // If the text field is empty and the user clicks on the spinner,
                // set the value to the initial value
                getValueFactory().setValue(initialValue);
            }
        });

        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // If the new value is null, set it to the initial value
                getValueFactory().setValue(initialValue);
            } else {
                // Round the new value to the nearest multiple of amountToStepBy
                double roundedValue = Math.round(newValue / amountToStepBy) * amountToStepBy;
                // Ensure the rounded value is within the specified range
                roundedValue = Math.max(min, Math.min(max, roundedValue));
                // Set the spinner's value to the rounded value
                getValueFactory().setValue(roundedValue);
            }
        });
    }
}