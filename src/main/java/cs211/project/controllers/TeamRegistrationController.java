package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.StaffTeam;
import cs211.project.services.*;
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

public class TeamRegistrationController {
    @FXML
    private CheckBox autoAcceptingCheckBox;
    @FXML
    private CheckBox multiTeamAllowedCheckBox;
    @FXML
    private Label amountTeamLabel;
    @FXML
    private TableColumn<StaffTeam, String> closeDateColumn;
    @FXML
    private Button createButton;
    @FXML
    private VBox createNewTeamVBox;
    @FXML
    private DatePicker endDateDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Label errorCreateNewTeamLabel;
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
    private TableColumn<StaffTeam, String> actionColumn;
    @FXML
    private Label maxParticipantsLabel;
    @FXML
    private TextField maxParticipantsTextField;
    @FXML
    private TableColumn<StaffTeam, String> membersColumn;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TableColumn<StaffTeam, String> openDateColumn;
    @FXML
    private TableColumn<StaffTeam, String> detailColumn;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private Label statusLabel;
    @FXML
    private TableColumn<Event, String> teamNameColumn;
    @FXML
    private TextField teamNameTextField;
    @FXML
    private TableView<StaffTeam> teamTableView;
    @FXML
    private VBox vboxScrollPane;

    private Event currentEvent;
    private FilteredList<StaffTeam> filteredList = null;
    private boolean isHost;
    private boolean isAdmin;
    private boolean isStaff;
    private SortedList<StaffTeam> sortedData;
    private ObservableList<StaffTeam> teamTableDataList;
    private String color;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        Object data = FXRouter.getData();
        if(data instanceof Event) {
            currentEvent = (Event) data;
        } else {
            currentEvent = ((StaffTeam) data).getEvent();
        }
        teamTableDataList = FXCollections.observableArrayList();
        FilteredList<StaffTeam> filteredData = new FilteredList<>(teamTableDataList, p -> true);
        sortedData = new SortedList<>(filteredData);

        setupPage();

        teamTableView.setOnMouseClicked(event -> {
            ObservableList<TablePosition> selectedCells = teamTableView.getSelectionModel().getSelectedCells();
            if (!selectedCells.isEmpty()) {
                TablePosition<?, ?> selectedCell = (TablePosition<?, ?>) selectedCells.get(0);
                int rowIndex = selectedCell.getRow();
                int columnIndex = selectedCell.getColumn();

                if (rowIndex >= 0 && rowIndex < teamTableView.getItems().size() && columnIndex >= 0 && columnIndex < teamTableView.getColumns().size()) {
                    TableColumn<?, ?> selectedColumn = teamTableView.getColumns().get(columnIndex);
                    Object selectedItem = teamTableView.getItems().get(rowIndex);
                    String cellData = teamTableView.getColumns().get(columnIndex).getCellData(rowIndex).toString();
                    if (selectedItem instanceof StaffTeam selectedObject) {
                        if (cellData.equalsIgnoreCase("Join")) {
                            joinTeam(selectedObject);
                        } else if (cellData.equalsIgnoreCase("Regis")) {
                            regisTeam(selectedObject);
                        } else if (cellData.equalsIgnoreCase("Detail")) {
                            PageHistory.getInstance().pushStack("team-registration");
                            try {
                                FXRouter.goTo("team-detail", selectedItem);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });

        searchAndUpdateTableView();
    }

    @FXML
    private void onBackButtonClick() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack(), currentEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onCreateButtonClick() {
        clearErrorLabels();

        boolean isDateAvailable = false;
        ArrayList<Date> dateToCreateArrayList = checkValidDateToCreate();
        if (checkValidDateToCreate() != null) {
            isDateAvailable = true;
        }

        Integer maxParticipants;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsTextField.getText());
        } catch (RuntimeException e) {
            maxParticipants = null;
        }

        boolean isTeamNameAvailable = false;

        if (teamNameTextField.getText().trim().isEmpty()) {
            errorTeamNameLabel.setText("Team name cannot be empty.");
        } else if (isTeamNameExists(teamNameTextField.getText().trim(), currentEvent)) {
            errorTeamNameLabel.setText("Team name already exists.");
        } else {
            isTeamNameAvailable = true;
        }

        boolean isMaxParticipantsAvailable = false;
        if (maxParticipantsTextField.getText().isEmpty()) {
            errorMaxParticipantsLabel.setText("Max participants cannot be empty.");
        } else if (maxParticipants == null) {
            errorMaxParticipantsLabel.setText("Max participants must be a valid number.");
        } else if (maxParticipants <= 0) {
            errorMaxParticipantsLabel.setText("Max participants must be greater than zero.");
        } else if (!currentEvent.isAnyMaxTeamMembers() && maxParticipants > currentEvent.getMaxTeamMembers()) {
            errorMaxParticipantsLabel.setText("The number of participants has exceeded the limit.");
        } else {
            isMaxParticipantsAvailable = true;
        }

        if (isTeamNameAvailable && isMaxParticipantsAvailable && isDateAvailable) {
            createNewTeam(maxParticipants, dateToCreateArrayList);
        }
    }


    private void checkRole() {
        isHost = AllCollection.getInstance().getCurrentUser().equals(currentEvent.getHostUser());
        isAdmin = AllCollection.getInstance().getCurrentUser().isAdmin();
        isStaff = AllCollection.getInstance().getCurrentUser().isStaff(currentEvent);
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
            boolean checkStartTime = false;
            boolean checkEndTime = false;
            if (dateArrayList.get(0).after(currentEvent.getEndTime())){
                errorStartDateLabel.setText("Cannot start after the event ends.");
                errorStartTimeLabel.setText("Cannot start after the event ends.");
            } else {
                checkStartTime = true;
            }

            if (dateArrayList.get(1).after(currentEvent.getEndTime())) {
                errorEndDateLabel.setText("Cannot end after the event ends.");
                errorEndTimeLabel.setText("Cannot end after the event ends.");
            } else {
                checkEndTime = true;
            }

            boolean isInThePast = DateTimeCheck.isTimeInThePast(dateArrayList.get(0), errorStartDateLabel, errorStartTimeLabel) || DateTimeCheck.isTimeInThePast(dateArrayList.get(1), errorEndDateLabel, errorEndTimeLabel);

            if (checkStartTime && checkEndTime && !isInThePast){
                return dateArrayList;
            }
        }

        return null;
    }

    private void clearErrorLabels() {
        errorMaxParticipantsLabel.setText("");
        errorTeamNameLabel.setText("");
        errorCreateNewTeamLabel.setText("");
        errorStartDateLabel.setText("");
        errorEndDateLabel.setText("");
        errorStartTimeLabel.setText("");
        errorEndTimeLabel.setText("");
    }

    private void clearInputFields() {
        teamNameTextField.clear();
        maxParticipantsTextField.clear();
        endDateDatePicker.setValue(null);
        startDateDatePicker.setValue(null);
        endTimeTextField.clear();
        startTimeTextField.clear();
    }

    private void createNewTeam(int maxParticipants, ArrayList<Date> dateToCreateArrayList) {
        ConfirmDialogController confirmDialogController =
                Loader.loadDialog("Confirm Creating New Team",
                                    "Are you sure to continue creating a new team?",
                                            teamNameTextField.getText());

        if (confirmDialogController.getIsConfirm()) {
            AllCollection.getInstance().getStaffTeamList().createNewStaffTeam(teamNameTextField.getText(), maxParticipants, currentEvent, dateToCreateArrayList.get(0), dateToCreateArrayList.get(1), autoAcceptingCheckBox.isSelected(), multiTeamAllowedCheckBox.isSelected());
            AllCollection.getInstance().writeEventData();
            AllCollection.getInstance().writeStaffTeamData();
            clearInputFields();
            setTeamTableDataList();
            showCreateTeam();
        }
    }

    private boolean isTeamNameExists(String teamName, Event event) {
        if (!teamName.isEmpty() && event.getStaffTeamList().findStaffTeamByName(teamName) != null) {
            return true;
        }
        return false;
    }

    private void joinTeam(StaffTeam staffTeam) {
        ConfirmDialogController confirmDialogController =
                Loader.loadDialog("Confirm Joining Team",
                                    "Are you sure you want to join this team?",
                                            staffTeam.getTeamName());

        if (confirmDialogController.getIsConfirm()) {
            if (staffTeam.getIsMultiTeamAllowed()) {
                for (StaffTeam temp : currentEvent.getStaffTeamList().getStaffTeamArrayList()) {
                    if (!temp.getIsMultiTeamAllowed()) {
                        temp.getRegistrants().getUserArrayList().remove(AllCollection.getInstance().getCurrentUser());
                    }
                }
            } else {
                for (StaffTeam temp : currentEvent.getStaffTeamList().getStaffTeamArrayList()) {
                    temp.getRegistrants().getUserArrayList().remove(AllCollection.getInstance().getCurrentUser());
                }
            }

            AllCollection.getInstance().getStaffList().createNewStaffJoinTeam(currentEvent, staffTeam, AllCollection.getInstance().getCurrentUser());
            AllCollection.getInstance().writeUser();
            AllCollection.getInstance().writeStaffTeamData();
            AllCollection.getInstance().writeStaffData();
            checkRole();
            setStatus();
            updateActionColumn();
        }
    }

    private void regisTeam(StaffTeam staffTeam) {
        ConfirmDialogController confirmDialogController =
                Loader.loadDialog("Confirm Team Registration",
                        "Are you sure you want to regis this team?",
                        staffTeam.getTeamName());

        if (confirmDialogController.getIsConfirm()) {
            staffTeam.regisTeam(AllCollection.getInstance().getCurrentUser());
            AllCollection.getInstance().writeStaffTeamData();
            updateActionColumn();
        }
    }

    private void searchAndUpdateTableView() {
        searchBarTextField.textProperty().addListener((observable, oldValue, newValue) ->{
            filteredList.setPredicate((Predicate<? super StaffTeam>) staffTeam -> {
                String searchString = newValue.toLowerCase();

                String teamName = staffTeam.getTeamName().toLowerCase();

                String members = staffTeam.getAmountTeamMember() + "/" + staffTeam.getMaxMembers();
                members = members.toLowerCase();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("en"));
                String openDate = simpleDateFormat.format(staffTeam.getStartTimeRegistration()).toLowerCase();
                String closeDate = simpleDateFormat.format(staffTeam.getEndTimeRegistration()).toLowerCase();


                String join = "";

                if (isStaff) {
                    if (staffTeam.isStaffInTeam(AllCollection.getInstance().getCurrentUser().getUserId()) != null) {
                        join = staffTeam.statusRegistration();
                    } else {
                        join = "Cannot Join";
                    }
                } else if (staffTeam.statusRegistration().equals("Available") && !isHost && !isAdmin) {
                    join = "Join";
                } else {
                    join = staffTeam.statusRegistration();
                }

                join = join.toLowerCase();

                if (teamName.contains(searchString)) {
                    return true;
                } else if (join.toLowerCase().contains(searchString)) {
                    return true;
                } else if (openDate.toLowerCase().contains(searchString)) {
                    return true;
                } else if (closeDate.toLowerCase().contains(searchString)) {
                    return true;
                } else if (members.toLowerCase().contains(searchString)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void setAmountTeam() {
        if(currentEvent.isNoLitMaxStaffTeams()) {
            amountTeamLabel.setText("(No Limit)");
        } else {
            amountTeamLabel.setText("(" + currentEvent.getStaffTeamList().getStaffTeamArrayList().size() + "/" + currentEvent.getMaxStaffTeams() + ")");
        }
    }

    private void setStatus() {

        if (isAdmin || isHost || isStaff) {
            if (isAdmin && isHost) {
                statusLabel.setText("Admin and Host");
            } else if (isAdmin) {
                statusLabel.setText("Admin");
            } else if (isHost) {
                statusLabel.setText("Host");
            } else {
                statusLabel.setText("Staff");
            }
        } else {
            statusLabel.setText("Visitor");
        }
    }

    private void setupTableView() {
        searchAndUpdateTableView();
        teamTableDataList = FXCollections.observableArrayList(currentEvent.getStaffTeamList().getStaffTeamArrayList());
        filteredList = new FilteredList<>(teamTableDataList, p -> true);
        sortedData = new SortedList<>(filteredList);
        teamTableView.setItems(sortedData);
        setColumn();
        sortTable();
        sortedData.comparatorProperty().bind(teamTableView.comparatorProperty());
    }

    private void setColumn() {
        teamTableView.getColumns().forEach(column -> column.setReorderable(false));

        detailColumn.setCellValueFactory(param -> new SimpleStringProperty("Detail"));
        detailColumn.setSortable(false);
        detailColumn.setCellFactory(column -> new TableCell<StaffTeam, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    setCursor(Cursor.HAND);
                    setUnderline(true);
                    setStyle("-fx-text-fill: " + color + ";");
                    setOnMouseEntered(event -> {
                        setCursor(Cursor.HAND);
                    });
                }
            }
        });

        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));

        membersColumn.setCellValueFactory(param -> {
            int maxMembers = param.getValue().getMaxMembers();
            int participant = param.getValue().getAmountTeamMember();
            String result = participant + "/" + maxMembers;
            return new SimpleStringProperty(result);
        });

        openDateColumn.setCellValueFactory(param -> {
            Locale englishLocale = new Locale("en");
            String result = new SimpleDateFormat("dd MMM yyyy HH:mm", englishLocale).format(param.getValue().getStartTimeRegistration());
            return new SimpleStringProperty(result);
        });

        closeDateColumn.setCellValueFactory(param -> {
            Locale englishLocale = new Locale("en");
            String result = new SimpleDateFormat("dd MMM yyyy HH:mm", englishLocale).format(param.getValue().getEndTimeRegistration());
            return new SimpleStringProperty(result);
        });

        actionColumn.setCellValueFactory(param -> {
            StaffTeam currentStaffTeam = param.getValue();

            if (isHost || isAdmin) {
                actionColumn.setText("Team Detail");
                return new SimpleStringProperty(currentStaffTeam.statusRegistration());
            }

            if (isStaff && !currentEvent.getStaffTeamList().findTeamsOfUser(
                    AllCollection.getInstance().getCurrentUser()).getStaffTeamArrayList().get(0).getIsMultiTeamAllowed()) {
                if (currentStaffTeam.isStaffInTeam(AllCollection.getInstance().getCurrentUser().getUserId()) != null) {
                    return new SimpleStringProperty("Participated");
                } else {
                    return new SimpleStringProperty("Cannot Join");
                }
            }

            if (currentStaffTeam.getStaffList().findStaffInTeamByUserId(AllCollection.getInstance().getCurrentUser().getUserId()) != null) {
                return new SimpleStringProperty("Participated");
            }

            if (currentStaffTeam.getRegistrants().getUserArrayList().contains(AllCollection.getInstance().getCurrentUser())) {
                return new SimpleStringProperty("Under Review");
            }

            if (!isHost && !isAdmin && !currentStaffTeam.getIsMultiTeamAllowed() && isStaff) {
                return new SimpleStringProperty("No Multi Team");
            }

            if (currentStaffTeam.statusRegistration().equals("Available") && !isHost && !isAdmin) {
                if (!currentStaffTeam.getIsTeamAutoAccepting()) {
                    return new SimpleStringProperty("Regis");
                }
                return new SimpleStringProperty("Join");
            }

            return new SimpleStringProperty(currentStaffTeam.statusRegistration());
        });

        updateActionColumn();
    }

    private void setTeamTableDataList() {
        teamTableDataList.clear();
        for (StaffTeam staffTeam : currentEvent.getStaffTeamList().getStaffTeamArrayList()) {
            teamTableDataList.add(staffTeam);
        }
        checkRole();
        setStatus();
        setupTableView();
        teamTableView.setItems(sortedData);
    }

    private void setupPage() {
        String currentTheme = AllCollection.getInstance().getCurrentUser().getTheme().getCurrentTheme();
        color = currentTheme.equalsIgnoreCase("Candy Pink")? "#F254A0" : "#A076F9";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        eventStartTimeLabel.setText(simpleDateFormat.format(currentEvent.getStartTime()));
        eventEndTimeLabel.setText(simpleDateFormat.format(currentEvent.getEndTime()));
        checkRole();
        clearErrorLabels();
        clearInputFields();
        showCreateTeam();
        Loader.loadNavBar(navBarVBox);
        setTeamTableDataList();
        setStatus();
        eventNameLabel.setText(currentEvent.getEventName());
        setDatePicker();
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

    private void showCreateTeam() {
        boolean isAdmin = AllCollection.getInstance().getCurrentUser().isAdmin();
        boolean isHost = AllCollection.getInstance().getCurrentUser().equals(currentEvent.getHostUser());

        if (!isAdmin && !isHost) {
            vboxScrollPane.getChildren().remove(createNewTeamVBox);
            vboxScrollPane.setPrefHeight(768);
            scrollPane.fitToHeightProperty();
            return;
        }
        if (!currentEvent.isEnableStaffRegistration() || currentEvent.isStaffTeamLimitReached()) {
            if (!currentEvent.isEnableStaffRegistration()) {
                errorCreateNewTeamLabel.setText("Team registration settings not configured. Please set it first.");
            } else if (currentEvent.isStaffTeamLimitReached()) {
                errorCreateNewTeamLabel.setText("The maximum number of teams has been reached.");
            }
            teamNameTextField.setDisable(true);
            maxParticipantsTextField.setDisable(true);
            createButton.setDisable(true);
            endTimeTextField.setDisable(true);
            startTimeTextField.setDisable(true);
            endDateDatePicker.setDisable(true);
            startDateDatePicker.setDisable(true);
            autoAcceptingCheckBox.setDisable(true);
            multiTeamAllowedCheckBox.setDisable(true);
        }

        if (currentEvent.isAnyMaxTeamMembers()) {
            maxParticipantsLabel.setText("(Max Participant is any.)");
        } else {
            maxParticipantsLabel.setText("(Max Participant is " + currentEvent.getMaxTeamMembers() + ".)");
        }
        setAmountTeam();
    }

    private void sortTable() {
        membersColumn.setComparator((team1, team2) -> {
            String[] stringArraysTeam1 = team1.split("/");
            Integer[] intArraysTeam1 = {Integer.parseInt(stringArraysTeam1[0]), Integer.parseInt(stringArraysTeam1[1])};
            String[] stringArraysTeam2 = team2.split("/");
            Integer[] intArraysTeam2 = {Integer.parseInt(stringArraysTeam2[0]), Integer.parseInt(stringArraysTeam2[1])};
            return Integer.compare(intArraysTeam2[1] - intArraysTeam2[0], intArraysTeam1[1] - intArraysTeam1[0]);
        });
    }

    private void updateActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<StaffTeam, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                StaffTeam currentStaffTeam = null;
                if (getIndex() >= 0 && getIndex() < teamTableView.getItems().size()){
                    currentStaffTeam = getTableView().getItems().get(getIndex());
                }

                if (empty || item == null || currentStaffTeam == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("Join") || item.equalsIgnoreCase("Team Detail") || item.equalsIgnoreCase("Regis")) {
                        setUnderline(true);
                        setStyle("-fx-text-fill: " + color + ";");
                        setOnMouseEntered(event -> {
                            setCursor(Cursor.HAND);
                        });
                    } else if (item.equalsIgnoreCase("Participated")) {
                        setStyle("-fx-text-fill: " + color + ";");
                    } else {
                        setStyle("-fx-text-fill: #F0F0F0");
                    }
                }
            }
        });
    }
}
