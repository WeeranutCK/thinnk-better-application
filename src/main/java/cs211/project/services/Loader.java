package cs211.project.services;

import cs211.project.controllers.*;
import cs211.project.models.StaffTeam;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Loader {
    public static ChatBlockController loadChat(VBox vBox) {
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/chat-block.fxml")
            );
            VBox childVBox = componentLoader.load();
            vBox.getChildren().add(childVBox);
            return componentLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfirmDialogController loadDialog(String dialogTitle, String message, String moreDetail) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/views/confirm-dialog.fxml"));
            Parent root = loader.load();
            ConfirmDialogController controller = loader.getController();

            controller.setDialogTitleLabel(dialogTitle);
            controller.setMessageLabel(message);
            controller.setTeamNameLabel(moreDetail);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(null);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static WarningDialogController loadWarningDialog(String dialogTitle, String moreDetail) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/views/warning-dialog.fxml"));
            Parent root = loader.load();
            WarningDialogController controller = loader.getController();

            controller.setDialogTitleLabel(dialogTitle);
            controller.setTeamNameLabel(moreDetail);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(null);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NavBarController loadNavBar(VBox vBox) {
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/nav-bar.fxml")
            );
            VBox childVBox = componentLoader.load();
            vBox.getChildren().add(childVBox);
            return componentLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ScheduleBlockController loadSchedule(VBox vBox) {
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/schedule-block.fxml")
            );
            VBox childVBox = componentLoader.load();
            vBox.getChildren().add(childVBox);
            return componentLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static StaffBlockController loadStaffBlock(VBox vBox, StaffTeam currentStaffTeam, Object controller) {
        StaffBlockController staffBlockController;
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/staff-block.fxml")
            );
            VBox childVBox = componentLoader.load();
            vBox.getChildren().add(childVBox);
            staffBlockController = componentLoader.getController();
            staffBlockController.setCurrentController(controller);
            staffBlockController.setDetails(currentStaffTeam);
            staffBlockController.showStaffBlock();
            return staffBlockController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RegistrantsBlockController loadRegistrantsBlock(VBox vBox, StaffTeam currentStaffTeam, StaffBlockController relativeStaffBlock) {
        RegistrantsBlockController registrantsBlockController;
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/registrants-block.fxml")
            );
            VBox childVBox = componentLoader.load();
            vBox.getChildren().add(childVBox);
            registrantsBlockController = componentLoader.getController();
            registrantsBlockController.setDetails(currentStaffTeam, relativeStaffBlock);
            registrantsBlockController.showBlock();
            return registrantsBlockController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadProgressBar(HBox progressBarHBox) {
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/progress-bar.fxml")
            );
            HBox childHBox = componentLoader.load();
            progressBarHBox.getChildren().add(childHBox);
            componentLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TextInputDialogController loadTextInputDialog(String dialogTitle, String message, String initialValue) {
        return loadTextInputDialog(dialogTitle, message, initialValue, null);
    }

    public static TextInputDialogController loadTextInputDialog(String dialogTitle, String message, String initialValue, String criteria) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/views/text-input-dialog.fxml"));
            Parent root = loader.load();
            TextInputDialogController controller = loader.getController();

            controller.setDialogTitleLabel(dialogTitle);
            controller.setMessageLabel(message);
            controller.setInitialValue(initialValue);
            controller.setRegExCriteria(criteria);
            controller.setTextFieldEnter();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(null);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NumberInputDialogController loadNumberInputDialog(String dialogTitle, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/views/number-input-dialog.fxml"));
            Parent root = loader.load();
            NumberInputDialogController controller = loader.getController();

            controller.setDialogTitleLabel(dialogTitle);
            controller.setMessageLabel(message);
            controller.setTextFieldEnter();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(null);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}