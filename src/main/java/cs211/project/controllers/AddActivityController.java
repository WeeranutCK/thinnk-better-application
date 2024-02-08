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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class AddActivityController {
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

    private Event currentEvent;
    private FilteredList<StaffTeam> filteredList = null;
    private StaffTeam selectedTeam = null;
    private ObservableList<StaffTeam> teamTableDataList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        currentEvent = (Event) FXRouter.getData();
        setupPage();
    }


    @FXML
    private void onAddButtonClicked() {
        clearErrorLabels();
        ArrayList<Date> dateToCreate = checkValidDateToCreate();
        String activityNameToCreate = checkValidActivityName();
        StaffTeam staffTeamToCreate = selectedTeam;

        if(dateToCreate != null && activityNameToCreate != null) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Adding the Activity",
                                        "Are you sure you want to add a new activity?",
                                                activityNameToCreate);

            if (confirmDialogController.getIsConfirm()) {
                AllCollection.getInstance().getScheduleList().
                                createNewSchedule(
                                    activityNameToCreate,
                                    currentEvent,
                                    descriptionTextArea.getText(),
                                    dateToCreate.get(0),
                                    dateToCreate.get(1),
                                    staffTeamToCreate);

                AllCollection.getInstance().writeStaffTeamData();
                AllCollection.getInstance().writeEventData();
                AllCollection.getInstance().writeScheduleData();
                clearErrorLabels();
                clearInputFields();
                selectedTeam = null;
                setTeamTableDataList();
                setTeamSelectionColumn(selectedTeam != null);
            }
        }
    }

    @FXML
    private void onBackButtonClicked() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack(), currentEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String checkValidActivityName() {
        if (nameActivityTextField.getText().trim().isEmpty()) {
            errorNameLabel.setText("Cannot be empty.");
        } else if (currentEvent.getActivityList().findActivityByName(nameActivityTextField.getText().trim()) != null) {
            errorNameLabel.setText("Activity name already exists.");
        } else {
            return nameActivityTextField.getText().trim();
        }
        return null;
    }

    private ArrayList<Date> checkValidDateToCreate() {
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
            if (isStartTimeInThePast || isEndTimeInThePast);
            else if (currentEvent.getStartTime().after(dateArrayList.get(0)) || currentEvent.getEndTime().before(dateArrayList.get(1))) {
                errorStartDateLabel.setText("Out of event timeframe.");
                errorEndDateLabel.setText("Out of event timeframe.");
                errorStartTimeLabel.setText("Out of event timeframe.");
                errorEndTimeLabel.setText("Out of event timeframe.");

            } else {
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

    private void clearInputFields() {
        descriptionTextArea.clear();
        endTimeTextField.clear();
        startTimeTextField.clear();
        nameActivityTextField.clear();
        startDateDatePicker.setValue(null);
        endDateDatePicker.setValue(null);
    }

    private void searchAndUpdateTableView() {
        searchBarTextField.textProperty().addListener((observable, oldValue, newValue) ->{
            filteredList.setPredicate((Predicate<? super StaffTeam>) staffTeam -> {
                String searchString = newValue.toLowerCase();
                String activity = "Not Assigned".toLowerCase();
                if (staffTeam.getSchedule() != null) {
                    activity = AllCollection.getInstance().getScheduleList().findActivityById(staffTeam.getSchedule().getActivityId()).getActivityName().toLowerCase();
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

    private void setupPage() {
        setDatePicker();
        clearErrorLabels();
        setupTableView();
        Loader.loadNavBar(navBarVBox);
        nameTeamLabel.setText("Not Selected");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        eventStartTimeLabel.setText(simpleDateFormat.format(currentEvent.getStartTime()));
        eventEndTimeLabel.setText(simpleDateFormat.format(currentEvent.getEndTime()));
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

    private void setTeamSelectionColumn(boolean isSelected) {
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
                        if (currentStaffTeam.getSchedule() == null) {
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
                            setTeamSelectionColumn(!isSelected);
                        } else {
                            selectedTeam = null;
                            nameTeamLabel.setText("Not Selected");
                            setTeamSelectionColumn(!isSelected);
                        }
                    });
                }
            }
        });
    }

    private void setupTableView() {
        searchAndUpdateTableView();
        setTeamTableDataList();
        filteredList = new FilteredList<>(teamTableDataList, p -> true);
        SortedList<StaffTeam> sortedData = new SortedList<>(filteredList);
        teamTableView.setItems(sortedData);
        setColumn();
        sortTable();
        sortedData.comparatorProperty().bind(teamTableView.comparatorProperty());
        setTeamSelectionColumn(selectedTeam != null);
    }

    private void setTeamTableDataList() {
        teamTableDataList.clear();
        for (StaffTeam staffTeam : currentEvent.getStaffTeamList().getStaffTeamArrayList()) {
            teamTableDataList.add(staffTeam);
        }
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
