package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Schedule;
import cs211.project.models.StaffTeam;
import cs211.project.models.collections.ScheduleList;
import cs211.project.models.collections.StaffList;
import cs211.project.models.collections.StaffTeamList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class EditEventController {
    @FXML
    private CheckBox anyMaxTeamMembersCheckBox;
    @FXML
    private ComboBox categoryComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Circle editPen;
    @FXML
    private CheckBox enableStaffTeamCheckBox;
    @FXML
    private DatePicker endDateDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Label errorAmountMaxParticipantsLabel;
    @FXML
    private Label errorEventEndDateLabel;
    @FXML
    private Label errorEventEndTimeLabel;
    @FXML
    private Label errorEventNameLabel;
    @FXML
    private Label errorEventStartDateLabel;
    @FXML
    private Label errorEventStartTimeLabel;
    @FXML
    private Label errorMaxTeamMembersLabel;
    @FXML
    private Label errorMaxTeamsLabel;
    @FXML
    private Label errorPlaceLabel;
    @FXML
    private Label errorPriceLabel;
    @FXML
    private ImageView eventImageView;
    @FXML
    private TextField eventNameTextField;
    @FXML
    private CheckBox freePriceCheckBox;
    @FXML
    private TextField maxParticipantsTextField;
    @FXML
    private TextField maxTeamMembersTextField;
    @FXML
    private TextField maxTeamsTextField;
    @FXML
    private VBox navBarVBox;
    @FXML
    private CheckBox noLimitMaxParticipantsCheckBox;
    @FXML
    private CheckBox noLimitMaxTeamsCheckBox;
    @FXML
    private TextField placeTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private VBox scheduleVBox;
    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label errorStartDateParticipantLabel;
    @FXML
    private Label errorEndDateParticipantLabel;
    @FXML
    private Label errorStartTimeParticipantLabel;
    @FXML
    private Label errorEndTimeParticipantLabel;
    @FXML
    private DatePicker participatedRegistrationStartDateDatePicker;
    @FXML
    private DatePicker participatedRegistrationEndDateDatePicker;
    @FXML
    private TextField participatedRegistrationStartTimeTextField;
    @FXML
    private TextField participatedRegistrationEndTimeTextField;


    private Event event;
    private ScheduleBlockController scheduleBlockController;
    private File selectedImageFile;
    private StaffTeam currentEventStaffTeam;
    private final Image DEFAULT_IMAGE = new Image(getClass().getResourceAsStream("/cs211/project/images/background-image.png"));


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        event = (Event)FXRouter.getData();
        String filePath = "/cs211/project/images/";
        StaffList currentStaffList = AllCollection.getInstance().getStaffList();
        ScheduleList scheduleList = AllCollection.getInstance().getScheduleList();
        StaffTeamList staffTeamOfEvent = event.getStaffTeamList();
        for (StaffTeam staffTeam : staffTeamOfEvent.getStaffTeamArrayList()) {
            currentEventStaffTeam = staffTeam;
        }

        setImage(event);

        Image editIcon = new Image (getClass().getResourceAsStream(filePath + "edit-round-icon.png"));
        editPen.setFill(new ImagePattern(editIcon));

        setDate();
        disableStaffTeam();
        setErrorLabel();
        initializeCategoryComboBox();
        setTextFieldData();

        Loader.loadNavBar(navBarVBox);
        StatusSetting.setupStatusLabel(statusLabel, event, 15);

        if (currentStaffList != null) {
            setUpScheduleComponent();
        } else {
            scheduleBlockController = Loader.loadSchedule(scheduleVBox);
            scheduleBlockController.setEvent(event);
            scheduleBlockController.setScheduleDetails(event, null, "edit-event");
            scheduleBlockController.setScheduleEditable(true);
        }
        scheduleBlockController.setSortComboBox("Any");
    }

    @FXML
    private void onAnyMaxTeamMembersCheckBox() {
        setCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
    }

    @FXML
    private void onBackButtonClick() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack(), event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEditEventImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Event Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );

        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.getPath());
            eventImageView.setImage(image);
            AdjustImageView.setViewPortImageView(eventImageView, image);
        }
    }

    @FXML
    private void onDeleteEventImage() {
        ConfirmDialogController confirmDialogController = Loader.loadDialog("Confirm Delete Image", "Are you sure you want to delete this event image?", event.getEventName());
        if (confirmDialogController.getIsConfirm()) {
            eventImageView.setImage(DEFAULT_IMAGE);
            AdjustImageView.setViewPortImageView(eventImageView, DEFAULT_IMAGE);
            selectedImageFile = null;
            deleteOldEventImage(event);
            event.changeEventImage(selectedImageFile);
            event.setEventImage();
        }
    }

    @FXML
    private void onEnableStaffTeamCheckBox() {
        if (enableStaffTeamCheckBox.isSelected()) {
            enableStaffTeam();
            setCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
            setCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
        } else {
            disableStaffTeam();
        }
    }

    @FXML
    private void onFreePriceCheckBox() {
        setCheckBox(freePriceCheckBox, priceTextField);
    }

    @FXML
    private void onNoLimitMaxParticipantsCheckBox() {
        setCheckBox(noLimitMaxParticipantsCheckBox, maxParticipantsTextField);
    }

    @FXML
    private void onNoLimitMaxTeamsCheckBox() {
        setCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
    }

    @FXML
    private void onSubmitButtonClick() {
        setErrorLabel();
        String editEventName = editEventName();
        ArrayList<Date> editDate = checkValidDateToEdit();
        Double editPrice = editPrice();
        Integer editMaxParticipants = editMaxParticipants();
        String editPlace = editPlace();
        String editCategory = (String) categoryComboBox.getSelectionModel().getSelectedItem();
        String editDescription = descriptionTextArea.getText();
        ArrayList<Date> participantRegistrationDateToEdit = checkValidParticipantRegistrationDateToEdit(editDate);

        boolean canSubmit = editEventName != null &&
                editDate != null &&
                editPrice != null &&
                editMaxParticipants != null &&
                editPlace != null &&
                participantRegistrationDateToEdit != null &&
                !invalidDate() &&
                !maxParticipantIsTooLow();

        if (!enableStaffTeamCheckBox.isSelected()) {
            if(canSubmit) {
                ConfirmDialogController confirmDialogController = Loader.loadDialog("Confirm Editing Event", "Are you sure you want to submit the edited event?", editEventName);
                if (confirmDialogController.getIsConfirm()) {
                    editEvent(editEventName,
                            editCategory,
                            editDescription,
                            editPlace,
                            editDate.get(0),
                            editDate.get(1),
                            editMaxParticipants,
                            editPrice,
                            selectedImageFile,
                            participantRegistrationDateToEdit.get(0),
                            participantRegistrationDateToEdit.get(1));
                    AllCollection.getInstance().writeEventData();
                    AllCollection.getInstance().writeUser();
                    resetPage();
                }
            }
        } else {
            Integer editMaxTeam = editMaxTeam();
            Integer editMaxTeamMember = editMaxTeamMember();

            boolean editStaffTeamIsNotNull = editMaxTeam != null &&
                    editMaxTeamMember != null;


            if(canSubmit && editStaffTeamIsNotNull) {
                ConfirmDialogController confirmDialogController = Loader.loadDialog("Confirm Editing Event", "Are you sure you want to submit the edited event?", editEventName);
                if (confirmDialogController.getIsConfirm()) {
                    editEvent(editEventName,
                            editCategory,
                            editDescription,
                            editPlace,
                            editDate.get(0),
                            editDate.get(1),
                            editMaxParticipants,
                            editPrice,
                            selectedImageFile,
                            true,
                            editMaxTeam,
                            editMaxTeamMember,
                            participantRegistrationDateToEdit.get(0),
                            participantRegistrationDateToEdit.get(1));
                    AllCollection.getInstance().writeEventData();
                    AllCollection.getInstance().writeUser();
                    resetPage();
                }
            }
        }
    }

    @FXML
    private void onViewAllTeamButtonClick() {
        try {
            PageHistory.getInstance().pushStack("edit-event");
            FXRouter.goTo("all-team", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Date> checkValidDateToEdit() {
        ArrayList<Date> dateArrayList = DateTimeCheck.createDate(startDateDatePicker,
                                                                 endDateDatePicker,
                                                                 errorEventStartDateLabel,
                                                                 errorEventEndDateLabel,
                                                                 startTimeTextField,
                                                                 endTimeTextField,
                                                                 errorEventStartTimeLabel,
                                                                 errorEventEndTimeLabel);
        if (dateArrayList != null) {
            boolean isEndTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEventEndDateLabel, errorEventEndTimeLabel);
            if (startTimeStayTheSame(startDateDatePicker, event.getStartTime(), startTimeTextField) && !isEndTimeInThePast) {
                errorEventStartDateLabel.setText("");
                errorEventEndDateLabel.setText("");
                return dateArrayList;
            } else if (!startTimeStayTheSame(startDateDatePicker, event.getStartTime(), startTimeTextField)) {
                boolean isStartTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(0), errorEventStartDateLabel, errorEventStartTimeLabel);
                if (!isStartTimeInThePast && !isEndTimeInThePast) {
                    return dateArrayList;
                }
            }
        }
        return null;
    }

    private boolean startTimeStayTheSame(DatePicker startDateDatePicker, Date startTime, TextField startTimeTextField) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        LocalDate localDateStart = startTime.toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
        boolean isOldStartDate = dateTimeFormatter.format(startDateDatePicker.getValue()).equals(dateTimeFormatter.format(localDateStart));
        boolean isOldStartTime = startTimeTextField.getText().trim().equals(simpleTimeFormat.format(startTime));

        if (isOldStartTime && isOldStartDate) {
            return true;
        }
        return false;
    }

    private ArrayList<Date> checkValidParticipantRegistrationDateToEdit(ArrayList<Date> eventDate) {
        if (eventDate == null || eventDate.size() != 2) {
            errorStartDateParticipantLabel.setText("Event period in need.");
            errorEndDateParticipantLabel.setText("Event period in need.");
            errorStartTimeParticipantLabel.setText("Event period in need.");
            errorEndTimeParticipantLabel.setText("Event period in need.");
            return null;
        }

        ArrayList<Date> dateArrayList = DateTimeCheck.createDate(
                participatedRegistrationStartDateDatePicker,
                participatedRegistrationEndDateDatePicker,
                errorStartDateParticipantLabel,
                errorEndDateParticipantLabel,
                participatedRegistrationStartTimeTextField,
                participatedRegistrationEndTimeTextField,
                errorStartTimeParticipantLabel,
                errorEndTimeParticipantLabel);

        if (dateArrayList != null) {
            boolean checkStartTime = false;
            boolean checkEndTime = false;
            if (dateArrayList.get(0).after(eventDate.get(1))){
                errorStartDateParticipantLabel.setText("Cannot start after the event ends.");
                errorStartTimeParticipantLabel.setText("Cannot start after the event ends.");
            } else {
                checkStartTime = true;
            }

            if (dateArrayList.get(1).after(eventDate.get(1))) {
                errorEndDateParticipantLabel.setText("Cannot end after the event ends.");
                errorEndTimeParticipantLabel.setText("Cannot end after the event ends.");
            } else {
                checkEndTime = true;
            }

            if (dateArrayList != null) {
                boolean isEndTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEndDateParticipantLabel, errorEndTimeParticipantLabel);
                if (startTimeStayTheSame(participatedRegistrationStartDateDatePicker, event.getParticipatedRegistrationStartTime(), participatedRegistrationStartTimeTextField) && !isEndTimeInThePast) {
                    if (checkStartTime && checkEndTime){
                        errorStartTimeParticipantLabel.setText("");
                        errorEndTimeParticipantLabel.setText("");
                        return dateArrayList;
                    }
                } else if (!startTimeStayTheSame(participatedRegistrationStartDateDatePicker, event.getParticipatedRegistrationStartTime(), participatedRegistrationStartTimeTextField)) {
                    boolean isInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(0), errorStartDateParticipantLabel, errorStartTimeParticipantLabel) || DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEndDateParticipantLabel, errorEndTimeParticipantLabel);
                    if (!isInThePast) {
                        return dateArrayList;
                    }
                }
            }
            return null;
        }
        return null;
    }

    private void deleteOldEventImage(Event event) {
        String oldImageFilePath = "data/events/event-images/" + event.getEventId() + "." + event.getEventImageFormat();
        File oldImageFile = new File(oldImageFilePath);
        if (oldImageFile.exists()) {
            oldImageFile.delete();
            if (!oldImageFile.delete()) {
                System.err.println("Failed to delete the old profile image.");
            }
        }
    }

    private boolean invalidDate() {
        ArrayList<Date> dateArrayList = DateTimeCheck.createDate(startDateDatePicker,
                endDateDatePicker,
                errorEventStartDateLabel,
                errorEventEndDateLabel,
                startTimeTextField,
                endTimeTextField,
                errorEventStartTimeLabel,
                errorEventEndTimeLabel);
        for (Schedule schedule : event.getActivityList().getScheduleArrayList()) {
            if (dateArrayList.get(0).after(schedule.getStartTime()) || dateArrayList.get(1).before(schedule.getEndTime())) {
                WarningDialogController warningDialog;
                warningDialog = Loader.loadWarningDialog("Warning",
                        "Start/End date can't be out of schedule's time frame.");
                return true;
            }
        }
        if (currentEventStaffTeam != null) {
            if (dateArrayList.get(1).before(currentEventStaffTeam.getEndTimeRegistration())) {
                WarningDialogController warningDialog;
                warningDialog = Loader.loadWarningDialog("Warning",
                        "Start/End date can't be out of staff registration time frame.");
                return true;
            }
        }
        return false;
    }

    private void disableStaffTeam() {
        anyMaxTeamMembersCheckBox.setDisable(true);
        maxTeamMembersTextField.setEditable(false);
        noLimitMaxTeamsCheckBox.setDisable(true);
        maxTeamsTextField.setEditable(false);
        noLimitMaxTeamsCheckBox.setSelected(false);
        anyMaxTeamMembersCheckBox.setSelected(false);
        maxTeamsTextField.setText("");
        maxTeamMembersTextField.setText("");
    }

    public void editEvent(String eventName,
                          String category,
                          String description,
                          String place,
                          Date startTime,
                          Date endTime,
                          int maxParticipants,
                          double price,
                          File selectedFile,
                          Date participatedRegistrationStartTime,
                          Date participatedRegistrationEndTime) {
        event.setEventName(eventName);
        event.setCategory(category);
        event.setDescription(description);
        event.setPlace(place);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setMaxParticipants(maxParticipants);
        event.setPrice(price);
        event.setEventImage();
        event.setParticipatedRegistrationStartTime(participatedRegistrationStartTime);
        event.setParticipatedRegistrationEndTime(participatedRegistrationEndTime);
        if (selectedFile != null) {
            event.changeEventImage(selectedFile);
        }
    }

    public void editEvent(String eventName,
                          String category,
                          String description,
                          String place,
                          Date startTime,
                          Date endTime,
                          int maxParticipants,
                          double price,
                          File selectedFile,
                          boolean enableStaffRegistration,
                          Integer editMaxTeam,
                          Integer editMaxTeamMember,
                          Date participatedRegistrationStartTime,
                          Date participatedRegistrationEndTime) {
        event.setEventName(eventName);
        event.setCategory(category);
        event.setDescription(description);
        event.setPlace(place);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setMaxParticipants(maxParticipants);
        event.setPrice(price);
        event.setEnableStaffRegistration(enableStaffRegistration);
        event.setMaxStaffTeams(editMaxTeam);
        event.setMaxTeamMembers(editMaxTeamMember);
        event.setParticipatedRegistrationStartTime(participatedRegistrationStartTime);
        event.setParticipatedRegistrationEndTime(participatedRegistrationEndTime);
        if (selectedFile != null) {
            event.changeEventImage(selectedFile);
        }
    }

    private String editEventName() {
        if (eventNameTextField.getText().equals("") || eventNameTextField.getText().trim().isEmpty()) {
            errorEventNameLabel.setText("Cannot be empty.");
        } else if (AllCollection.getInstance().getEventList().findEventByName(eventNameTextField.getText().trim()) != null &&
                !eventNameTextField.getText().trim().equals(event.getEventName())) {
            errorEventNameLabel.setText("Event name already exists.");
        } else {
            return eventNameTextField.getText().trim();
        }
        return null;
    }

    private Integer editMaxParticipants() {
        if (!noLimitMaxParticipantsCheckBox.isSelected()) {
            if (!maxParticipantIsTooLow()) {
                return intTextField(maxParticipantsTextField,
                        errorAmountMaxParticipantsLabel,
                        noLimitMaxParticipantsCheckBox);
            } else {
                WarningDialogController warningDialog;
                warningDialog = Loader.loadWarningDialog("Warning",
                        "Max participant can't be less then current participants. If you want to continue, please delete some participants.");
            }
        } else {
            event.setMaxParticipants(-1);
            setCheckBox(noLimitMaxParticipantsCheckBox, maxParticipantsTextField);
        }
        return event.getMaxParticipants();
    }

    private Integer editMaxTeam() {
        if (!noLimitMaxTeamsCheckBox.isSelected()) {
            if (!maxTeamIsTooLow()) {
                return intTextField(maxTeamsTextField,
                        errorMaxTeamsLabel,
                        noLimitMaxTeamsCheckBox);
            } else {
                WarningDialogController warningDialog;
                warningDialog = Loader.loadWarningDialog("Warning",
                        "Max staff team can't be less then current staff team.");
            }
        } else {
            event.setMaxStaffTeams(-1);
            setCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
        }
        return event.getMaxStaffTeams();
    }

    private Integer editMaxTeamMember() {
        if (!anyMaxTeamMembersCheckBox.isSelected()) {
            if (!maxTeamMemberIsTooLow()) {
                return intTextField(maxTeamMembersTextField,
                        errorMaxTeamMembersLabel,
                        anyMaxTeamMembersCheckBox);
            } else {
                WarningDialogController warningDialog;
                warningDialog = Loader.loadWarningDialog("Warning",
                        "Max team member can't be less then current team member.");
            }
        } else {
            event.setMaxTeamMembers(-1);
            setCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
        }
        return event.getMaxTeamMembers();
    }

    private String editPlace() {
        if (placeTextField.getText().equals("")) {
            errorPlaceLabel.setText("Cannot be empty.");
        } else {
            return placeTextField.getText();
        }
        return null;
    }

    private Double editPrice() {
        Double parseDouble;
        try {
            parseDouble = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            parseDouble = null;
        }
        if (freePriceCheckBox.isSelected()) {
            parseDouble = 0.0;
            return parseDouble;
        } else if (priceTextField.getText().equals("")) {
            errorPriceLabel.setText("Cannot be empty.");
        } else if (parseDouble == null){
            errorPriceLabel.setText("Invalid price format. Please enter a valid number.");
        } else if (parseDouble < 0) {
            errorPriceLabel.setText("Price cannot be minus.");
        } else {
            return parseDouble;
        }
        return null;
    }

    private void enableStaffTeam() {
        anyMaxTeamMembersCheckBox.setDisable(false);
        noLimitMaxTeamsCheckBox.setDisable(false);
        setCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
        setCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
    }

    private void initializeCategoryComboBox() {
        String[] categoryMenu = {
                "Art", "Business", "Comedy", "Community", "Concert",
                "Conference", "Cultural Festival", "Dance", "Education",
                "Exhibition", "Fashion", "Festival", "Film", "Food",
                "Games", "Health and Wellness", "Indoor", "Literature",
                "Music", "Nature", "Outdoor", "Party", "Public Holidays",
                "Religion", "Science", "Shopping", "Sport", "Technology",
                "Travel", "Workshop", "Other"};

        categoryComboBox.getItems().clear();
        for (String menu: categoryMenu) {
            categoryComboBox.getItems().add(menu);
        }
        categoryComboBox.getSelectionModel().select(event.getCategory());
    }

    private Integer intTextField(TextField textField, Label errorLabel, CheckBox checkBox) {
        Integer parseInt;
        try {
            parseInt = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            parseInt = null;
        }
        if (checkBox.isSelected()) {
            parseInt = -1;
            return parseInt;
        } else if (textField.getText().equals("")) {
            errorLabel.setText("Cannot be empty.");
        } else if (parseInt == null){
            errorLabel.setText("Please enter a valid number.");
        } else if (parseInt <= 0) {
            errorLabel.setText("Cannot be minus or zero.");
        } else {
            return parseInt;
        }
        return parseInt;
    }

    private boolean maxParticipantIsTooLow() {
        int participantFromTextField;
        try {
            participantFromTextField = Integer.parseInt(maxParticipantsTextField.getText());
        } catch (Exception e) {
            participantFromTextField = -1;
        }
        return participantFromTextField != -1 && Integer.parseInt(maxParticipantsTextField.getText()) < event.getParticipantList().getSize();
    }

    private boolean maxTeamMemberIsTooLow() {
        int teamMemberFromTextField;
        try {
            teamMemberFromTextField = Integer.parseInt(maxTeamMembersTextField.getText());
        } catch (Exception e) {
            teamMemberFromTextField = -1;
        }
        for (StaffTeam staffTeam1 : event.getStaffTeamList().getStaffTeamArrayList()) {
            return teamMemberFromTextField != -1 && Integer.parseInt(maxTeamMembersTextField.getText()) < staffTeam1.getAmountTeamMember();
        }
        return false;
    }

    private boolean maxTeamIsTooLow() {
        int teamFromTextField;
        try {
            teamFromTextField = Integer.parseInt(maxTeamsTextField.getText());
        } catch (Exception e) {
            teamFromTextField = -1;
        }
        return teamFromTextField != -1 && Integer.parseInt(maxTeamsTextField.getText()) < event.getStaffTeamList().getStaffTeamArrayList().size();
    }

    private void resetPage() {
        setErrorLabel();
        setTextFieldData();
        initializeCategoryComboBox();
        StatusSetting.setupStatusLabel(statusLabel, event, 15);
    }

    private void setCheckBox(CheckBox checkBox, TextField textField) {
        if (checkBox.isSelected()) {
            textField.setEditable(false);
            textField.setText(checkBox.getText());
        } else {
            textField.setText("");
            textField.setEditable(true);
        }
    }

    private void setDate() {
        endDateDatePicker.setEditable(false);
        startDateDatePicker.setEditable(false);
        participatedRegistrationStartDateDatePicker.setEditable(false);
        participatedRegistrationEndDateDatePicker.setEditable(false);
        DateTimeCheck.disableDateBeforeNow(startDateDatePicker);
        DateTimeCheck.disableDateBeforeNow(endDateDatePicker);
        DateTimeCheck.disableDateBeforeNow(participatedRegistrationStartDateDatePicker);
        DateTimeCheck.disableDateBeforeNow(participatedRegistrationEndDateDatePicker);
    }

    private void setErrorLabel() {
        errorEventEndDateLabel.setText("");
        errorEventEndTimeLabel.setText("");
        errorAmountMaxParticipantsLabel.setText("");
        errorMaxTeamMembersLabel.setText("");
        errorMaxTeamsLabel.setText("");
        errorEventNameLabel.setText("");
        errorPriceLabel.setText("");
        errorEventStartDateLabel.setText("");
        errorEventStartTimeLabel.setText("");
        errorPlaceLabel.setText("");
        errorStartDateParticipantLabel.setText("");
        errorEndDateParticipantLabel.setText("");
        errorStartTimeParticipantLabel.setText("");
        errorEndTimeParticipantLabel.setText("");
    }

    private void setImage(Event event) {
        eventImageView.setImage(event.getEventImage());
        AdjustImageView.setViewPortImageView(eventImageView, event.getEventImage());
    }

    private void setTextFieldData() {
        SimpleDateFormat timeOutputFormat = new SimpleDateFormat("HH:mm");
        LocalDate localDateStart = event.getStartTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
        LocalDate localDateEnd = event.getEndTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
        LocalDate localDateParticipantRegistrationStart = event.getParticipatedRegistrationStartTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
        LocalDate localDateParticipantRegistrationEnd = event.getParticipatedRegistrationEndTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();

        eventNameTextField.setText(event.getEventName());
        descriptionTextArea.setText(event.getDescription());
        placeTextField.setText(event.getPlace());
        startTimeTextField.setText(timeOutputFormat.format(event.getStartTime()));
        endTimeTextField.setText(timeOutputFormat.format(event.getEndTime()));
        participatedRegistrationStartTimeTextField.setText(timeOutputFormat.format(event.getParticipatedRegistrationStartTime()));
        participatedRegistrationEndTimeTextField.setText(timeOutputFormat.format(event.getParticipatedRegistrationEndTime()));

        if (event.getPrice() == 0.0) {
            freePriceCheckBox.setSelected(true);
            setCheckBox(freePriceCheckBox, priceTextField);
        } else {
            priceTextField.setText(String.format("%.2f",event.getPrice()));
        }

        if (event.getMaxParticipants() == -1) {
            noLimitMaxParticipantsCheckBox.setSelected(true);
            setCheckBox(noLimitMaxParticipantsCheckBox, maxParticipantsTextField);
        } else {
            maxParticipantsTextField.setText(String.valueOf(event.getMaxParticipants()));
        }

        startDateDatePicker.setValue(localDateStart);
        endDateDatePicker.setValue(localDateEnd);
        participatedRegistrationStartDateDatePicker.setValue(localDateParticipantRegistrationStart);
        participatedRegistrationEndDateDatePicker.setValue(localDateParticipantRegistrationEnd);

        if (event.getMaxStaffTeams() == 0 && event.getMaxTeamMembers() == 0) {
            enableStaffTeamCheckBox.setSelected(false);
        } else {
            enableStaffTeamCheckBox.setSelected(true);
            enableStaffTeam();

            if (event.getMaxStaffTeams() == -1) {
                noLimitMaxTeamsCheckBox.setSelected(true);
                setCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
            } else {
                maxTeamsTextField.setText(String.valueOf(event.getMaxStaffTeams()));
            }

            if (event.getMaxTeamMembers() == -1) {
                anyMaxTeamMembersCheckBox.setSelected(true);
                setCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
            } else {
                maxTeamMembersTextField.setText(String.valueOf(event.getMaxTeamMembers()));
            }
        }
    }

    private void setUpScheduleComponent() {
        if (scheduleBlockController != null) {
            scheduleBlockController.clearSchedule();
            scheduleVBox.getChildren().clear();
        }

        scheduleBlockController = Loader.loadSchedule(scheduleVBox);
        scheduleBlockController.setEvent(event);
        scheduleBlockController.setScheduleEditable(true);

        if (event != null && currentEventStaffTeam != null) {
            scheduleBlockController.setScheduleDetails(event, currentEventStaffTeam, "event-info");
            scheduleBlockController.showSchedule();
            scheduleBlockController.setSortComboBox("Any");
        }

        if (event.getStatus().equals("Ended")) {
            scheduleBlockController.setScheduleButtonVisible(false);
            scheduleBlockController.setScheduleEditable(false);
            scheduleBlockController.setSortComboBox("Any");
        }
    }
}
