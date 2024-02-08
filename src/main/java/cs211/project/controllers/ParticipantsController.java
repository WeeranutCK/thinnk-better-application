package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Participant;
import cs211.project.models.User;
import cs211.project.models.collections.ParticipantList;
import cs211.project.services.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ParticipantsController {
    @FXML
    private Button deleteButton;
    @FXML
    private Label nameEventLabel;
    @FXML
    private Label participantsNumLabel;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TableView<Participant> tableView;
    @FXML
    private TableColumn<Participant, Boolean> selectColumn;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private TableColumn<Participant, Image> profileColumn;
    @FXML
    private TableColumn<Participant, String> nameColumn;
    @FXML
    private TableColumn<Participant, String> usernameColumn;
    @FXML
    private TableColumn<Participant, Integer> ageColumn;
    @FXML
    private TableColumn<Participant, String> joinDateColumn;
    @FXML
    private TableColumn<Participant, String> joinAsColumn;

    private ParticipantList participantList;
    private Event event;
    private final Image DEFAULT_IMAGE = new Image(getClass().getResourceAsStream("/cs211/project/images/user.png"));


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        participantList = AllCollection.getInstance().getParticipantList();
        event = (Event) FXRouter.getData();

        nameEventLabel.setText(event.getEventName());
        participantsNumLabel.setText(String.valueOf(event.getParticipantList().getParticipantArrayList().size()));

        selectColumn.setReorderable(false);
        profileColumn.setReorderable(false);
        joinDateColumn.setReorderable(false);
        nameColumn.setReorderable(false);
        usernameColumn.setReorderable(false);
        ageColumn.setReorderable(false);
        joinAsColumn.setReorderable(false);

        tableView.setEditable(true);
        tableView.getSelectionModel().setCellSelectionEnabled(true);

        if (AllCollection.getInstance().getCurrentUser().isAdmin() || event.getHostUser().getUserId().equals(AllCollection.getInstance().getCurrentUser().getUserId())) {
            deleteButton.setVisible(true);
            deleteButton.setManaged(true);
        } else {
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
        }

        searchBarTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filterParticipantByName(newValue);
            }
        });
        pageSetUp();
        showTable();
    }


    private void filterParticipantByName(String filterText) {
        if (participantList != null) {
            ObservableList<Participant> participantsListData = FXCollections.observableArrayList();

            for (Participant participant : participantList.getParticipantArrayList()) {
                if (participant.getEvent().getEventId().equals(event.getEventId()) &&
                        participant.getUser().getProfileName().toLowerCase().contains(filterText.toLowerCase())) {
                    participantsListData.add(participant);
                }
            }

            tableView.setItems(participantsListData);
        }
    }

    private Image loadUserImage(String userId) {
        String[] imageExtensions = {".png", ".jpg", ".jpeg"};
        String userImageFileName = null;

        for (String extension : imageExtensions) {
            userImageFileName = "data/users/users-images/" + userId + extension;
            File imageFile = new File(userImageFileName);

            if (imageFile.exists()) {
                return new Image(imageFile.toURI().toString());
            }
        }
        return DEFAULT_IMAGE;
    }

    @FXML
    public void onDelete(ActionEvent actionEvent) {
        deleteButton.setDisable(true);

        ObservableList<Participant> selectedParticipants = tableView.getItems();
        List<Participant> participantsToDelete = selectedParticipants
                .stream()
                .filter(Participant::isSelected)
                .toList();

        if (!participantsToDelete.isEmpty()) {
            ConfirmDialogController controller = Loader.loadDialog("Delete Participant",
                    "Are you sure you want to delete the participant from", event.getEventName());

            if (controller.getIsConfirm()) {
                boolean changesMade = false;
                for (Participant participant : participantsToDelete) {
                    if (Participant.deleteParticipants(participantList.getParticipantArrayList(), participant)) {
                        changesMade = true;

                        User user = participant.getUser();
                        if (user != null) {
                            user.removeParticipatedEvent(event);
                            AllCollection.getInstance().writeUser();
                        }

                        event.removeParticipantById(participant.getParticipantId());
                        AllCollection.getInstance().writeEventData();
                    }
                }

                tableView.getItems().removeAll(participantsToDelete);
                updateDeleteButtonState();

                if (changesMade) {
                    AllCollection.getInstance().writeParticipantData();
                }
            }
        }

        deleteButton.setDisable(false);
    }


    @FXML
    private void onBackButtonClick() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack(), event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showTable() {
        tableView.getItems().clear();
        profileColumn.setSortable(false);
        selectColumn.setSortable(false);
        if (participantList != null) {
            ObservableList<Participant> participantsListData = FXCollections.observableArrayList();

            for (Participant participant : participantList.getParticipantArrayList()) {
                if (participant.getEvent().getEventId().equals(event.getEventId())) {
                    participantsListData.add(participant);
                }
            }

            selectColumn.setCellValueFactory(cellData -> cellData.getValue().isSelectedProperty());
            selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
            profileColumn.setCellValueFactory(param -> {
                Participant participant = param.getValue();
                Image userImage = loadUserImage(participant.getUser().getUserId());
                if (userImage != null) {
                    return new SimpleObjectProperty<>(userImage);
                } else {
                    return null;
                }
            });

            profileColumn.setCellFactory(param -> new TableCell<Participant, Image>() {
                private final ImageView imageView = new ImageView();
                {
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                    AdjustImageView.radiusImageView(imageView, 55);
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


            usernameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUser().getUsername()));
            nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUser().getProfileName()));
            ageColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getUser().getAge()).asObject());
            joinDateColumn.setCellValueFactory(param -> new SimpleStringProperty(new SimpleDateFormat("dd/MM/yy").format(param.getValue().getJoinDate())));
            joinAsColumn.setCellValueFactory(param -> new SimpleStringProperty("participant"));

            tableView.setItems(participantsListData);
        }
    }

    private void pageSetUp() {
        Loader.loadNavBar(navBarVBox);
    }
    private void updateDeleteButtonState() {
        ObservableList<Participant> selectedParticipants = tableView.getItems();

        deleteButton.setDisable(selectedParticipants.stream().noneMatch(Participant::isSelected));
        participantsNumLabel.setText(String.valueOf(event.getParticipantList().getParticipantArrayList().size()));
    }
}
