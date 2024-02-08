package cs211.project.controllers;

import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class CreateNewEventController {
    @FXML
    private CheckBox anyMaxTeamMembersCheckBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextArea descriptionTextArea;
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
    private ImageView eventImageImageView;
    @FXML
    private CheckBox freePriceCheckBox;
    @FXML
    private TextField maxParticipantsTextField;
    @FXML
    private TextField maxTeamMembersTextField;
    @FXML
    private TextField maxTeamsTextField;
    @FXML
    private TextField nameEventTextField;
    @FXML
    private VBox navBarSpaceVBox;
    @FXML
    private CheckBox noLimitMaxParticipantsCheckBox;
    @FXML
    private CheckBox noLimitMaxTeamsCheckBox;
    @FXML
    private TextField placeTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private DatePicker participatedRegistrationStartDateDatePicker;
    @FXML
    private DatePicker participatedRegistrationEndDateDatePicker;
    @FXML
    private TextField participatedRegistrationStartTimeTextField;
    @FXML
    private TextField participatedRegistrationEndTimeTextField;
    @FXML
    private Label errorStartTimeParticipantLabel;
    @FXML
    private Label errorEndTimeParticipantLabel;
    @FXML
    private Label errorStartDateParticipantLabel;
    @FXML
    private Label errorEndDateParticipantLabel;


    private final Image DEFAULT_IMAGE = new Image(getClass().getResourceAsStream("/cs211/project/images/background-image.png"));
    private File selectedFile;
    private String latestPath;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        setupPage();
    }


    @FXML
    private void onAnyMaxTeamMembersCheckBox() {
        updateTextFieldFromCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
    }

    @FXML
        private void onBackButtonClick() {
            try {
                FXRouter.goTo(PageHistory.getInstance().popStack());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    @FXML
    private void onCreateButtonClick() {
        clearErrorLabels();
        String eventNameToCreate = checkValidEventNameToCreate();
        ArrayList<Date> eventDateToCreate = checkValidDateToCreate();

        Double priceToCreate = checkValidPriceToCreate();
        Integer maxParticipantsToCreate = checkValidMaxParticipantsToCreate();
        String placeToCreate = checkValidPlaceToCreate();
        String categoryToCreate = categoryComboBox.getSelectionModel().getSelectedItem();
        String descriptionToCreate = descriptionTextArea.getText();
        ArrayList<Date> participantRegistrationDateToCreate = checkValidParticipantRegistrationDateToCreate(eventDateToCreate);

        boolean canCreateGeneral = eventNameToCreate != null &&
                eventDateToCreate != null &&
                priceToCreate != null &&
                maxParticipantsToCreate != null &&
                placeToCreate != null &&
                participantRegistrationDateToCreate != null;


        if (!enableStaffTeamCheckBox.isSelected()) {
            if(canCreateGeneral ) {
                ConfirmDialogController confirmDialogController = Loader.loadDialog("Confirm Creating New Event", "Do you want to continue creating new event?", eventNameToCreate);
                if (confirmDialogController.getIsConfirm()) {
                    AllCollection.getInstance().getEventList().createNewEvent(eventNameToCreate,
                                                                categoryToCreate,
                                                                descriptionToCreate,
                                                                placeToCreate,
                                                                eventDateToCreate.get(0),
                                                                eventDateToCreate.get(1),
                                                                maxParticipantsToCreate,
                                                                priceToCreate,
                                                                AllCollection.getInstance().getCurrentUser(),
                                                                selectedFile,
                                                                participantRegistrationDateToCreate.get(0),
                                                                participantRegistrationDateToCreate.get(1));
                    AllCollection.getInstance().writeEventData();
                    AllCollection.getInstance().writeUser();
                    resetPage();
                }
            }
        } else {
            Integer maxTeamsToCreate = checkValidMaxTeamsToCreate();
            Integer maxTeamMembersToCreate = checkValidMaxTeamMembersToCreate();

            boolean canCreateStaffTeam = maxTeamsToCreate != null &&
                                         maxTeamMembersToCreate != null;

            if(canCreateGeneral && canCreateStaffTeam) {
                ConfirmDialogController confirmDialogController = Loader.loadDialog("Confirm Creating New Event", "Do you want to continue creating new event?", eventNameToCreate);
                if (confirmDialogController.getIsConfirm()) {
                    AllCollection.getInstance().getEventList().createNewEvent(eventNameToCreate,
                                                                categoryToCreate,
                                                                descriptionToCreate,
                                                                placeToCreate,
                                                                eventDateToCreate.get(0),
                                                                eventDateToCreate.get(1),
                                                                maxParticipantsToCreate,
                                                                priceToCreate,
                                                                AllCollection.getInstance().getCurrentUser(),
                                                                selectedFile,
                                                                true,
                                                                maxTeamsToCreate,
                                                                maxTeamMembersToCreate,
                                                                participantRegistrationDateToCreate.get(0),
                                                                participantRegistrationDateToCreate.get(1));
                    AllCollection.getInstance().writeEventData();
                    AllCollection.getInstance().writeUser();
                    resetPage();
                }
            }
        }
    }

    @FXML
    private void onDeleteImage() {
        eventImageImageView.setImage(DEFAULT_IMAGE);
        AdjustImageView.setViewPortImageView(eventImageImageView, DEFAULT_IMAGE);
        selectedFile = null;
    }

    @FXML
    private void onEditEventImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
        if (latestPath == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {
            fileChooser.setInitialDirectory(new File(latestPath));
        }

        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            Image image = new Image(selectedFile.getAbsolutePath());
            eventImageImageView.setImage(image);
            AdjustImageView.setViewPortImageView(eventImageImageView, image);
            latestPath = selectedFile.getParentFile().toString();
        }
    }

    @FXML
    private void onEnableStaffTeamCheckBox() {
        if (enableStaffTeamCheckBox.isSelected()) {
            enableStaffTeam();
            updateTextFieldFromCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
            updateTextFieldFromCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
        } else {
            disableStaffTeam();
        }
    }

    @FXML
    private void onFreePriceCheckBox() {
        updateTextFieldFromCheckBox(freePriceCheckBox, priceTextField);
    }

    @FXML
    private void  onNoLimitMaxParticipantsCheckBox() {
        updateTextFieldFromCheckBox(noLimitMaxParticipantsCheckBox, maxParticipantsTextField);
    }

    @FXML
    private void onNoLimitMaxTeamsCheckBox() {
        updateTextFieldFromCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
    }


    private ArrayList<Date> checkValidDateToCreate() {
        ArrayList<Date> dateArrayList = DateTimeCheck.createDate(startDateDatePicker,
                                                                 endDateDatePicker,
                                                                 errorEventStartDateLabel,
                                                                 errorEventEndDateLabel,
                                                                 startTimeTextField,
                                                                 endTimeTextField,
                                                                 errorEventStartTimeLabel,
                                                                 errorEventEndTimeLabel);

        if (dateArrayList != null) {
            boolean isStartTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(0), errorEventStartDateLabel, errorEventStartTimeLabel);
            boolean isEndTimeInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEventEndDateLabel, errorEventEndTimeLabel);
            if (!isStartTimeInThePast && !isEndTimeInThePast) {
                return dateArrayList;
            }
        }
        return null;
    }

    private ArrayList<Date> checkValidParticipantRegistrationDateToCreate(ArrayList<Date> eventDate) {
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

                boolean isInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(0), errorStartDateParticipantLabel, errorStartTimeParticipantLabel) || DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEndDateParticipantLabel, errorEndTimeParticipantLabel);

                if (checkStartTime && checkEndTime && !isInThePast){
                    return dateArrayList;
                }
        }
        return null;
    }

    private void enableStaffTeam() {
        anyMaxTeamMembersCheckBox.setDisable(false);
        noLimitMaxTeamsCheckBox.setDisable(false);
        updateTextFieldFromCheckBox(anyMaxTeamMembersCheckBox, maxTeamMembersTextField);
        updateTextFieldFromCheckBox(noLimitMaxTeamsCheckBox, maxTeamsTextField);
    }

    private void disableStaffTeam() {
        anyMaxTeamMembersCheckBox.setDisable(true);
        maxTeamMembersTextField.setEditable(false);
        maxTeamsTextField.setEditable(false);
        noLimitMaxTeamsCheckBox.setDisable(true);
    }

    private String checkValidEventNameToCreate() {
        if (nameEventTextField.getText().trim().isEmpty()) {
            errorEventNameLabel.setText("Cannot be empty.");
        } else if (AllCollection.getInstance().getEventList().findEventByName(nameEventTextField.getText().trim()) != null) {
            errorEventNameLabel.setText("Event name already exists.");
        } else {
            return nameEventTextField.getText().trim();
        }
        return null;
    }

    private Integer checkValidIntTextField(TextField textField, Label errorLabel, CheckBox checkBox) {
        Integer parseInt;
        try {
            parseInt = Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException e) {
            parseInt = null;
        }
        if (checkBox.isSelected()) {
            parseInt = -1;
            return parseInt;
        } else if (textField.getText().trim().equals("")) {
            errorLabel.setText("Cannot be empty.");
        } else if (parseInt == null){
            errorLabel.setText("Please enter a valid number.");
        } else if (parseInt <= 0) {
            errorLabel.setText("Cannot be minus or zero.");
        } else {
            return parseInt;
        }
        return null;
    }

    private Integer checkValidMaxParticipantsToCreate() {
        return checkValidIntTextField(maxParticipantsTextField,
                errorAmountMaxParticipantsLabel,
                noLimitMaxParticipantsCheckBox);
    }

    private Integer checkValidMaxTeamMembersToCreate() {
        return checkValidIntTextField(maxTeamMembersTextField,
                errorMaxTeamMembersLabel,
                anyMaxTeamMembersCheckBox);
    }

    private Integer checkValidMaxTeamsToCreate() {
        return checkValidIntTextField(maxTeamsTextField,
                errorMaxTeamsLabel,
                noLimitMaxTeamsCheckBox);
    }

    private String checkValidPlaceToCreate() {
        if (placeTextField.getText().trim().equals("")) {
            errorPlaceLabel.setText("Cannot be empty.");
        } else {
            return placeTextField.getText();
        }
        return null;
    }

    private Double checkValidPriceToCreate() {
        Double parseDouble;
        try {
            parseDouble = Double.parseDouble(priceTextField.getText().trim());
        } catch (NumberFormatException e) {
            parseDouble = null;
        }
        if (freePriceCheckBox.isSelected()) {
            parseDouble = 0.0;
            return parseDouble;
        } else if (priceTextField.getText().trim().equals("")) {
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

    private void clearErrorLabels() {
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
        errorEndTimeParticipantLabel.setText("");
        errorEndDateParticipantLabel.setText("");
        errorStartDateParticipantLabel.setText("");
        errorStartTimeParticipantLabel.setText("");
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
        categoryComboBox.getSelectionModel().selectFirst();
    }

    private void resetPage() {
        clearErrorLabels();
        nameEventTextField.setText("");
        placeTextField.setText("");
        maxTeamsTextField.setText("");
        priceTextField.setText("");
        maxParticipantsTextField.setText("");
        maxTeamMembersTextField.setText("");
        startTimeTextField.setText("");
        endTimeTextField.setText("");
        startDateDatePicker.setValue(null);
        endDateDatePicker.setValue(null);
        selectedFile = null;
        initializeCategoryComboBox();
        descriptionTextArea.setText("");
        eventImageImageView.setImage(DEFAULT_IMAGE);
        AdjustImageView.setViewPortImageView(eventImageImageView, DEFAULT_IMAGE);
        enableStaffTeamCheckBox.setSelected(false);
        noLimitMaxTeamsCheckBox.setSelected(false);
        anyMaxTeamMembersCheckBox.setSelected(false);
        freePriceCheckBox.setSelected(false);
        noLimitMaxParticipantsCheckBox.setSelected(false);
        participatedRegistrationEndDateDatePicker.setValue(null);
        participatedRegistrationStartDateDatePicker.setValue(null);
        participatedRegistrationEndTimeTextField.setText("");
        participatedRegistrationStartTimeTextField.setText("");
    }

    private void setDatePicker() {
        endDateDatePicker.setEditable(false);
        startDateDatePicker.setEditable(false);
        DateTimeCheck.disableDateBeforeNow(startDateDatePicker);
        DateTimeCheck.disableDateBeforeNow(endDateDatePicker);
        participatedRegistrationStartDateDatePicker.setEditable(false);
        participatedRegistrationEndDateDatePicker.setEditable(false);
        DateTimeCheck.disableDateBeforeNow(participatedRegistrationStartDateDatePicker);
        DateTimeCheck.disableDateBeforeNow(participatedRegistrationEndDateDatePicker);
    }

    private void setupPage() {
        setDatePicker();
        disableStaffTeam();
        clearErrorLabels();
        initializeCategoryComboBox();
        eventImageImageView.setImage(DEFAULT_IMAGE);
        AdjustImageView.setViewPortImageView(eventImageImageView, DEFAULT_IMAGE);
        AdjustImageView.radiusImageView(eventImageImageView, 55);
        Loader.loadNavBar(navBarSpaceVBox);
    }

    private void updateTextFieldFromCheckBox(CheckBox checkBox, TextField textField) {
        if (checkBox.isSelected()) {
            textField.setEditable(false);
            textField.setText(checkBox.getText());
        } else {
            textField.setText("");
            textField.setEditable(true);
        }
    }
}
