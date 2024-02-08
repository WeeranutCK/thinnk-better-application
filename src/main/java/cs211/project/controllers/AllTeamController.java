package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.collections.StaffTeamList;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.Loader;
import cs211.project.services.PageHistory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class AllTeamController {
    @FXML
    private TableView<StaffTeam> allTeamTableView;
    @FXML
    private TableColumn <StaffTeam, String> dateCreatedColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Label eventNameLabel;
    @FXML
    private TableColumn <StaffTeam, Integer> membersColumn;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private TableColumn <StaffTeam, Boolean> selectColumn;
    @FXML
    private TableColumn <StaffTeam, String> teamLeaderColumn;
    @FXML
    private TableColumn <StaffTeam, String> teamNameColumn;
    @FXML
    private Label totalTeamLabel;
    @FXML
    private TableColumn <StaffTeam, String> viewColumn;

    private Event event;
    private StaffTeamList staffTeamList;
    private StaffTeam staffTeam;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();
        Object data = FXRouter.getData();

        if (data instanceof Event) {
            event = (Event) data;
        } else if (data instanceof StaffTeam) {
            event = ((StaffTeam) data).getEvent();
        }

        staffTeamList = AllCollection.getInstance().getStaffTeamList();

        for (StaffTeam staffTeam1 : event.getStaffTeamList().getStaffTeamArrayList()) {
            staffTeam = staffTeam1;
        }

        allTeamTableView.setEditable(true);
        allTeamTableView.getSelectionModel().setCellSelectionEnabled(true);

        searchBarTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filterTeamByName(newValue);
            }
        });

        showTable();
        setLabel();

        Loader.loadNavBar(navBarVBox);
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
    public void onDeleteButtonClick() {
        deleteButton.setDisable(true);

        ConfirmDialogController controller = Loader.loadDialog("Delete Current Team",
                                                                "Are you sure you want to delete team from this event ",
                                                                event.getEventName());

        ObservableList<StaffTeam> selectedTeam = allTeamTableView.getItems();
        List<StaffTeam> teamToDelete = selectedTeam
                                       .stream()
                                       .filter(StaffTeam::isSelected)
                                       .toList();

        if (!teamToDelete.isEmpty()) {
            if (controller.getIsConfirm()) {
                boolean changesMade = false;
                for (StaffTeam staffTeam : teamToDelete) {
                    if (staffTeam.getAmountTeamMember() != 0) {
                        deleteButton.setDisable(true);
                        changesMade = false;
                        WarningDialogController warningDialog;
                        warningDialog = Loader.loadWarningDialog("Warning",
                                "You can't delete team that has members.");
                    } else {
                        if (StaffTeam.deleteStaffTeam(staffTeamList.getStaffTeamArrayList(), staffTeam)) {
                            changesMade = true;
                            event.removeStaffTeamById(staffTeam.getTeamId());
                            event.getStaffTeamList().getStaffTeamArrayList().remove(teamToDelete);
                            staffTeam.getStaffList().getStaffArrayList().remove(teamToDelete);
                            AllCollection.getInstance().getEventList().getEventArrayList().remove(teamToDelete);
                            allTeamTableView.getItems().removeAll(teamToDelete);
                        }
                    }
                }
                updateDeleteButtonState();
                totalTeamLabel.setText(String.valueOf(event.getStaffTeamList().getStaffTeamArrayList().size()));
                if (changesMade) {
                    AllCollection.getInstance().writeAllData();
                }
            }
        }
        deleteButton.setDisable(false);
    }


    private void filterTeamByName(String filterText) {
        if (staffTeamList != null) {
            ObservableList<StaffTeam> staffTeamListData = FXCollections.observableArrayList();
            for (StaffTeam staffTeam : event.getStaffTeamList().getStaffTeamArrayList()) {
                if (event.getStaffTeamList().getStaffTeamArrayList().contains(staffTeam) &&
                        staffTeam.getTeamName().toLowerCase().contains(filterText.toLowerCase())) {
                    staffTeamListData.add(staffTeam);
                }
            }
            allTeamTableView.setItems(staffTeamListData);
        }
    }

    private void setLabel() {
        eventNameLabel.setText(event.getEventName());
        totalTeamLabel.setText(String.valueOf(event.getStaffTeamList().getStaffTeamArrayList().size()));
    }

    private void showTable() {
        allTeamTableView.getItems().clear();
        if (staffTeam != null) {
            selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
            selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

            teamNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTeamName()));

            viewColumn.setCellFactory(column -> {
                TableCell<StaffTeam, String> cell = new TableCell<>() {
                    private Label label = new Label("View");
                    {
                        label.setUnderline(true);
                        label.getStyleClass().add("secondary-text");
                        setCursor(Cursor.HAND);
                        label.setOnMouseClicked(mouseClick -> {
                            if (mouseClick.getClickCount() == 1) {
                                try {
                                    String staffTeamName = teamNameColumn.getCellData(allTeamTableView.getSelectionModel().getSelectedIndex());
                                    StaffTeam choseStaffTeam = staffTeamList.findStaffTeamByName(staffTeamName);

                                    PageHistory.getInstance().pushStack("all-team");
                                    FXRouter.goTo("team-detail", choseStaffTeam);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == "null") {
                            setGraphic(null);
                        } else {
                            setGraphic(label);
                        }
                    }
                };
                return cell;
            });

            ObservableList<StaffTeam> staffTeamObservableList = FXCollections.observableArrayList(event.getStaffTeamList().getStaffTeamArrayList());

            teamLeaderColumn.setCellValueFactory(cellData -> {
                String teamName = cellData.getValue().getTeamName();
                Staff teamLeader = staffTeamList.findStaffTeamByName(teamName).getHeadTeam();
                if (teamLeader != null) {
                    String teamLeaderName = teamLeader.getUser().getProfileName();
                    return new SimpleStringProperty(teamLeaderName);
                } else {
                    return new SimpleStringProperty("None");
                }
            });

            membersColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getAmountTeamMember()).asObject());
            dateCreatedColumn.setCellValueFactory(param -> new SimpleStringProperty(new SimpleDateFormat("dd/MM/yy").format(param.getValue().getCreateDate())));
            viewColumn.setCellValueFactory(param -> new SimpleStringProperty("view"));

            allTeamTableView.setItems(staffTeamObservableList);
        }
    }

    private void updateDeleteButtonState() {
        ObservableList<StaffTeam> selectedTeam = allTeamTableView.getItems();

        deleteButton.setDisable(selectedTeam.stream().noneMatch(StaffTeam::isSelected));
    }
}
