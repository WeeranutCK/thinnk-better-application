package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.ProgressPercentage;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;


public class ProgressBarController {
    @FXML private ProgressBar progressBar;

    private Event event;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        event = (Event) FXRouter.getData();
        double progress = ProgressPercentage.getPercentage(event.getSold(), event.getMaxParticipants());
        if (event.isNoLimitMaxParticipant()) {
            progressBar.setProgress(-1);
        } else {
            progressBar.setProgress(progress);
        }

        String backgroundColor = "#131113";

        progressBar.setStyle("-fx-accent: " + getColorForPercentage(progress)
                                            + "; -fx-box-border: #131113"
                                            + "; -fx-control-inner-background: " + backgroundColor + ";");
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setStyle("-fx-accent: " + getColorForPercentage(newValue.doubleValue())
                                                + "; -fx-box-border: #131113"
                                                + "; -fx-control-inner-background: " + backgroundColor + ";");
        });
    }


    private String getColorForPercentage(double percentage) {
        if (percentage == 1) {
            return "#DA2525";
        } else if (percentage > 0.9 && percentage < 1) {
            return "#DA5025";
        } else if (percentage > 0.75 && percentage <= 0.9) {
            return "#DA8725";
        } else if (percentage > 0.5 && percentage <= 0.75) {
            return "#DABD25";
        } else if (percentage <= 0.5) {
            return "#14AE5C";
        } else {
            return "#DA2525";
        }
    }
}
