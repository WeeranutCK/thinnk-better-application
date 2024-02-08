package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.User;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditTeamController {
    @FXML
    private CheckBox autoAcceptingCheckBox;
    @FXML
    private CheckBox multiTeamAllowedCheckBox;
    @FXML
    private VBox registrantsBlockVBox;
    @FXML
    private DatePicker endDateDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Label errorEndDateLabel;
    @FXML
    private Label errorEndTimeLabel;
    @FXML
    private Label errorMaxParticipantsLabel;
    @FXML
    private Label errorStartDateLabel;
    @FXML
    private Label errorStartTimeLabel;
    @FXML
    private Label errorTeamNameLabel;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label eventEndTimeLabel;
    @FXML
    private Label eventStartTimeLabel;
    @FXML
    private Label maxParticipantsLabel;
    @FXML
    private TextField maxParticipantsTextField;
    @FXML
    private VBox navBarVBox;
    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TextField teamNameTextField;
    @FXML
    private Label teamNameLabel;
    @FXML
    private VBox staffBlockVBox;
    private StaffTeam currentStaffTeam;
    private Event currentEvent;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        currentStaffTeam = (StaffTeam) FXRouter.getData();
        currentEvent = currentStaffTeam.getEvent();
        setupPage();
    }


    private void setupPage() {
        teamNameLabel.setText(currentStaffTeam.getTeamName());
        Loader.loadNavBar(navBarVBox);
        teamNameTextField.setText(currentStaffTeam.getTeamName());
        String showEventNameLabel = "Event : ";
        eventNameLabel.setText("(" + showEventNameLabel + currentEvent.getEventName() + ")");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        eventStartTimeLabel.setText(simpleDateFormat.format(currentEvent.getStartTime()));
        eventEndTimeLabel.setText(simpleDateFormat.format(currentEvent.getEndTime()));

        if (currentEvent.isAnyMaxTeamMembers()) {
            maxParticipantsLabel.setText("(Max Participant is any.)");
        } else {
            maxParticipantsLabel.setText("(Max Participant is " + currentEvent.getMaxTeamMembers() + ".)");
        }

        setupField();
        clearErrorLabels();
        setDatePicker();
        setupTableView();

    }

    private void setupField() {
        teamNameTextField.setText(currentStaffTeam.getTeamName());
        endTimeTextField.setText(String.format("%02d:%02d", currentStaffTeam.getEndTimeRegistration().getHours(), currentStaffTeam.getEndTimeRegistration().getMinutes()));
        startTimeTextField.setText(String.format("%02d:%02d", currentStaffTeam.getStartTimeRegistration().getHours(), currentStaffTeam.getStartTimeRegistration().getMinutes()));
        LocalDate localDateStart = currentStaffTeam.getStartTimeRegistration().toInstant().atZone(TimeConversion.getZoneOffset()).toLocalDate();
        LocalDate localDateEnd = currentStaffTeam.getEndTimeRegistration().toInstant().atZone(TimeConversion.getZoneOffset()).toLocalDate();
        startDateDatePicker.setValue(localDateStart);
        endDateDatePicker.setValue(localDateEnd);
        maxParticipantsTextField.setText(String.valueOf(currentStaffTeam.getMaxMembers()));
        autoAcceptingCheckBox.setSelected(currentStaffTeam.getIsTeamAutoAccepting());
        multiTeamAllowedCheckBox.setSelected(currentStaffTeam.getIsMultiTeamAllowed());
        setCheckBox();
    }

    private void clearErrorLabels() {
        errorEndDateLabel.setText("");
        errorEndTimeLabel.setText("");
        errorTeamNameLabel.setText("");
        errorStartDateLabel.setText("");
        errorStartTimeLabel.setText("");
        errorMaxParticipantsLabel.setText("");
    }

    private void setDatePicker() {
        endDateDatePicker.setEditable(false);
        startDateDatePicker.setEditable(false);
        startDateDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate localDateEnd = currentEvent.getEndTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
                setDisable(date.isBefore(LocalDate.now()) || date.isAfter(localDateEnd));
            }
        });
        endDateDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate localDateEnd = currentEvent.getEndTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
                setDisable(date.isBefore(LocalDate.now()) || date.isAfter(localDateEnd));
            }
        });
    }

    private void setupTableView() {
        staffBlockVBox.getChildren().clear();
        StaffBlockController staffBlockController = Loader.loadStaffBlock(staffBlockVBox, currentStaffTeam, this);
        registrantsBlockVBox.getChildren().clear();
        Loader.loadRegistrantsBlock(registrantsBlockVBox, currentStaffTeam, staffBlockController);
    }

    private ArrayList<Date> checkValidDateToEdit() {
        ArrayList<Date> dateArrayList = DateTimeCheck.createDate(startDateDatePicker,
                endDateDatePicker,
                errorStartDateLabel,
                errorEndDateLabel,
                startTimeTextField,
                endTimeTextField,
                errorStartTimeLabel,
                errorEndTimeLabel);
        if (dateArrayList != null) {
            boolean isStartTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(0), errorStartDateLabel, errorStartTimeLabel);
            boolean isEndTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEndDateLabel, errorEndTimeLabel);
            boolean checkStartTime = false;
            boolean checkEndTime = false;
            if (dateArrayList.get(0).after(currentEvent.getEndTime())){
                errorStartDateLabel.setText("Cannot start after the event ends");
                errorStartTimeLabel.setText("Cannot start after the event ends");
            } else {
                checkStartTime = true;
            }

            if (dateArrayList.get(1).after(currentEvent.getEndTime())) {
                errorEndDateLabel.setText("Cannot end after the event ends");
                errorEndTimeLabel.setText("Cannot end after the event ends");
            } else {
                checkEndTime = true;
            }

            if (checkStartTime && checkEndTime){
                if (!currentStaffTeam.getStartTimeRegistration().equals(dateArrayList.get(0)) && (isStartTimeInThePast || isEndTimeInThePast)) {

                } else if (!currentStaffTeam.getEndTimeRegistration().equals(dateArrayList.get(1)) && isEndTimeInThePast) {
                    errorStartDateLabel.setText("");
                    errorStartTimeLabel.setText("");
                } else {
                    errorStartDateLabel.setText("");
                    errorStartTimeLabel.setText("");
                    errorEndDateLabel.setText("");
                    errorEndTimeLabel.setText("");
                    return dateArrayList;
                }
            }
        }

        return null;
    }

    private String checkValidTeamNameToEdit() {
        if (teamNameTextField.getText().trim().isEmpty()) {
            errorTeamNameLabel.setText("Cannot be empty.");
        } else if (!teamNameTextField.getText().equals(currentStaffTeam.getTeamName()) && currentEvent.getStaffTeamList().findStaffTeamByName(teamNameTextField.getText()) != null) {
            errorTeamNameLabel.setText("Team name already exists.");
        } else {
            return teamNameTextField.getText();
        }
        return null;
    }

    private Integer checkMaxMemberToEdit() {
        Integer maxMembers;
        try {
            maxMembers = Integer.parseInt(maxParticipantsTextField.getText());
        } catch (RuntimeException e) {
            maxMembers = null;
        }

        if (maxParticipantsTextField.getText().isEmpty()) {
            errorMaxParticipantsLabel.setText("Max participants cannot be empty.");
        } else if (maxMembers == null) {
            errorMaxParticipantsLabel.setText("Max participants must be a valid number.");
        } else if (maxMembers <= 0) {
            errorMaxParticipantsLabel.setText("Max participants must be greater than zero.");
        } else if (!currentEvent.isAnyMaxTeamMembers() && maxMembers > currentEvent.getMaxTeamMembers()) {
            errorMaxParticipantsLabel.setText("The number of participants has exceeded the limit.");
        } else {
            if (maxMembers < currentStaffTeam.getStaffList().getStaffArrayList().size()) {
                Loader.loadWarningDialog("Change Not Allowed",
                                         "Before adjusting the max member limit, kindly consider reducing the current number of team members, as it currently exceeds the desired value.");
            } else {
                return maxMembers;
            }
        }
        return  null;
    }

    private void editStaffTeam(String staffTeamName, Date startTime, Date endTime, int maxMembers) {
        currentStaffTeam.setTeamName(staffTeamName);
        currentStaffTeam.setStartTimeRegistration(startTime);
        currentStaffTeam.setEndTimeRegistration(endTime);
        currentStaffTeam.setMaxMembers(maxMembers);
        currentStaffTeam.setTeamAutoAccepting(autoAcceptingCheckBox.isSelected());
        currentStaffTeam.setMultiTeamAllowed(multiTeamAllowedCheckBox.isSelected());

    }

    @FXML
    private void onBack() {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm",
                            "Are you certain you want to discard your edits?",
                            currentStaffTeam.getTeamName());
            if (confirmDialogController.getIsConfirm()) {
                try {
                    FXRouter.goTo(PageHistory.getInstance().popStack(), currentStaffTeam);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    @FXML
    private void onSubmit() {
        clearErrorLabels();
        ArrayList<Date> dateToEdit = checkValidDateToEdit();
        String teamNameToEdit = checkValidTeamNameToEdit();
        Integer TeamMemberToEdit = checkMaxMemberToEdit();

        if(dateToEdit != null && teamNameToEdit != null && TeamMemberToEdit != null) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Editing the Activity",
                            "Are you sure you want to edit this activity?",
                            teamNameToEdit);
            if (confirmDialogController.getIsConfirm()) {
                editStaffTeam(teamNameToEdit,
                        dateToEdit.get(0),
                        dateToEdit.get(1),
                        TeamMemberToEdit);
                AllCollection.getInstance().writeStaffTeamData();
                AllCollection.getInstance().writeEventData();
                clearErrorLabels();
                setupField();
                teamNameLabel.setText(currentStaffTeam.getTeamName());
            }
        }

    }

    private  void setCheckBox() {
        multiTeamAllowedCheckBox.setOnAction(event -> {
            if (!multiTeamAllowedCheckBox.isSelected() && hasStaffInMultipleTeams()) {
                multiTeamAllowedCheckBox.setSelected(currentStaffTeam.getIsMultiTeamAllowed());
                Loader.loadWarningDialog("Change Not Allowed",
                                         "Please remove staff members who are on multiple teams before making change.");
            }
        });
    }

    private boolean hasStaffInMultipleTeams() {
        for (Staff staff : currentStaffTeam.getStaffList().getStaffArrayList()) {
            User user = staff.getUser();
            if (currentEvent.getStaffTeamList().findTeamsOfUser(user).getNumberOfTeams() > 1) {
                return true;
            }
        }
        return false;
    }

    public void backToPreviousPage() {
        try {
            String page = PageHistory.getInstance().popStack();
            if (page.equals("team-detail")) {
                FXRouter.goTo(PageHistory.getInstance().popStack(), currentEvent);
            } else {
                FXRouter.goTo(page, currentEvent);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
