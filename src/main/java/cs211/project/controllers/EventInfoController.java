package cs211.project.controllers;

import cs211.project.models.*;
import cs211.project.models.collections.ParticipantList;
import cs211.project.models.collections.StaffTeamList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;


public class EventInfoController {
    @FXML
    private Label allTeamLabel;
    @FXML
    private Button allTeamViewButton;
    @FXML
    private VBox baseVBox;
    @FXML
    private Label categoryLabel;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Button editButton;
    @FXML
    private Label endDateLabel;
    @FXML
    private ImageView eventImage;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label hostedLabel;
    @FXML
    private VBox navBarVBox;
    @FXML
    private Button participantJoinButton;
    @FXML
    private Label placeLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private HBox progressBarHBox;
    @FXML
    private VBox scheduleSettingVBox;
    @FXML
    private VBox scheduleVBox;
    @FXML
    private Label soldLabel;
    @FXML
    private Button staffJoinButton;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label teamRegistrationLabel;
    @FXML
    private Button teamRegistrationViewButton;
    @FXML
    private Label totalLabel;
    @FXML
    private VBox viewPathVBox;

    private User currentUser;
    private Event event;
    private ScheduleBlockController scheduleBlockController;
    private StaffTeamList staffTeamListOfUser;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        Object data = FXRouter.getData();
        if (data instanceof Event) {
            event = (Event)data;
        } else if (data instanceof StaffTeam) {
            event = ((StaffTeam)data).getEvent();
        }

        currentUser = AllCollection.getInstance().getCurrentUser();
        staffTeamListOfUser = event.getStaffTeamList().findTeamsOfUser(currentUser);


        eventImage.setImage(event.getEventImage());
        AdjustImageView.setViewPortImageView(eventImage, event.getEventImage());
        AdjustImageView.radiusImageView(eventImage, 55);

        Loader.loadNavBar(navBarVBox);
        Loader.loadProgressBar(progressBarHBox);

        setupScheduleComponent();

        setLabel();
        setButtonVisibility();
        setComponentAppearance();
        StatusSetting.setupStatusLabel(statusLabel, event, 10);
    }

    @FXML
    private void onAllTeamViewButtonClick() {
        try {
            PageHistory.getInstance().pushStack("event-info");
            FXRouter.goTo("all-team", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    private void onEditButtonClick() {
        try {
            PageHistory.getInstance().pushStack("event-info");
            FXRouter.goTo("edit-event", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onParticipantJoinButtonClick() {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy, HH:mm");
        String formattedStartParticipantRegistration = outputFormat.format(event.getParticipatedRegistrationStartTime());
        String formattedEndParticipantRegistration = outputFormat.format(event.getParticipatedRegistrationEndTime());

        if (participantRegistrationIsEnded()) {
            participantJoinButton.setVisible(false);
        } else if (participantRegistrationIsSoon()) {
            WarningDialogController warningDialog;
            warningDialog = Loader.loadWarningDialog("Registration Soon",
                    "Event registration is not open yet. Please try again on " + formattedStartParticipantRegistration + "  to " + formattedEndParticipantRegistration);
        } else {
            if (event != null) {
                Participant participant;
                ParticipantList participantList;
                if (event.getPrice() == 0.0) {
                    ConfirmDialogController controller = Loader.loadDialog("Join Event",
                            "Are you sure you want to join", event.getEventName());
                    if (controller.getIsConfirm()) {
                        currentUser = AllCollection.getInstance().getCurrentUser();
                        String eventId = event.getEventId();
                        participantList = AllCollection.getInstance().getParticipantList();

                        if (currentUser.getParticipatedEvent().findEventById(eventId) == null) {
                            participant = new Participant(event, currentUser);
                            currentUser.getParticipatedEvent().getEventArrayList().add(event);
                            participantList.getParticipantArrayList().add(participant);
                            event.getParticipantList().getParticipantArrayList().add(participant);
                            AllCollection.getInstance().writeParticipantData();
                            setButtonVisibility();
                            isRelatedInEvent();
                            Insets margins = new Insets(45);
                            baseVBox.getChildren().addAll(scheduleVBox);
                            VBox.setMargin(scheduleVBox, margins);
                            scheduleBlockController.setScheduleButtonVisible(false);
                        }
                    }
                } else {
                    ConfirmDialogController controller = Loader.loadDialog("Join Event",
                            "You have to pay " + event.getPrice() + " Baht to join this event.", event.getEventName());
                    if (controller.getIsConfirm()) {
                        currentUser = AllCollection.getInstance().getCurrentUser();
                        String eventId = event.getEventId();
                        participantList = AllCollection.getInstance().getParticipantList();
                        Wallet currentUserWallet = currentUser.getWallet();
                        if (currentUser.getParticipatedEvent().findEventById(eventId) == null) {
                            if (currentUserWallet.isEnoughMoney(event.getPrice())) {
                                currentUserWallet.purchase(eventId, event.getPrice());
                                participant = new Participant(event, currentUser);
                                currentUser.getParticipatedEvent().getEventArrayList().add(event);
                                participantList.getParticipantArrayList().add(participant);
                                event.getParticipantList().getParticipantArrayList().add(participant);
                                AllCollection.getInstance().writeParticipantData();

                                event.getHostUser().getWallet().topUp(event.getPrice());
                                AllCollection.getInstance().writeWalletData();

                                setButtonVisibility();
                                Insets margins = new Insets(45);
                                baseVBox.getChildren().addAll(scheduleVBox);
                                VBox.setMargin(scheduleVBox, margins);
                                scheduleBlockController.setScheduleButtonVisible(false);
                            } else {
                                WarningDialogController warningDialog;
                                warningDialog = Loader.loadWarningDialog("Warning",
                                        "Your current amount is too low to join. Please top up your money.");
                            }
                        }
                    }
                }
            }
        }

    }

    @FXML
    private void onParticipantViewButtonClick() {
        try {
            PageHistory.getInstance().pushStack("event-info");
            FXRouter.goTo("participants", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onStaffJoinButton() {
        if (event.isEnableStaffRegistration()) {
            try {
                PageHistory.getInstance().pushStack("event-info");
                FXRouter.goTo("team-registration", event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onTeamRegistrationViewButtonClick() {
        try {
            PageHistory.getInstance().pushStack("event-info");
            FXRouter.goTo("team-registration", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean participantRegistrationIsEnded() {
        if (TimeConversion.getNowDate().after(event.getParticipatedRegistrationEndTime())) {
            return true;
        }
        return false;
    }

    private boolean participantRegistrationIsSoon() {
        if (TimeConversion.getNowDate().before(event.getParticipatedRegistrationStartTime())) {
            return true;
        }
        return false;
    }

    private boolean isHostOrAdmin() {
        return (currentUser.isAdmin() || event.isHost(currentUser.getUserId()));
    }

    private boolean isRelatedInEvent() {
        return (currentUser.isStaff(event)) ||
                event.getParticipantList().findParticipantByUserId(currentUser.getUserId())!=null;
    }

    private void setButtonVisibility() {
        if (isHostOrAdmin()) {
            editButton.setVisible(true);
        } else {
            editButton.setVisible(false);
        }

        if (event.isEnableStaffRegistration() || event.getStaffTeamList().getStaffTeamArrayList().size() != event.getMaxTeamMembers()) {
            staffJoinButton.setVisible(true);
            if (event.getStatus().equals("Ended")) {
                staffJoinButton.setVisible(false);
                scheduleBlockController.setScheduleButtonVisible(false);
                scheduleBlockController.setScheduleEditable(false);
            } else if (event.getParticipantList().findParticipantByUserId(currentUser.getUserId())!=null) {
                staffJoinButton.setVisible(false);
            } else if (isHostOrAdmin()) {
                staffJoinButton.setVisible(false);
            }
        } else {
            staffJoinButton.setVisible(false);
        }

        if (event.isFull()) {
            participantJoinButton.setVisible(false);
        } else if (event.getStatus().equals("Ended")) {
            participantJoinButton.setVisible(false);
            scheduleBlockController.setScheduleButtonVisible(false);
            scheduleBlockController.setScheduleEditable(false);
        } else if (isRelatedInEvent()) {
            participantJoinButton.setVisible(false);
        } else if (isHostOrAdmin()) {
            participantJoinButton.setVisible(false);
        } else {
            participantJoinButton.setVisible(true);
        }
    }

    private void setComponentAppearance() {
        if (isHostOrAdmin()) {
            teamRegistrationLabel.setVisible(true);
            allTeamLabel.setVisible(true);
            teamRegistrationViewButton.setDisable(false);
            allTeamViewButton.setDisable(false);
            scheduleBlockController.setScheduleButtonVisible(true);
            if (event.getStatus().equals("Ended")) {
                scheduleBlockController.setScheduleEditable(false);
            } else {
                scheduleBlockController.setScheduleEditable(true);
            }
        } else if (isRelatedInEvent()) {
            ((VBox)viewPathVBox.getParent()).getChildren().remove(viewPathVBox);
            teamRegistrationLabel.setVisible(false);
            allTeamLabel.setVisible(false);
            teamRegistrationViewButton.setDisable(true);
            allTeamViewButton.setDisable(true);
            scheduleBlockController.setScheduleButtonVisible(false);
            scheduleBlockController.setScheduleEditable(false);
        } else {
            ((VBox)scheduleVBox.getParent()).getChildren().remove(scheduleVBox);
            ((VBox)scheduleSettingVBox.getParent()).getChildren().remove(scheduleSettingVBox);
            ((VBox)viewPathVBox.getParent()).getChildren().remove(viewPathVBox);
            teamRegistrationLabel.setVisible(false);
            allTeamLabel.setVisible(false);
            teamRegistrationViewButton.setDisable(true);
            allTeamViewButton.setDisable(true);
        }
    }

    private void setLabel() {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy, HH:mm");
        User hostedUser = event.getHostUser();

        eventNameLabel.setText(event.getEventName());
        placeLabel.setText(event.getPlace());
        hostedLabel.setText(hostedUser.getUsername());
        categoryLabel.setText(event.getCategory());
        startDateLabel.setText(outputFormat.format(event.getStartTime()));
        endDateLabel.setText(outputFormat.format(event.getEndTime()));
        setPriceLabel();
        setSoldLabel();
        setTotalLabel();
        descriptionTextArea.setText(event.getDescription());
        descriptionTextArea.setEditable(false);
    }

    private void setPriceLabel() {
        if (event.getPrice() == 0.0) {
            priceLabel.setText("Free");
        } else {
            priceLabel.setText(String.format("%.2f",event.getPrice()) + " Baht");
        }
    }

    private void setSoldLabel() {
        if (event.getSold() == -1) {
            soldLabel.setText("No Limit");
        } else {
            soldLabel.setText(String.valueOf(event.getSold()));
        }
    }

    private void setTotalLabel() {
        if (event.isNoLimitMaxParticipant()) {
            totalLabel.setText("No Limit");
        } else {
            totalLabel.setText(String.valueOf(event.getMaxParticipants()));
        }
    }

    private void setupScheduleComponent() {
        if (scheduleBlockController != null) {
            scheduleBlockController.clearSchedule();
            scheduleVBox.getChildren().clear();
        }

        if (isHostOrAdmin()) {
            if (!event.getStatus().equals("Ended")) {
                scheduleBlockController = Loader.loadSchedule(scheduleVBox);
                scheduleBlockController.setScheduleEditable(true);
                scheduleBlockController.setScheduleDetails(event, null, "event-info");
                scheduleBlockController.setSortComboBox("Any");
            } else {
                scheduleBlockController = Loader.loadSchedule(scheduleVBox);
                scheduleBlockController.setScheduleEditable(false);
                scheduleBlockController.setScheduleDetails(event, null, "event-info");
                scheduleBlockController.setSortComboBox("Any");
            }
        } else {
            if (staffTeamListOfUser != null) {
                scheduleBlockController = Loader.loadSchedule(scheduleVBox);
                scheduleBlockController.setScheduleDetails(event, staffTeamListOfUser, "event-info");
                scheduleBlockController.setSortComboBox("Any");
            } else {
                scheduleBlockController = Loader.loadSchedule(scheduleVBox);
                scheduleBlockController.setScheduleDetails(event, null, "event-info");
                scheduleBlockController.setSortComboBox("Any");
            }
        }
    }
}
