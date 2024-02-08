package cs211.project.services;

import cs211.project.models.Event;
import javafx.scene.control.Label;


public class StatusSetting {
    public static void setupStatusLabel(Label label, Event event, double radius) {
        String eventState = event.getStatus();
        String backgroundColor = statusColorChange(event);
        label.setStyle("-fx-background-color: " + backgroundColor + ";" +
                        "-fx-background-radius: " + radius + ";" +
                        "-fx-text-fill: #F0F0F0;" +
                        "-fx-alignment: CENTER;");
        label.setText(eventState);
    }

    private static String statusColorChange(Event event) {
        if (event.getStatus().equals("On Going")) {
            return "#14AE5C";
        }
        else if (event.getStatus().equals("Soon")) {
            return "#C1A02C";
        }
        else {
            return "#DA2525";
        }
    }
}
