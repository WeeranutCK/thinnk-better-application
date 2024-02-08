package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Schedule;
import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.services.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class EditActivityController {
    @FXML
    private TableColumn<StaffTeam, String> createDateColumn;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private DatePicker endDateDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Label errorEndDateLabel;
    @FXML
    private Label errorEndTimeLabel;
    @FXML
    private Label errorNameLabel;
    @FXML
    private Label errorStartDateLabel;
    @FXML
    private Label errorStartTimeLabel;
    @FXML
    private Label eventEndTimeLabel;
    @FXML
    private Label eventStartTimeLabel;
    @FXML
    private TableColumn<StaffTeam, String> headTeamColumn;
    @FXML
    private TextField nameActivityTextField;
    @FXML
    private Label nameTeamLabel;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TableColumn<StaffTeam, String> responsibleActivityColumn;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private TableColumn<StaffTeam, Boolean> selectColumn;
    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TableColumn<StaffTeam, String> teamMemberColumn;
    @FXML
    private TableColumn<StaffTeam, String> teamNameColumn;
    @FXML
    private TableView<StaffTeam> teamTableView;

    private Schedule currentActivity;
    private Event currentEvent;
    private FilteredList<StaffTeam> filteredList = null;
    private StaffTeam selectedTeam = null;
    private ObservableList<StaffTeam> teamTableDataList;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        currentActivity = (Schedule) FXRouter.getData();
        currentEvent = AllCollection.getInstance().getEventList().findEventById(currentActivity.getEvent().getEventId());
        setupPage();
    }


    @FXML
    private void onBackButtonClicked() {
        boolean isOldData = isOldData();
        if (!isOldData) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm",
                            "Are you certain you want to discard your edits?",
                            currentActivity.getActivityName());
            if (confirmDialogController.getIsConfirm()) {
                try {
                    FXRouter.goTo(PageHistory.getInstance().popStack(), currentActivity.getEvent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                FXRouter.goTo(PageHistory.getInstance().popStack(), currentActivity.getEvent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onConfirmButtonClicked() {
        clearErrorLabels();
        ArrayList<Date> dateToEdit = checkValidDateToEdit();
        String activityNameToEdit = checkValidActivityNameToEdit();
        StaffTeam staffTeamToEdit = selectedTeam;

        if(dateToEdit != null && activityNameToEdit != null) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Editing the Activity",
                                        "Are you sure you want to edit this activity?",
                                                activityNameToEdit);
            if (confirmDialogController.getIsConfirm()) {
                editActivity(activityNameToEdit,
                             dateToEdit.get(0),
                             dateToEdit.get(1),
                             staffTeamToEdit,
                             descriptionTextArea.getText());
                AllCollection.getInstance().writeStaffTeamData();
                AllCollection.getInstance().writeEventData();
                AllCollection.getInstance().writeScheduleData();
                clearErrorLabels();
                setupField();
                selectedTeam = currentActivity.getStaffTeam();
                setTeamTableDataList();
                setupTeamSelectionColumn(selectedTeam != null);
            }
        }
    }


    private boolean isOldData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        boolean isOldName = currentActivity.getActivityName().equals(nameActivityTextField.getText().trim());
        boolean isOldStartDate = dateTimeFormatter.format(startDateDatePicker.getValue()).equals(simpleDateFormat.format(currentActivity.getStartTime()));
        boolean isOldEndDate = dateTimeFormatter.format(endDateDatePicker.getValue()).equals(simpleDateFormat.format(currentActivity.getEndTime()));
        boolean isOldStartTime = startTimeTextField.getText().trim().equals(simpleTimeFormat.format(currentActivity.getStartTime()));
        boolean isOldEndTime = endTimeTextField.getText().trim().equals(simpleTimeFormat.format(currentActivity.getEndTime()));
        boolean isOldTeam = selectedTeam == currentActivity.getStaffTeam();
        boolean isOldDescription = currentActivity.getDescription().equals(descriptionTextArea.getText());

        if (isOldName && isOldDescription && isOldStartTime && isOldEndTime && isOldTeam && isOldStartDate && isOldEndDate) {
            return true;
        }
        return false;
    }

    private String checkValidActivityNameToEdit() {
        if (nameActivityTextField.getText().trim().isEmpty()) {
            errorNameLabel.setText("Cannot be empty.");
        } else if (!nameActivityTextField.getText().equals(currentActivity.getActivityName().trim()) && currentEvent.getActivityList().findActivityByName(nameActivityTextField.getText().trim()) != null) {
            errorNameLabel.setText("Activity name already exists.");
        } else {
            return nameActivityTextField.getText().trim();
        }
        return null;
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
            if (currentEvent.getStartTime().after(dateArrayList.get(0)) || currentEvent.getEndTime().before(dateArrayList.get(1))) {
                errorStartDateLabel.setText("Out of event timeframe.");
                errorEndDateLabel.setText("Out of event timeframe.");
                errorStartTimeLabel.setText("Out of event timeframe.");
                errorEndTimeLabel.setText("Out of event timeframe.");
            } else if (!currentActivity.getStartTime().equals(dateArrayList.get(0)) && (isStartTimeInThePast || isEndTimeInThePast));
              else if (!currentActivity.getEndTime().equals(dateArrayList.get(1)) && isEndTimeInThePast) {
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
        return null;
    }

    private void clearErrorLabels() {
        errorEndDateLabel.setText("");
        errorEndTimeLabel.setText("");
        errorNameLabel.setText("");
        errorStartDateLabel.setText("");
        errorStartTimeLabel.setText("");
    }

    private void editActivity(String activityName, Date startTime, Date endTime, StaffTeam staffTeam, String description) {
        currentActivity.setActivityName(activityName);
        currentActivity.setStartTime(startTime);
        currentActivity.setEndTime(endTime);

        if (currentActivity.getStaffTeam() != null) {
            currentActivity.getStaffTeam().setSchedule(null);
        }
        currentActivity.setStaffTeam(staffTeam);

        currentActivity.setDescription(description);
        if (staffTeam != null) {
            staffTeam.setSchedule(currentActivity);
        }
    }

    private void searchAndUpdateTableView() {
        searchBarTextField.textProperty().addListener((observable, oldValue, newValue) ->{
            filteredList.setPredicate((Predicate<? super StaffTeam>) staffTeam -> {
                String searchString = newValue.toLowerCase();
                String activity = "Not Assigned".toLowerCase();
                if (staffTeam.getSchedule() != null) {
                    activity = staffTeam.getSchedule().getActivityName();
                }
                String headTeam = "Not Assigned".toLowerCase();
                if (staffTeam.getHeadTeam() != null) {
                    headTeam = staffTeam.getHeadTeam().getUser().getUsername().toLowerCase();
                }
                String members = staffTeam.getAmountTeamMember() + "/" + staffTeam.getMaxMembers();
                String createdDate = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("en")).format(staffTeam.getCreateDate()).toLowerCase();
                String teamName = staffTeam.getTeamName().toLowerCase();

                if (teamName.contains(searchString)) {
                    return true;
                } else if (headTeam.contains(searchString)) {
                    return true;
                } else if (activity.contains(searchString)) {
                    return true;
                } else if (createdDate.contains(searchString)) {
                    return true;
                } else if (members.contains(searchString)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void setDatePicker() {
        endDateDatePicker.setEditable(false);
        startDateDatePicker.setEditable(false);
        startDateDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate localDateEnd = currentEvent.getEndTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
                LocalDate localDateStart = currentEvent.getStartTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
                setDisable(date.isBefore(LocalDate.now()) || date.isAfter(localDateEnd) || date.isBefore(localDateStart));
            }
        });
        endDateDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate localDateEnd = currentEvent.getEndTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
                LocalDate localDateStart = currentEvent.getStartTime().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
                setDisable(date.isBefore(LocalDate.now()) || date.isAfter(localDateEnd) || date.isBefore(localDateStart));
            }
        });
    }


    private void setColumn() {
        teamTableView.getColumns().forEach(column -> column.setReorderable(false));
        selectColumn.setCellValueFactory(param -> {
            if (selectedTeam != null) {
                return new SimpleBooleanProperty(param.getValue() == selectedTeam);
            }
            return new SimpleBooleanProperty(false);
        });
        selectColumn.setEditable(true);


        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));

        headTeamColumn.setCellValueFactory(param -> {
            Staff headTeam = param.getValue().getHeadTeam();
            if (headTeam != null) {
                return new SimpleStringProperty(headTeam.getUser().getUsername());
            }
            return new SimpleStringProperty("Not Assigned");
        });

        responsibleActivityColumn.setCellValueFactory(param -> {
            if (param.getValue().getSchedule() == null) {
                return new SimpleStringProperty("Not Assigned");
            }
            Schedule schedule = param.getValue().getSchedule();

            return new SimpleStringProperty(schedule.getActivityName());
        });

        teamMemberColumn.setCellValueFactory(param -> {
            int maxMembers = param.getValue().getMaxMembers();
            int participant = param.getValue().getAmountTeamMember();
            String result = participant + "/" + maxMembers;
            return new SimpleStringProperty(result);
        });

        createDateColumn.setCellValueFactory(param -> {
            Locale englishLocale = new Locale("en");
            String result = new SimpleDateFormat("dd MMM yyyy HH:mm", englishLocale).format(param.getValue().getCreateDate());
            return new SimpleStringProperty(result);
        });
    }

    private void setTeamTableDataList() {
        teamTableDataList.clear();
        teamTableDataList.addAll(currentEvent.getStaffTeamList().getStaffTeamArrayList());
    }

    private void setupField() {
        nameTeamLabel.setText(selectedTeam == null? "Not Selected":selectedTeam.getTeamName());
        descriptionTextArea.setText(currentActivity.getDescription());
        endTimeTextField.setText(String.format("%02d:%02d", currentActivity.getEndTime().getHours(), currentActivity.getEndTime().getMinutes()));
        startTimeTextField.setText(String.format("%02d:%02d", currentActivity.getStartTime().getHours(), currentActivity.getStartTime().getMinutes()));
        nameActivityTextField.setText(currentActivity.getActivityName());
        LocalDate localDateStart = currentActivity.getStartTime().toInstant().atZone(TimeConversion.getZoneOffset()).toLocalDate();
        LocalDate localDateEnd = currentActivity.getEndTime().toInstant().atZone(TimeConversion.getZoneOffset()).toLocalDate();
        startDateDatePicker.setValue(localDateStart);
        endDateDatePicker.setValue(localDateEnd);
    }

    private void setupPage() {
        selectedTeam = currentActivity.getStaffTeam();
        setDatePicker();
        clearErrorLabels();
        setupField();
        setupTableView();
        Loader.loadNavBar(navBarVBox);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        eventStartTimeLabel.setText(simpleDateFormat.format(currentEvent.getStartTime()));
        eventEndTimeLabel.setText(simpleDateFormat.format(currentEvent.getEndTime()));
    }

    private void setupTableView() {
        searchAndUpdateTableView();
        teamTableDataList = FXCollections.observableArrayList(currentEvent.getStaffTeamList().getStaffTeamArrayList());
        filteredList = new FilteredList<>(teamTableDataList, p -> true);
        SortedList<StaffTeam> sortedData = new SortedList<>(filteredList);
        teamTableView.setItems(sortedData);
        setColumn();
        sortTable();
        sortedData.comparatorProperty().bind(teamTableView.comparatorProperty());
        setupTeamSelectionColumn(selectedTeam != null);
    }

    private void setupTeamSelectionColumn(boolean isSelected) {
        selectColumn.setCellFactory(col -> new TableCell<StaffTeam, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    StaffTeam currentStaffTeam = getTableView().getItems().get(getIndex());
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    setGraphic(checkBox);

                    if (!isSelected) {
                        if (currentStaffTeam.getSchedule() == null || currentStaffTeam.getSchedule() == currentActivity) {
                            checkBox.setDisable(false);
                        } else {
                            checkBox.setDisable(true);
                            setCursor(Cursor.DEFAULT);
                        }
                    } else {
                        if (!checkBox.isSelected()) {
                            checkBox.setDisable(true);
                        } else {
                            checkBox.setDisable(false);
                            setCursor(Cursor.HAND);
                        }
                    }

                    checkBox.setOnAction(event -> {
                        if (checkBox.isSelected()) {
                            selectedTeam = getTableView().getItems().get(getIndex());
                            nameTeamLabel.setText(selectedTeam.getTeamName());
                            setupTeamSelectionColumn(!isSelected);
                        } else {
                            selectedTeam = null;
                            nameTeamLabel.setText("Not Selected");
                            setupTeamSelectionColumn(!isSelected);
                        }
                    });
                }
            }
        });
    }

    private void sortTable() {
        teamMemberColumn.setComparator((team1, team2) -> {
            String[] stringArraysTeam1 = team1.split("/");
            Integer[] intArraysTeam1 = {Integer.parseInt(stringArraysTeam1[0]), Integer.parseInt(stringArraysTeam1[1])};
            String[] stringArraysTeam2 = team2.split("/");
            Integer[] intArraysTeam2 = {Integer.parseInt(stringArraysTeam2[0]), Integer.parseInt(stringArraysTeam2[1])};
            return Integer.compare(intArraysTeam2[1] - intArraysTeam2[0], intArraysTeam1[1] - intArraysTeam1[0]);
        });
    }
}
