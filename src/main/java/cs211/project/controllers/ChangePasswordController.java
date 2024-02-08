package cs211.project.controllers;

import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.regex.Pattern;

public class ChangePasswordController {
    @FXML
    private TextField confirmPasswordPasswordField;
    @FXML
    private TextField currentPasswordPasswordField;
    @FXML
    private Label errorConfirmPasswordLabel;
    @FXML
    private Label errorCurrentPasswordLabel;
    @FXML
    private Label errorNewPasswordLabel;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TextField newPasswordPasswordField;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Label usernameLabel;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        clearErrorLabels();
        Loader.loadNavBar(navBarVBox);
        profilePicture.setImage(AllCollection.getInstance().getCurrentUser().getProfileImage());
        AdjustImageView.radiusImageView(profilePicture, profilePicture.getFitHeight());
        AdjustImageView.setViewPortImageView(profilePicture, AllCollection.getInstance().getCurrentUser().getProfileImage());
        usernameLabel.setText(AllCollection.getInstance().getCurrentUser().getUsername());
    }


    @FXML
    private void onBackButtonClick() {
        try {
            String previousPage = PageHistory.getInstance().popStack();
            FXRouter.goTo(previousPage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onConfirmButtonClick() {
        clearErrorLabels();
        Boolean statusCurrentPassword = isCurrentPassword();
        Boolean statusNewPassword = checkNewPassword();
        Boolean statusConfirmPassword = checkConfirmPassword();
        Boolean isNewPasswordSameAsCurrentPassword = AllCollection.getInstance().getCurrentUser().checkPassword(newPasswordPasswordField.getText());
        if (statusCurrentPassword && statusNewPassword && statusConfirmPassword && isNewPasswordSameAsCurrentPassword) {
            errorNewPasswordLabel.setText("New password must be not same as current password.");

        } else if (statusCurrentPassword && statusNewPassword && statusConfirmPassword) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Changing Password",
                                        "Are you sure you want to change password?",
                                       "");
            if (confirmDialogController.getIsConfirm()) {
                AllCollection.getInstance().getCurrentUser().setPassword(newPasswordPasswordField.getText());
                AllCollection.getInstance().writeUser();
                try {
                    FXRouter.goTo(PageHistory.getInstance().popStack());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private boolean checkConfirmPassword(){
        if (confirmPasswordPasswordField.getText().equals("")) {
            errorConfirmPasswordLabel.setText("Cannot be empty.");
        } else if (!confirmPasswordPasswordField.getText().equals(newPasswordPasswordField.getText())) {
            errorConfirmPasswordLabel.setText("The password does not match.");
        } else {
            return true;
        }
        return false;
    }

    private boolean checkNewPassword() {
        String newPasswordInput = newPasswordPasswordField.getText();
        boolean hasUppercase = Pattern.compile("[A-Z]").matcher(newPasswordInput).find();
        boolean hasLowercase = Pattern.compile("[a-z]").matcher(newPasswordInput).find();
        boolean hasNumber = Pattern.compile("[a-z]").matcher(newPasswordInput).find();

        if (newPasswordInput.equals("")) {
            errorNewPasswordLabel.setText("Cannot be empty.");
        } else if (newPasswordInput.length() < 8) {
            errorNewPasswordLabel.setText("The password must be at least 8 characters.");
        } else if (!hasUppercase || !hasLowercase || !hasNumber) {
            errorNewPasswordLabel.setText("Password must contain uppercase letters, lowercase letters, and numbers.");
        } else {
            return true;
        }
        return false;
    }

    private boolean isCurrentPassword() {
        String currentPassword = currentPasswordPasswordField.getText();
        boolean isCurrentPassword = AllCollection.getInstance().getCurrentUser().checkPassword(currentPassword);

        if (currentPassword.equals("")) {
            errorCurrentPasswordLabel.setText("Cannot be empty.");
        } else if (!isCurrentPassword) {
            errorCurrentPasswordLabel.setText("Incorrect Current Password");
        }

        return isCurrentPassword;
    }

    private void clearErrorLabels() {
        errorConfirmPasswordLabel.setText("");
        errorCurrentPasswordLabel.setText("");
        errorNewPasswordLabel.setText("");
    }
}
