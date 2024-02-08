package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.AdjustImageView;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.Loader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SignUpController {
    @FXML
    private CheckBox acceptCheckBox;
    @FXML
    private PasswordField confirmPasswordTextFile;
    @FXML
    private Label confirmPasswordErrorLabel;
    @FXML
    private Label dateErrorLabel;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField emailTextFile;
    @FXML
    private ImageView profileImageView;
    @FXML
    private VBox navBarVBox;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Label profileNameErrorLabel;
    @FXML
    private TextField profileNameTextFile;
    @FXML
    private PasswordField passwordTextFile;
    @FXML
    private TextField usernameTextFile;
    @FXML
    private Label usernameErrorLabel;
    @FXML
    private DatePicker dateOfBirthDatePicker;

    private Image defaultImage;
    private File selectedFile;
    private List<TextField> validateTextFields = new ArrayList<>();


    @FXML
    public void initialize() {
        defaultImage = new Image(getClass().getResourceAsStream("/cs211/project/images/user.png"));

        profileImageView.setImage(defaultImage);
        AdjustImageView.setViewPortImageView(profileImageView, defaultImage);
        AdjustImageView.radiusImageView(profileImageView, 150);

        validateTextFields.add(emailTextFile);
        validateTextFields.add(usernameTextFile);
        validateTextFields.add(passwordTextFile);
        validateTextFields.add(confirmPasswordTextFile);
        validateTextFields.add(profileNameTextFile);

        for (TextField textField : validateTextFields) {
            textField.setOnMouseClicked(event -> {
                clearErrorLabel(textField);
                clearConfirmPasswordErrorLabel();
            });
        }

        dateOfBirthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dateErrorLabel.setText("");
            }
        });

        pageSetUp();
    }

    @FXML
    public void onAcceptCheckBox(ActionEvent actionEvent) {
        if (acceptCheckBox.isSelected()) {
            errorLabel.setText("");
        }
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
        try {
            FXRouter.goTo("log-in");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onCreate(ActionEvent actionEvent) {
        validateTextFields.forEach(this::clearErrorLabel);
        boolean hasErrors = false;
        String password = passwordTextFile.getText();
        String confirmPassword = confirmPasswordTextFile.getText();
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        if (!acceptCheckBox.isSelected()) {
            errorLabel.setText("You must accept the terms to create an account.");
            hasErrors = true;
        }
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,4})$";
        if (emailTextFile.getText().isEmpty()) {
            emailErrorLabel.setText("Email cannot be empty.");
            hasErrors = true;
        } else if (!emailTextFile.getText().matches(EMAIL_REGEX)) {
            emailErrorLabel.setText("Invalid email address.");
            hasErrors = true;
        }
        if (profileNameTextFile.getText().isEmpty() || profileNameTextFile.getText().trim().isEmpty()) {
            profileNameErrorLabel.setText("Profile name cannot be empty.");
            hasErrors = true;
        } else if (containsSpecialCharacters(profileNameTextFile.getText())){
            profileNameErrorLabel.setText("Profile Name cannot contain special characters.");
            hasErrors = true;
        }
        if (usernameTextFile.getText().isEmpty()) {
            usernameErrorLabel.setText("Username cannot be empty.");
            hasErrors = true;
        } else if (usernameAlreadyExists(usernameTextFile.getText())) {
            usernameErrorLabel.setText("Username is already in use.");
            hasErrors = true;
        } else if (containsSpaces(usernameTextFile.getText())) {
            usernameErrorLabel.setText("Username cannot contain spaces.");
            hasErrors = true;
        } else if (containsSpecialCharacters(usernameTextFile.getText())) {
            usernameErrorLabel.setText("Username cannot contain special characters.");
            hasErrors = true;
        }
        if (passwordTextFile.getText().isEmpty() || passwordTextFile.getText().length() < 8) {
            passwordErrorLabel.setText("Password must be at least 8 characters long.");
            hasErrors = true;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match.");
            hasErrors = true;
        }
        if (password.length() < 8) {
            passwordErrorLabel.setText("Password must be at least 8 characters long.");
            hasErrors = true;
        } else if (!hasUppercase || !hasLowercase || !hasDigit) {
            passwordErrorLabel.setText("Password must contain uppercase letters, lowercase letters, and numbers.");
            hasErrors = true;
        }
        DatePicker datePicker = getDatePicker();
        if (datePicker.getValue() != null) {
            LocalDate dateOfBirth = datePicker.getValue();
            if (dateOfBirth.isAfter(LocalDate.now())) {
                dateErrorLabel.setText("Selected date must not be in the future.");
                hasErrors = true;
            }
        } else {
            dateErrorLabel.setText("Please select a date of birth.");
            hasErrors = true;
        }

        if (!hasErrors) {
            try {
                Date dateOfBirth = convertToDate(datePicker.getValue());
                String email = emailTextFile.getText();
                String profileName = profileNameTextFile.getText();
                String username = usernameTextFile.getText();
                String passwordHash = password;

                AllCollection.getInstance().getUserList().addNewUser(dateOfBirth, email, passwordHash, profileName, username, selectedFile);
                AllCollection.getInstance().writeUser();

                FXRouter.goTo("log-in");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onChooseProfile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                if (image != null) {
                    profileImageView.setImage(image);
                    AdjustImageView.setViewPortImageView(profileImageView, image);
                } else {
                    profileImageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearErrorLabel(TextField textField) {
        if (textField == emailTextFile) {
            emailErrorLabel.setText("");
        } else if (textField == usernameTextFile) {
            usernameErrorLabel.setText("");
        } else if (textField == passwordTextFile) {
            passwordErrorLabel.setText("");
        } else if (textField == confirmPasswordTextFile) {
            confirmPasswordErrorLabel.setText("");
        } else if (textField == profileNameTextFile) {
            profileNameErrorLabel.setText("");
        }
    }

    private void clearConfirmPasswordErrorLabel() {
        confirmPasswordErrorLabel.setText("");
    }

    public DatePicker getDatePicker() {
        return dateOfBirthDatePicker;
    }

    private Date convertToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    private void pageSetUp() {
        NavBarController navBarController = Loader.loadNavBar(navBarVBox);
        navBarController.setDisable(true);
        navBarController.setDefaultTheme();
        dateOfBirthDatePicker.setEditable(false);
    }

    private boolean usernameAlreadyExists(String username) {
        UserList userList = AllCollection.getInstance().getUserList();
        if (userList != null) {
            User existingUser = userList.findUserByUsername(username);
            return existingUser != null;
        }
        return false;
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

    private boolean containsSpaces(String input) {
        return input.contains(" ");
    }
}
