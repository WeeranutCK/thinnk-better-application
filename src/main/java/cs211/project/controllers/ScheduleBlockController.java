package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Schedule;
import cs211.project.models.StaffTeam;
import cs211.project.models.User;
import cs211.project.models.collections.ScheduleList;
import cs211.project.models.collections.StaffTeamList;
import cs211.project.services.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleBlockController {
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button participantsButton;
    @FXML
    private VBox scheduleComponentVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private Button teamButton;


    private String currentPage;
    private ArrayList<Schedule> selectedSchedule = new ArrayList<>();
    private boolean scheduleEditable;
    private Event event;
    private boolean isScheduleEnded;
    private String mode;
    private static final int SCHEDULE_BATCH_SIZE = 5;
    private ScheduleList scheduleList;
    private StaffTeam staffTeam;
    private int tailScheduleIndex;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        mode = "team";
        participantsButton.getStyleClass().addAll("primary-button");
        teamButton.getStyleClass().addAll("primary-button", "non-active");

        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (scheduleList.getScheduleArrayList().size() <= SCHEDULE_BATCH_SIZE)
            {
                isScheduleEnded = true;
            }

            if (tailScheduleIndex >= scheduleList.getScheduleArrayList().size() - 1) {
                isScheduleEnded = true;
            }

            if (newValue.doubleValue() >= 0.85 && !isScheduleEnded) {
                Platform.runLater(() -> scrollPane.setVvalue(0.84));
                showSchedule();
            }
        });

        tailScheduleIndex = 0;
        isScheduleEnded = false;
        scheduleEditable = false;
        addButton.setVisible(true);
        deleteButton.setVisible(true);
        scheduleList = new ScheduleList();

        String[] eventStatus = new String[]{"Any", "Soon", "On Going", "Ended"};
        sortComboBox.getItems().clear();
        for (String status: eventStatus) {
            sortComboBox.getItems().add(status);
        }
    }


    @FXML
    void onAddButtonClicked() {
        try {
            PageHistory.getInstance().pushStack(currentPage);
            FXRouter.goTo("add-activity", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSortComboBoxSelected() {
        clearSchedule();
        initialScheduleList();
        showSchedule();
    }

    @FXML
    private void swapFromParticipantsButton() {
        if (mode.equals("participants")) {
            swapMode();
            mode = "team";
        }
        clearSchedule();
        initialScheduleList();
        showSchedule();
    }

    @FXML
    private void swapFromTeamButton() {
        if (mode.equals("team")) {
            swapMode();
            mode = "participants";
        }
        clearSchedule();
        initialScheduleList();
        showSchedule();
    }

    @FXML
    private void onDeleteClicked() {
        ConfirmDialogController controller = Loader.loadDialog("Delete Activity",
                "Are you sure you want to delete selected schedules in ",
                event.getEventName());

        if (controller.getIsConfirm()) {
            for (Schedule schedule : selectedSchedule) {
                StaffTeam staffTeam = schedule.getStaffTeam();
                if (staffTeam != null) {
                    staffTeam.setSchedule(null);
                }
            }
            event.getActivityList().removeSchedules(selectedSchedule);
            AllCollection.getInstance().getScheduleList().removeSchedules(selectedSchedule);

            clearSchedule();
            initialScheduleList();
            showSchedule();
            AllCollection.getInstance().writeScheduleData();
        }
    }


    public void clearSchedule() {
        scheduleComponentVBox.getChildren().clear();
        scheduleList.getScheduleArrayList().clear();
        tailScheduleIndex = 0;
        isScheduleEnded = false;
    }

    private List<Schedule> filterSchedulesByStatus(String status) {
        long now = TimeConversion.getNowDate().getTime();

        return scheduleList.getScheduleArrayList().stream()
                .filter(schedule -> {
                    if ("Any".equals(status)) {
                        return true;
                    } else if ("Soon".equals(status)) {
                        return schedule.getStartTime().getTime() > now;
                    } else if ("Ongoing".equals(status)) {
                        return schedule.getStartTime().getTime() <= now && now <= schedule.getEndTime().getTime();
                    } else if ("Ended".equals(status)) {
                        return schedule.getEndTime().getTime() < now;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private void initialScheduleList() {
        if (mode.equals("team")) {
            if (event != null) {
                for (Schedule schedule : AllCollection.getInstance().getScheduleList().getScheduleArrayList()) {
                    if (schedule.getEvent() != null && schedule.getEvent().getEventId().equals(event.getEventId())) {
                        scheduleList.addNewSchedule(schedule);
                    }
                }
            }
        } else {
                if (event.getStaffTeamList().getStaffTeamArrayList().size() != 0) {
                    StaffTeamList staffTeamList = event.getStaffTeamList();
                    User user = AllCollection.getInstance().getCurrentUser();
                    StaffTeamList participatedTeam = staffTeamList.findTeamsOfUser(user);
                    for (StaffTeam temp : participatedTeam.getStaffTeamArrayList()) {
                        scheduleList.getScheduleArrayList().add(temp.getSchedule());
                }

            }
        }
    }

    public void setScheduleButtonVisible(boolean visible) {
        addButton.setVisible(visible);
        deleteButton.setVisible(visible);
    }

    public void setScheduleDetails(Event event, Object staffTeam, String currentPage) {
        this.event = event;
        this.currentPage = currentPage;

        clearSchedule();

        if (staffTeam instanceof StaffTeam) {
            this.staffTeam = (StaffTeam) staffTeam;
        } else if (staffTeam instanceof StaffTeamList staffTeamList) {
            for (StaffTeam eachStaffTeam : staffTeamList.getStaffTeamArrayList()) {
                this.staffTeam = eachStaffTeam;
                initialScheduleList();
            }
        }
        showSchedule();
    }

    public void setSortComboBox(String status) {
        sortComboBox.setValue(status);
        clearSchedule();
        initialScheduleList();
        showSchedule();
    }

    public void showSchedule() {
        if (scheduleList.getScheduleArrayList().size() > 0 &&
                Math.min(tailScheduleIndex,
                        scheduleList.getScheduleArrayList().size() - 1) < scheduleList.getScheduleArrayList().size()) {

            String selectedStatus = sortComboBox.getValue();

            List<Schedule> filteredSchedules = filterSchedulesByStatus(selectedStatus);
            Collections.sort(filteredSchedules);

            for (int i = tailScheduleIndex; i <= Math.min(tailScheduleIndex + SCHEDULE_BATCH_SIZE, filteredSchedules.size() - 1); i++) {
                Schedule schedule = filteredSchedules.get(i);
                try {
                    FXMLLoader componentLoader = new FXMLLoader(
                            Loader.class.getResource("/cs211/project/components/schedule.fxml")
                    );
                    VBox childVBox = componentLoader.load();

                    ScheduleController scheduleController = componentLoader.getController();
                    scheduleController.setScheduleDetails(schedule, scheduleEditable, currentPage, this);

                    scheduleComponentVBox.getChildren().add(childVBox);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (tailScheduleIndex == 0) Platform.runLater(() -> scrollPane.setVvalue(0));
            tailScheduleIndex += SCHEDULE_BATCH_SIZE + 1;
        }
    }

    private void swapMode() {
        if (teamButton.getStyleClass().contains("non-active")) {
            teamButton.getStyleClass().remove("non-active");
            participantsButton.getStyleClass().add("non-active");
        } else {
            participantsButton.getStyleClass().remove("non-active");
            teamButton.getStyleClass().add("non-active");
        }
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setScheduleEditable(boolean scheduleEditable) {
        addButton.setVisible(scheduleEditable);
        deleteButton.setVisible(scheduleEditable);
        this.scheduleEditable = scheduleEditable;
    }

    public void addSelectedSchedule(Schedule schedule) {
        selectedSchedule.add(schedule);
    }

    public void removeSelectedSchedule(Schedule schedule) {
        selectedSchedule.remove(schedule);
    }
}
