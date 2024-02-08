package cs211.project.controllers;

import cs211.project.services.DialogWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.Arrays;
import java.util.List;

public class TextInputDialogController extends DialogWindow {
    @FXML
    protected TextField inputTextField;
    @FXML
    protected Label errorLabel;

    protected String inputText;
    protected String regExCriteria;


    @Override
    public void onCancelButtonClicked() {
        isConfirm = false;
        Platform.runLater(() -> {
            dialogStage.close();
        });
    }

    @Override
    public void onConfirmButtonClicked() {
        inputText = inputTextField.getText().trim();

        if (checkInput()) {
            isConfirm = true;
            Platform.runLater(() -> {
                dialogStage.close();
            });
        } else {
            isConfirm = false;
        }
    }

    public boolean checkInput() {
        if (inputText.isEmpty()) {
            setErrorLabel("Input text cannot be empty");
            return false;
        } else if (containsSpecialCharacters(inputText)) {
            setErrorLabel("Input text cannot contain special characters.");
            return false;
        } else {
            if (regExCriteria == null) {
                return true;
            } else {
                if (inputText.matches(regExCriteria)) {
                    return true;
                } else {
                    setErrorLabel("Input text format is wrong");
                    return false;
                }
            }
        }
    }

    public String getInputText() {
        return inputText;
    }

    public void setInitialValue(String initialValue) {
        inputTextField.setText(initialValue);
    }

    public void setErrorLabel(String errorText) {
        errorLabel.setText(errorText);
    }

    public void setRegExCriteria(String regExCriteria) {
        this.regExCriteria = regExCriteria;
    }

    public void setTextFieldEnter() {
        inputTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onConfirmButtonClicked();
            }
        });
    }

    private boolean containsSpecialCharacters(String input) {
        List<Character> disallowedCharacters = Arrays.asList('\\', '[', ']', ',');

        for (char character : input.toCharArray()) {
            if (disallowedCharacters.contains(character)) {
                return true;
            }
        }
        return false;
    }

}
