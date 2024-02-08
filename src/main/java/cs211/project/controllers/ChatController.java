package cs211.project.controllers;

import cs211.project.models.Chat;
import cs211.project.models.User;
import cs211.project.services.AdjustImageView;
import cs211.project.services.AllCollection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ChatController {
    @FXML
    private Label chatLabel;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Label timeLabel;
    @FXML
    private Label usernameLabel;


    private Chat chat;
    private User user;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        usernameLabel.setText("");
        chatLabel.setText("");
        timeLabel.setText("");

        String filePath = "/cs211/project/images/";
        Image profileImg = new Image (getClass().getResourceAsStream(filePath + "user.png"));
        profilePicture.setImage(profileImg);
        AdjustImageView.setViewPortImageView(profilePicture, profileImg);
        AdjustImageView.radiusImageView(profilePicture, profilePicture.getFitHeight());
    }


    public void setChatDetails(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
        updateUI();
    }

    private void updateUI() {
        usernameLabel.setText(user.getProfileName());
        chatLabel.setText(chat.getMessage());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm a", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault());

        String formattedTime = formatter.format(chat.getTime().toInstant());
        timeLabel.setText(formattedTime);

        Image profileImg = user.getProfileImage();
        profilePicture.setImage(profileImg);
        AdjustImageView.setViewPortImageView(profilePicture, profileImg);
    }
}
