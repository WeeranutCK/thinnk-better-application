package cs211.project.controllers;

import cs211.project.cs211661project.ThinnkApplication;
import cs211.project.models.User;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class MainController {
    @FXML
    private Label detailsLabel;
    @FXML
    private Hyperlink hyperlinkText;
    @FXML
    private VBox navBarVBox;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        User currentUser = AllCollection.getInstance().getCurrentUser();
        profilePicture.setImage(currentUser.getProfileImage());
        AdjustImageView.setViewPortImageView(profilePicture, currentUser.getProfileImage());
        AdjustImageView.radiusImageView(profilePicture, profilePicture.getFitHeight());

        welcomeLabel.setText("Hi, " + AllCollection.getInstance().getCurrentUser().getProfileName());
        detailsLabel.setText("        It's time to turn your event dreams into reality. Join the growing community of event organizers who trust THINNK to simplify their journey and exceed expectations. Ready to begin? ");

        hyperlinkText.setOnAction(e -> {
            ThinnkApplication.hostServices.showDocument("https://www.freepik.com/free-photo/young-woman-freelancer-cafe-with-cup-coffee-gift-box_21995265.htm#page=2&query=communication&position=40&from_view=search&track=sph");
        });

        Loader.loadNavBar(navBarVBox);
    }

    @FXML
    private void onCreateEventButtonClick() {
        try {
            PageHistory.getInstance().pushStack("main");
            FXRouter.goTo("create-new-event");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onJoinEventButtonClick() {
        try {
            FXRouter.goTo("events");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
