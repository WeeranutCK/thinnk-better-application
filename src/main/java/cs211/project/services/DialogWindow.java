package cs211.project.services;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class DialogWindow {
    @FXML
    public Label dialogTitleLabel;
    @FXML
    public Label messageLabel;
    @FXML
    public Label moreDetailLabel;


    protected Stage dialogStage;
    protected boolean isConfirm;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();
    }
    @FXML
    public void onCancelButtonClicked() {}
    @FXML
    public void onConfirmButtonClicked() {}


    public boolean getIsConfirm() {
        return isConfirm;
    }


    public void setDialogTitleLabel(String dialogTitle) {
        dialogTitleLabel.setText(dialogTitle);
    }

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
    }

    public void setTeamNameLabel(String moreDetail) {
        moreDetailLabel.setText(moreDetail);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    onConfirmButtonClicked();
                }
            }
        });
    }
}
