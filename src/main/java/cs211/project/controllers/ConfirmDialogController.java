package cs211.project.controllers;

import cs211.project.services.DialogWindow;
import javafx.application.Platform;

public class ConfirmDialogController extends DialogWindow {
    @Override
    public void onCancelButtonClicked() {
        isConfirm = false;
        Platform.runLater(() -> {
            dialogStage.close();
        });
    }

    @Override
    public void onConfirmButtonClicked() {
        isConfirm = true;
        Platform.runLater(() -> {
            dialogStage.close();
        });
    }
}
