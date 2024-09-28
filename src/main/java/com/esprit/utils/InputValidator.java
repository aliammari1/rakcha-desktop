package com.esprit.utils;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum InputValidator {
    ;

    /**
     * @param textField
     * @param regexes
     * @param errorMessages
     * @param successMessages
     */
    public static void inputValidator(final TextField textField, final String[] regexes, final String[] errorMessages, final String[] successMessages) {
        final Tooltip tooltip = new Tooltip();
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                final Object obj = textField.getProperties().get("listener");
                if (obj instanceof ChangeListener) {
                    final ChangeListener<String> oldListener = (ChangeListener<String>) obj;
                    textField.textProperty().removeListener(oldListener);
                }
                final ChangeListener<String> newListener = (textObservable, oldText, newText) -> {
                    boolean isValid = true;
                    final StringBuilder combinedErrorMessage = new StringBuilder();
                    if (newText.isEmpty()) {
                        isValid = false;
                        combinedErrorMessage.append("The TextField is empty.\n");
                    } else {
                        for (int i = 0; i < regexes.length; i++) {
                            final Pattern pattern = Pattern.compile(regexes[i]);
                            final Matcher matcher = pattern.matcher(newText);
                            if (!matcher.matches()) {
                                isValid = false;
                                combinedErrorMessage.append(errorMessages[i]).append("\n");
                            }
                        }
                    }
                    if (!isValid) {
                        tooltip.setText(combinedErrorMessage.toString());
                        tooltip.setStyle("""
                                -fx-background-color: \
                                linear-gradient(from 0% 0% to 0% 100%, #ff0000 0%, #ff0000 100%);\
                                -fx-background-radius: 20.0px;\
                                """);
                        textField.setStyle("-fx-border-color: #ff0000;");
                        final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                        tooltip.show(textField.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
                    } else {
                        tooltip.setText(String.join("\n", successMessages));
                        tooltip.setStyle("""
                                -fx-background-color: \
                                linear-gradient(from 0% 0% to 0% 100%, #00ff00 0%, #00ff00 100%);\
                                -fx-background-radius: 20.0px;\
                                """);
                        textField.setStyle("-fx-border-color: #00ff00;");
                        tooltip.hide();
                    }
                };
                textField.textProperty().addListener(newListener);
                textField.getProperties().put("listener", newListener);
                textField.setTooltip(tooltip);
            } else {
                textField.setTooltip(null);
                tooltip.hide();
            }
        });
    }

}
