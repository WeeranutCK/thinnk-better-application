package cs211.project.controllers;

import cs211.project.models.Schedule;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.PageHistory;
import cs211.project.services.TimeConversion;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ScheduleController {
    @FXML
    private Label activityLabel;
    @FXML
    private Label dayLabel;
    @FXML
    private VBox dayBackgroundVBox;
    @FXML
    private Label descriptionLabel;
    @FXML
    private VBox editVBox;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Label staffTeamLabel;
    @FXML
    private Label timeLabel;


    private ScheduleBlockController scheduleBlockController;
    private String currentPage;
    private boolean editable;
    private Schedule schedule;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        dayLabel.setText("");
        activityLabel.setText("");
        descriptionLabel.setText("");
        timeLabel.setText("");
        staffTeamLabel.setText("");
        editable = false;

        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                scheduleBlockController.addSelectedSchedule(schedule);
            } else {
                scheduleBlockController.removeSelectedSchedule(schedule);
            }
        });
    }

    @FXML
    private void onEditActivityClicked() {
        try {
            PageHistory.getInstance().pushStack(currentPage);
            FXRouter.goTo("edit-activity", schedule);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String formatTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy, HH:mm a", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault());
        String formattedStartTime = formatter.format(schedule.getStartTime().toInstant());
        String formattedEndTime = formatter.format(schedule.getEndTime().toInstant());

        return formattedStartTime + " - " + formattedEndTime;
    }

    private void setDayStyleClass() {
        String dayOfWeek = switch (schedule.getStartDayString()) {
            case "SUNDAY" -> "sunday";
            case "MONDAY" -> "monday";
            case "TUESDAY" -> "tuesday";
            case "WEDNESDAY" -> "wednesday";
            case "THURSDAY" -> "thursday";
            case "FRIDAY" -> "friday";
            case "SATURDAY" -> "saturday";
            default -> throw new IllegalStateException("Invalid day of the week value!");
        };

        dayBackgroundVBox.getStyleClass().removeIf(styleClass -> styleClass.matches("(monday|tuesday|wednesday|thursday|friday|saturday|sunday)"));
        dayBackgroundVBox.getStyleClass().add(dayOfWeek);
    }

    public void setScheduleDetails(Schedule schedule, Boolean editable, String currentPage, ScheduleBlockController scheduleBlockController) {
        this.scheduleBlockController = scheduleBlockController;
        this.schedule = schedule;
        this.editable = editable;
        this.currentPage = currentPage;

        updateUI();
        setDayStyleClass();
    }

    private void updateUI() {
        dayLabel.setText(schedule.getStartDayString());
        activityLabel.setText(schedule.getActivityName());
        descriptionLabel.setText(schedule.getDescription());

        long now = TimeConversion.getNowDate().getTime();
        String timeLabelString = formatTimeRange();
        if (schedule.getStartTime().getTime() > now) {
            timeLabelString += " (Soon)";
        } else if (schedule.getStartTime().getTime() <= now && now <= schedule.getEndTime().getTime()) {
            timeLabelString += " (On Going)";
        } else if (schedule.getEndTime().getTime() < now) {
            timeLabelString += " (Ended)";
        }
        timeLabel.setText(timeLabelString);

        if (schedule.getStaffTeam() != null) {
            staffTeamLabel.setText(schedule.getStaffTeam().getTeamName());
        } else {
            staffTeamLabel.setText("None");
        }

        if (!editable) {
            editVBox.getChildren().clear();
            editVBox.setManaged(false);
        }
    }
}
