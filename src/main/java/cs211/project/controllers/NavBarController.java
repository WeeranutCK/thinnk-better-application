package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.PageHistory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;


public class NavBarController {
    @FXML
    private VBox navBarVBox;
    @FXML
    private Label timestampsLabel;


    @FXML
    private void initialize() {
        navBarVBox.setStyle(null);
        if (AllCollection.getInstance().getCurrentUser() == null || !AllCollection.getInstance().getCurrentUser().isAdmin()) {
            timestampsLabel.setVisible(false);
        }
    }


    @FXML
    private void onAboutUsLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("about-us");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEventsHistoryLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("events-history","All");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEventsLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("events");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onHostedEventsLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("hosted-events");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onMainLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("main");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onMyTeamLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("my-team");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onLogOutButtonClick() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("log-in");
            AllCollection.getInstance().logOutCurrentUSer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onProfileLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("profile");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSettingsLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("settings");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onTimestampsLabelClicked() {
        try {
            PageHistory.getInstance().clearStack();
            FXRouter.goTo("timestamps-user-list");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDisable(boolean b) {
        for (Object o : navBarVBox.getChildren()) {
            if (o instanceof VBox) {
                VBox vBox = (VBox) o;
                vBox.setDisable(b);
            } else if (o instanceof Button) {
                Button button = (Button) o;
                button.setDisable(b);
            }
        }
    }

    public void setDefaultTheme() {
        navBarVBox.getParent().getStylesheets().clear();
        navBarVBox.getParent().getStylesheets().add("/cs211/project/styles/nav-bar/styles.css");
    }
}