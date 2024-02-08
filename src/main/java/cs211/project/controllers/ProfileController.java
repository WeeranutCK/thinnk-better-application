package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.collections.WalletList;
import cs211.project.services.*;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {
    @FXML
    private Button EventParticipated;
    @FXML
    private Button HostedEvent;
    @FXML
    private Button StaffEventParticipated;
    @FXML
    private TextArea biosTextArea;
    @FXML
    private Label dateOfBirthLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label idLabel;
    @FXML
    private VBox navBarVBox;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileNameLabel2;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label walletLabel;

    private final Image DEFAULT_IMAGE = new Image(getClass().getResourceAsStream("/cs211/project/images/user.png"));

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        User loggedInUser = AllCollection.getInstance().getCurrentUser();
        WalletList walletList = AllCollection.getInstance().getWalletList();

        setImage(loggedInUser);

        profileImageView.setImage(DEFAULT_IMAGE);
        AdjustImageView.setViewPortImageView(profileImageView, DEFAULT_IMAGE);
        AdjustImageView.radiusImageView(profileImageView, 150);

        dateOfBirthLabel.setText("");
        emailLabel.setText("");
        idLabel.setText("");
        profileNameLabel2.setText("");
        profileNameLabel.setText("");
        userNameLabel.setText("");
        walletLabel.setText("");

        if (loggedInUser != null) {

            profileNameLabel.setText(loggedInUser.getProfileName());
            emailLabel.setText(loggedInUser.getEmail());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateOfBirthLabel.setText(dateFormat.format(loggedInUser.getDateOfBirth()));
            idLabel.setText(loggedInUser.getUserId());
            emailLabel.setText(loggedInUser.getEmail());
            userNameLabel.setText(loggedInUser.getUsername());
            profileNameLabel2.setText(loggedInUser.getProfileName());
            biosTextArea.setText(loggedInUser.getBio());
            walletLabel.setText(String.format("%.2f", loggedInUser.getWallet().getAmount()));

            biosTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                if (loggedInUser != null) {
                    loggedInUser.setBio(newValue);
                    AllCollection.getInstance().writeUser();
                }
            });

            Loader.loadNavBar(navBarVBox);
        }

        setImage(loggedInUser);

        EventParticipated.setOnMouseEntered(event -> {
            zoomButton(EventParticipated, true);
        });

        EventParticipated.setOnMouseExited(event -> {
            zoomButton(EventParticipated, false);
        });

        HostedEvent.setOnMouseExited(event -> {
            zoomButton(HostedEvent, false);
        });

        HostedEvent.setOnMouseEntered(event -> {
            zoomButton(HostedEvent, true);
        });

        StaffEventParticipated.setOnMouseEntered(event -> {
            zoomButton(StaffEventParticipated, true);
        });

        StaffEventParticipated.setOnMouseExited(event -> {
            zoomButton(StaffEventParticipated, false);
        });
    }


    @FXML
    public void addMoney() {
        User currentUser = AllCollection.getInstance().getCurrentUser();
        NumberInputDialogController controller = Loader.loadNumberInputDialog("Top up money", "Enter top up amount :");
        if (controller.getIsConfirm()) {
            Double topUpAmount = Double.valueOf(controller.getInputText());
            currentUser.getWallet().topUp(topUpAmount);
            walletLabel.setText(String.format("%.2f", currentUser.getWallet().getAmount()));
            AllCollection.getInstance().writeWalletData();
        }
    }


    @FXML
    public void onShowHostedEvent() {
        try {
            FXRouter.goTo("hosted-events");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onShowEventParticipated() {
        try {
            FXRouter.goTo("events-history", "Participant");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onShowStaffEventParticipated() {
        try {
            FXRouter.goTo("events-history", "Staff");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onChangeEmail() {
        String emailRegex = "^(?=.{1,256}$)[A-Za-z0-9._%+-]+@(?!.*\\.{2})[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        TextInputDialogController controller = Loader.loadTextInputDialog("Change Email", "Enter your new email:", emailLabel.getText(), emailRegex);

        if (controller.getIsConfirm()) {
            String newEmail = controller.getInputText();
            User loggedInUser = AllCollection.getInstance().getCurrentUser();
            loggedInUser.setEmail(newEmail);
            AllCollection.getInstance().writeUser();
            emailLabel.setText(newEmail);
        }
    }

    @FXML
    public void onChangeProfileName() {
        TextInputDialogController controller = Loader.loadTextInputDialog("Change Profile Name", "Enter your new profile name:", profileNameLabel2.getText());

        if (controller.getIsConfirm()) {
            String newProfileName = controller.getInputText();
            User loggedInUser = AllCollection.getInstance().getCurrentUser();
            loggedInUser.setProfileName(newProfileName);
            AllCollection.getInstance().writeUser();
            profileNameLabel2.setText(newProfileName);
            profileNameLabel.setText(newProfileName);
        }
    }

    @FXML
    public void onChangePassword() {
        try {
            PageHistory.getInstance().pushStack("profile");
            FXRouter.goTo("change-password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onWithdrawMoney() {
        User currentUser = AllCollection.getInstance().getCurrentUser();
        ConfirmDialogController controller = Loader.loadDialog("Withdraw Money",
                "If you withdraw you will lose all your money", String.valueOf(currentUser.getWallet().getAmount()));
        if (controller.getIsConfirm()) {
            currentUser.getWallet().withdraw();
            AllCollection.getInstance().writeWalletData();
            walletLabel.setText("0.00");
        }

    }

    private void zoomButton(Button button, boolean zoomIn) {
        double targetScale = zoomIn ? 1.2 : 1.0;

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), button);
        scaleTransition.setToX(targetScale);
        scaleTransition.setToY(targetScale);
        scaleTransition.play();
    }

    private void setImage(User user) {
        String[] imageExtensions = { ".png", ".jpg", ".jpeg" };
        String userImageFileName = null;

        for (String extension : imageExtensions) {
            userImageFileName = "data/users/users-images/" + user.getUserId() + extension;
            File imageFile = new File(userImageFileName);

            if (imageFile.exists()) {
                Image userImage = new Image(imageFile.toURI().toString());
                profileImageView.setImage(userImage);
                AdjustImageView.setViewPortImageView(profileImageView, userImage);

                break;
            } else {
                profileImageView.setImage(DEFAULT_IMAGE);
            }
        }
    }


    @FXML
    private void onUpdateProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                User loggedInUser = AllCollection.getInstance().getCurrentUser();
                deleteOldProfileImage(loggedInUser);
                loggedInUser.changeProfileImage(selectedFile);
                loggedInUser.setProfileImageFormat(getFileExtension(selectedFile));
                AllCollection.getInstance().writeUser();
                Image image = new Image(selectedFile.toURI().toString());
                profileImageView.setImage(image);
                AdjustImageView.setViewPortImageView(profileImageView, image);
                AdjustImageView.radiusImageView(profileImageView, profileImageView.getFitHeight());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void deleteOldProfileImage(User user) {
        String oldImageFilePath = "data/users/users-images/" + user.getUserId() + "." + user.getProfileImageFormat();
        File oldImageFile = new File(oldImageFilePath);
        if (oldImageFile.exists()) {
            if (!oldImageFile.delete()) {
                System.err.println("Failed to delete the old profile image.");
            }
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}
