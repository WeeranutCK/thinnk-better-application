package cs211.project.controllers;

import cs211.project.cs211661project.ThinnkApplication;
import cs211.project.models.User;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.PageHistory;
import cs211.project.services.TimeConversion;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.io.IOException;

public class LogInController {
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Hyperlink hyperlinkText;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label forgotPasswordLabel;
    @FXML
    private StackPane stackPane;
    @FXML
    private TextField usernameTextField;


    @FXML
    public void initialize() {
        // There's still room to improve/add this feature in the future if so
        forgotPasswordLabel.setDisable(true);

        String filePath = "/cs211/project/images/background-image.png";
        Image backgroundImage = new Image(getClass().getResourceAsStream(filePath));

        BackgroundImage backgroundImg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        stackPane.setBackground(new Background(backgroundImg));

        hyperlinkText.setOnAction(e -> {
            ThinnkApplication.hostServices.showDocument("https://pixabay.com/photos/flower-macro-violet-purple-6328348/");
        });

        errorMessageLabel.setText("");
        usernameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                logInButtonClickMethod();
            }
        });
    }


    @FXML
    private void onLogInButtonClick() {
        logInButtonClickMethod();
    }

    @FXML
    private void onSignUpButtonClick() {
        try {
            FXRouter.goTo("sign-up");
            PageHistory.getInstance().pushStack("log-in");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void logInButtonClickMethod() {
        User usernameLogIn = AllCollection.getInstance().getUserList().findUserByUsername(usernameTextField.getText());
        if (usernameTextField.getText().trim().isEmpty()) {
            errorMessageLabel.setText("Username cannot be empty.");
        } else if (passwordField.getText().trim().isEmpty()) {
            errorMessageLabel.setText("Password cannot be empty.");
        } else if (usernameLogIn == null || !usernameLogIn.checkPassword(passwordField.getText())) {
            errorMessageLabel.setText("The username or password are incorrect.");
        } else if (usernameLogIn.checkPassword(passwordField.getText())) {
            AllCollection.getInstance().setCurrentUser(usernameLogIn);
            AllCollection.getInstance().getCurrentUser().getTimeStamps().add(TimeConversion.getNowDate());
            AllCollection.getInstance().writeUser();
            AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();
            try {
                FXRouter.goTo("main");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
