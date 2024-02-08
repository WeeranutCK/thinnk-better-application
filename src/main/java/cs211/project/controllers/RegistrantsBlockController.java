package cs211.project.controllers;

import cs211.project.models.Event;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class RegistrantsBlockController {
    @FXML
    private TableColumn<User, Integer> ageRegistrantsColumn;
    @FXML
    private TableColumn<User, Integer> numberOfTeamsInEventColumn;
    @FXML
    private TableColumn<User, String> orderColumn;
    @FXML
    private TableColumn<User, Image> profileRegistrantsColumn;
    @FXML
    private TableColumn<User, String> profileNameRegistrantsColumn;
    @FXML
    private TableColumn<User, Boolean> selectRegistrantsColumn;
    @FXML
    private TableColumn<User, String> usernameRegistrantsColumn;
    @FXML
    private TableView<User> registrantListTableView;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField searchBarRegistrantsTextField;
    @FXML
    private Button acceptButton;
    @FXML
    private Label isFullStatusLabel;
    @FXML
    private Label numberOfRegistrantsLabel;

    private StaffTeam currentStaffTeam;
    private Event currentEvent;
    private ArrayList<User> selectedRegistrants = new ArrayList<>();
    private SortedList<User> sortedData;
    private ObservableList<User> TableDataList = FXCollections.observableArrayList();
    private FilteredList<User> filteredList = null;
    private StaffBlockController relativeStaffBlock;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();
    }

    @FXML
    private void onAcceptButtonClicked() {
        if (selectedRegistrants.size() > 0 && selectedRegistrants.size() > currentStaffTeam.findAvailableTeamSpots() ) {
            Loader.loadWarningDialog("Team Capacity Exceeded",
                                     "Unable to accept additional applicants. The team is already at full capacity.");
        } else if (!currentStaffTeam.getIsMultiTeamAllowed() && isThereStaffInEvent()) {
            Loader.loadWarningDialog("Registrant Acceptance Error",
                    "This team cannot accept all of the registrants you selected because some of them have already joined a team in this event. Please review your selections.");
        } else {
            String moreDetail = selectedRegistrants.size() + " registrant" + (selectedRegistrants.size() > 1? "s" : "");
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Accepting", "Are you sure to accept selected registrants?", moreDetail);
            if (confirmDialogController.getIsConfirm()) {
                for (User user : selectedRegistrants) {
                    joinTeam(user);
                }
                AllCollection.getInstance().writeUser();
                AllCollection.getInstance().writeStaffTeamData();
                AllCollection.getInstance().writeStaffData();
                updateTableDataList();
                if (relativeStaffBlock != null) {
                    relativeStaffBlock.updateStaffTableDataList();
                }
                selectedRegistrants.clear();
                setDisableAcceptButton();
                setDisableDeleteButton();
            }
        }
    }

    @FXML
    private void onDeleteButtonClicked() {
        if (selectedRegistrants.size() > 0) {
            String moreDetail = selectedRegistrants.size() + " registrant" + (selectedRegistrants.size() > 1? "s" : "");
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Deleting", "Are you sure to delete selected registrants?", moreDetail);
            if (confirmDialogController.getIsConfirm()) {
                for (User user : selectedRegistrants) {
                    currentStaffTeam.deleteRegistrant(user);
                }
                selectedRegistrants.clear();
                AllCollection.getInstance().writeStaffTeamData();
                updateTableDataList();
            }
        }
    }

    private void joinTeam(User user) {
        if (currentStaffTeam.getIsMultiTeamAllowed()) {
            for (StaffTeam temp : currentEvent.getStaffTeamList().getStaffTeamArrayList()) {
                if (!temp.getIsMultiTeamAllowed()) {
                    temp.getRegistrants().getUserArrayList().remove(user);
                }
            }
        } else {
            for (StaffTeam temp : currentEvent.getStaffTeamList().getStaffTeamArrayList()) {
                temp.getRegistrants().getUserArrayList().remove(user);
            }
        }
        currentStaffTeam.getRegistrants().getUserArrayList().remove(user);
        AllCollection.getInstance().getStaffList().createNewStaffJoinTeam(currentEvent, currentStaffTeam, user);
    }

    public void showBlock() {
        setupRegistrantsBlock();
    }

    public void setDetails(StaffTeam currentStaffTeam, StaffBlockController relativeStaffBlock) {
        this.currentStaffTeam = currentStaffTeam;
        this.relativeStaffBlock = relativeStaffBlock;
        if (currentStaffTeam != null) {
            currentEvent = currentStaffTeam.getEvent();
        }
    }

    private void setupRegistrantsBlock() {
        modifyBlockBaseOnRole();
        if (!isNoCurrentTeam()) {
            updateTableDataList();
            filteredList = new FilteredList<>(TableDataList, p -> true);
            sortedData = new SortedList<>(filteredList);
            setupTableView();
            searchAndUpdateTableView();
        }
    }

    private void modifyBlockBaseOnRole() {
        User currentUser = AllCollection.getInstance().getCurrentUser();
        if (currentUser.isAdmin() || currentEvent.isHost(currentUser.getUserId()) || currentStaffTeam.isHeadTeam(currentUser)) {
            registrantListTableView.getColumns().remove(orderColumn);
            setDisableDeleteButton();
            setDisableAcceptButton();
            setupSelectRegistrantsColumn();
        } else {
            deleteButton.setVisible(false);
            acceptButton.setVisible(false);
            registrantListTableView.getColumns().remove(selectRegistrantsColumn);
            setupOrderColumn();
        }
    }

    public void updateTableDataList() {
        TableDataList.clear();
        TableDataList.addAll(currentStaffTeam.getRegistrants().getUserArrayList());
        numberOfRegistrantsLabel.setText("(" + currentStaffTeam.getRegistrants().getUserArrayList().size() + ")");
        if (currentStaffTeam.isFull()) {
            isFullStatusLabel.setText("The team is full.");
        } else {
            isFullStatusLabel.setText("The team has " + currentStaffTeam.findAvailableTeamSpots() + " spot" + (currentStaffTeam.findAvailableTeamSpots() > 1? "s" : "") + " left");
        }
    }

    private void setDisableDeleteButton() {
        deleteButton.setVisible(true);
        if (selectedRegistrants.size() == 0 || currentStaffTeam == null) {
            deleteButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
        }
    }

    private void setDisableAcceptButton() {
        acceptButton.setVisible(true);
        if (selectedRegistrants.size() == 0 || currentStaffTeam == null || currentStaffTeam.isFull()) {
            acceptButton.setDisable(true);
        } else {
            acceptButton.setDisable(false);
        }
    }

    private boolean isNoCurrentTeam() {
        if (currentStaffTeam == null) {
            searchBarRegistrantsTextField.setDisable(true);
            registrantListTableView.setDisable(true);
            return true;
        }

        searchBarRegistrantsTextField.setDisable(false);
        registrantListTableView.setDisable(false);
        return false;
    }

    private void setupTableView() {
        registrantListTableView.setItems(sortedData);
        setupColumn();
    }

    void setupColumn() {
        registrantListTableView.getColumns().forEach(column -> column.setReorderable(false));
        profileRegistrantsColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProfileImage()));
        profileRegistrantsColumn.setCellFactory(column -> new TableCell<User, Image>() {
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

        selectRegistrantsColumn.setCellValueFactory(param -> {
            if (selectedRegistrants.contains(param.getValue())) {
                return new SimpleBooleanProperty(true);
            }
            return new SimpleBooleanProperty(false);
        });

        usernameRegistrantsColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUsername()));
        profileNameRegistrantsColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProfileName()));
        ageRegistrantsColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getAge()).asObject());
        numberOfTeamsInEventColumn.setCellValueFactory(param -> new SimpleIntegerProperty(currentEvent.getStaffTeamList().findTeamsOfUser(param.getValue()).getNumberOfTeams()).asObject());
        sortedData.comparatorProperty().bind(registrantListTableView.comparatorProperty());
    }

    private void setupSelectRegistrantsColumn() {
        selectRegistrantsColumn.setCellValueFactory(param -> {
            if (selectedRegistrants.contains(param.getValue())) {
                return new SimpleBooleanProperty(true);
            }
            return new SimpleBooleanProperty(false);
        });
        updateRegistrantSelectionColumn();
    }

    private void updateRegistrantSelectionColumn() {
        selectRegistrantsColumn.setCellFactory(col -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    setGraphic(checkBox);

                    checkBox.setOnAction(event -> {
                        if (checkBox.isSelected()) {
                            selectedRegistrants.add(user);
                        } else {
                            selectedRegistrants.remove(user);
                        }
                        setDisableDeleteButton();
                        setDisableAcceptButton();
                    });
                }
            }
        });
    }

    private void setupOrderColumn() {
        orderColumn.setCellValueFactory(param -> {
            int numberOrder = registrantListTableView.getItems().indexOf(param.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(numberOrder));
        });
    }

    private void searchAndUpdateTableView() {
        searchBarRegistrantsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(user -> {
                String searchString = newValue.toLowerCase();
                String username = user.getUsername().toLowerCase();
                String profileName = user.getProfileName().toLowerCase();
                String age = String.valueOf(user.getAge());
                String numberOfTeam = String.valueOf(currentEvent.getStaffTeamList().findTeamsOfUser(user).getNumberOfTeams());

                return username.contains(searchString) || profileName.contains(searchString) || age.contains(searchString) || numberOfTeam.contains(searchString);
            });
        });
    }

    private boolean isThereStaffInEvent() {
        for (User user : selectedRegistrants) {
            if (user.getStaffParticipatedEvent().getEventArrayList().contains(currentEvent)) {
                return true;
            }
        }
        return false;
    }
}
