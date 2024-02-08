package cs211.project.controllers;

import cs211.project.services.DialogWindow;
import javafx.application.Platform;

public class WarningDialogController extends DialogWindow {

    @Override
    public void onConfirmButtonClicked() {
        isConfirm = true;
        Platform.runLater(() -> {
            dialogStage.close();
        });
    }

    @Override
    public void onCancelButtonClicked() {

    }
}
