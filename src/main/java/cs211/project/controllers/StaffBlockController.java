package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.User;
import cs211.project.services.AdjustImageView;
import cs211.project.services.AllCollection;
import cs211.project.services.Loader;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class StaffBlockController {
    @FXML
    private TableColumn<Staff, Integer> ageStaffColumn;
    @FXML
    private TableColumn<Staff, Integer> numberOfTeamsInEventColumn;
    @FXML
    private TableColumn<Staff, Image> profileStaffColumn;
    @FXML
    private TableColumn<Staff, String> profileNameStaffColumn;
    @FXML
    private TableColumn<Staff, Boolean> selectStaffColumn;
    @FXML
    private TableColumn<Staff, String> usernameStaffColumn;
    @FXML
    private TableColumn<Staff, Boolean> selectHeadTeamColumn;
    @FXML
    private TableView<Staff> staffListTableView;
    @FXML
    private Button kickMembersButton;
    @FXML
    private TextField searchBarStaffTextField;
    @FXML
    private Button disbandButton;
    @FXML
    private Label numberOfMembersLabel;

    private StaffTeam currentStaffTeam;
    private Event currentEvent;
    private ArrayList<Staff> selectedStaff = new ArrayList<>();
    private ArrayList<User> selectedRegistrants = new ArrayList<>();
    private SortedList<Staff> sortedData;
    private ObservableList<Staff> staffTableDataList = FXCollections.observableArrayList();
    private FilteredList<Staff> filteredList = null;
    private Object currentController;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();
    }

    @FXML
    private void onKickMembersButtonClicked() {
        ArrayList<Staff> temp = selectedStaff;
        ConfirmDialogController controller = Loader.loadDialog("Kick Selected Members",
                "Are you sure you want to kick selected staff in",
                currentStaffTeam.getTeamName());
        if (controller.getIsConfirm()) {
            currentStaffTeam.kickMembers(temp);
            AllCollection.getInstance().getStaffList().getStaffArrayList().removeAll(temp);
            AllCollection.getInstance().writeStaffData();
            AllCollection.getInstance().writeEventData();
            AllCollection.getInstance().writeUser();
            selectedStaff.clear();
            updateStaffTableDataList();
            if (currentController instanceof TeamDetailController teamDetailController) {
                teamDetailController.setupGeneralInformationBlock();
            }
        }
    }

    @FXML
    private void onDisbandedButtonClicked() {
        ConfirmDialogController controller = Loader.loadDialog("Disband Current Team",
                "Are you sure you want to disband team",
                currentStaffTeam.getTeamName());
        if (controller.getIsConfirm()) {
            if (currentStaffTeam.getStaffList() != null) {
                currentStaffTeam.kickMembers(currentStaffTeam.getStaffList().getStaffArrayList());
                AllCollection.getInstance().getStaffList().getStaffArrayList().removeAll(currentStaffTeam.getStaffList().getStaffArrayList());
            }

            if (currentStaffTeam.getSchedule() != null) {
                currentStaffTeam.getSchedule().setStaffTeam(null);
            }

            AllCollection.getInstance().getChatList().removeChatHistoryOfStaffTeam(currentStaffTeam);
            currentEvent.getStaffTeamList().removeStaffTeam(currentStaffTeam);
            AllCollection.getInstance().getStaffTeamList().removeStaffTeam(currentStaffTeam);
            AllCollection.getInstance().writeAllData();

            if (currentController instanceof MyTeamController) {
                MyTeamController myTeamController = (MyTeamController) currentController;
                myTeamController.onEventComboBoxSelectedMethod();
                if (currentEvent.getStaffTeamList().getStaffTeamArrayList().size() == 0) {
                    myTeamController.setStaffTeam(null);
                } else {
                    myTeamController.setStaffTeam(currentEvent.getStaffTeamList().getStaffTeamArrayList().get(0));
                }
                myTeamController.setupComponent();
                myTeamController.checkRoleComponent();
            } else if (currentController instanceof EditTeamController){
                EditTeamController editTeamController = (EditTeamController) currentController;
                editTeamController.backToPreviousPage();
            } else if (currentController instanceof TeamDetailController) {
                TeamDetailController teamDetailController = (TeamDetailController) currentController;
                teamDetailController.backToPreviousPage();
            }
        }
    }


    public void showStaffBlock() {
        setupStaffBlock();
    }

    public void setDetails(StaffTeam currentStaffTeam) {
        this.currentStaffTeam = currentStaffTeam;
        if (currentStaffTeam != null) {
            currentEvent = currentStaffTeam.getEvent();
        }
    }

    private void setupStaffBlock() {
        setDisableKickMembersButton();
        setWhenNoCurrentTeam();
        if (currentStaffTeam != null) {
            updateStaffTableDataList();
            filteredList = new FilteredList<>(staffTableDataList, p -> true);
            sortedData = new SortedList<>(filteredList);
            setupStaffTableView();
            searchAndUpdateStaffTableView();
            modifyBlockBaseOnRole();
        }
    }

    private void modifyBlockBaseOnRole() {
        User currentUser = AllCollection.getInstance().getCurrentUser();
        if (currentUser.isAdmin() || currentEvent.isHost(currentUser.getUserId())) {
            disbandButton.setVisible(true);
            kickMembersButton.setVisible(true);
        } else if (currentStaffTeam.isHeadTeam(currentUser)) {
            double quotient = selectHeadTeamColumn.getPrefWidth() / 2;
            staffListTableView.getColumns().remove(selectHeadTeamColumn);
            profileNameStaffColumn.setPrefWidth(155 + quotient);
            usernameStaffColumn.setPrefWidth(150 + quotient);
            disbandButton.setVisible(false);
            kickMembersButton.setVisible(true);
        } else {
            double quotient = (selectHeadTeamColumn.getPrefWidth() + selectStaffColumn.getPrefWidth()) / 2;
            staffListTableView.getColumns().remove(selectHeadTeamColumn);
            staffListTableView.getColumns().remove(selectStaffColumn);
            profileNameStaffColumn.setPrefWidth(155 + quotient);
            usernameStaffColumn.setPrefWidth(150 + quotient);
            disbandButton.setVisible(false);
            kickMembersButton.setVisible(false);
        }
    }

    public void updateStaffTableDataList() {
        staffTableDataList.clear();
        staffTableDataList.addAll(currentStaffTeam.getStaffList().getStaffArrayList());
        numberOfMembersLabel.setText("(" + currentStaffTeam.getStaffList().getStaffArrayList().size() + "/" + currentStaffTeam.getMaxMembers() + ")");
        updateHeadTeamSelectionColumn();
    }

    private void setDisableKickMembersButton() {
        if (selectedStaff.size() == 0 || currentStaffTeam == null) {
            kickMembersButton.setDisable(true);
        } else {
            kickMembersButton.setDisable(false);
        }
    }

    private void setWhenNoCurrentTeam() {
        if (currentStaffTeam == null) {
            disbandButton.setDisable(true);
            searchBarStaffTextField.setDisable(true);
            staffListTableView.setDisable(true);
        } else {
            disbandButton.setDisable(false);
            searchBarStaffTextField.setDisable(false);
            staffListTableView.setDisable(false);
        }
    }

    private void setupStaffTableView() {
        staffListTableView.setItems(sortedData);
        setupStaffColumn();
        updateStaffSelectionColumn();
        updateHeadTeamSelectionColumn();
    }

    void setupStaffColumn() {
        staffListTableView.getColumns().forEach(column -> column.setReorderable(false));
        profileStaffColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getUser().getProfileImage()));
        profileStaffColumn.setCellFactory(column -> new TableCell<Staff, Image>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                setGraphic(imageView);
            }

            @Override
            protected void updateItem(Image profileImage, boolean empty) {
                super.updateItem(profileImage, empty);

                if (empty || profileImage == null) {
                    imageView.setImage(null);
                } else {
                    imageView.setImage(profileImage);
                    AdjustImageView.radiusImageView(imageView, imageView.getFitHeight());
                    AdjustImageView.setViewPortImageView(imageView, profileImage);
                }
            }
        });

        selectStaffColumn.setCellValueFactory(param -> {
            if (selectedStaff.contains(param.getValue())) {
                return new SimpleBooleanProperty(true);
            }
            return new SimpleBooleanProperty(false);
        });

        selectHeadTeamColumn.setCellValueFactory(param -> new SimpleBooleanProperty(currentStaffTeam.isHeadTeam(param.getValue())));

        usernameStaffColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUser().getUsername()));
        profileNameStaffColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUser().getProfileName()));
        ageStaffColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getUser().getAge()).asObject());
        numberOfTeamsInEventColumn.setCellValueFactory(param -> new SimpleIntegerProperty(currentEvent.getStaffTeamList().findTeamsOfUser(param.getValue().getUser()).getNumberOfTeams()).asObject());
        sortedData.comparatorProperty().bind(staffListTableView.comparatorProperty());
    }

    private void updateHeadTeamSelectionColumn() {
        selectHeadTeamColumn.setCellFactory(col -> new TableCell<Staff, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    RadioButton radioButton = new RadioButton();
                    setCursor(Cursor.HAND);
                    setGraphic(radioButton);
                    Staff staff = getTableView().getItems().get(getIndex());
                    radioButton.setSelected(item);

                    radioButton.setOnAction(event -> {
                        staff.getStaffTeam().setHeadTeam(radioButton.isSelected() ? staff : null);
                        AllCollection.getInstance().writeStaffTeamData();

                        if (currentController instanceof TeamDetailController teamDetailController) {
                            teamDetailController.setupGeneralInformationBlock();
                        }
                        updateHeadTeamSelectionColumn();
                    });
                }
            }
        });
    }

    private void updateStaffSelectionColumn() {
        selectStaffColumn.setCellFactory(col -> new TableCell<Staff, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Staff staff = getTableView().getItems().get(getIndex());
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    setGraphic(checkBox);

                    checkBox.setOnAction(event -> {
                        if (checkBox.isSelected()) {
                            selectedStaff.add(staff);
                        } else {
                            selectedStaff.remove(staff);
                        }
                        setDisableKickMembersButton();
                    });
                }
            }
        });
    }

    private void searchAndUpdateStaffTableView() {
        searchBarStaffTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(staff -> {
                String searchString = newValue.toLowerCase();
                User user = staff.getUser();
                String username = user.getUsername().toLowerCase();
                String profileName = user.getProfileName().toLowerCase();
                String age = String.valueOf(user.getAge());
                String numberOfTeam = String.valueOf(currentEvent.getStaffTeamList().findTeamsOfUser(user).getNumberOfTeams());

                return username.contains(searchString) || profileName.contains(searchString) || age.contains(searchString) || numberOfTeam.contains(searchString);
            });
        });
    }

    public void setCurrentController(Object currentController) {
        this.currentController = currentController;
    }
}
