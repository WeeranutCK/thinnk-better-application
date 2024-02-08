package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Participant;
import cs211.project.models.User;
import cs211.project.models.Wallet;
import cs211.project.models.collections.ParticipantList;
import cs211.project.services.*;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class CardController {
    @FXML
    private Label categoryLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private ImageView eventImageView;
    @FXML
    private Button joinAsParticipant;
    @FXML
    private Button joinAsStaff;
    @FXML
    private Label maxParticipantsLabel;
    @FXML
    private Label nameEventLabel;
    @FXML
    private Label placeLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label unsoldLabel;
    @FXML
    private Label soldLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label statusLabel;

    private String currentPage;
    private User currentUser;
    private Event event;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        currentUser = AllCollection.getInstance().getCurrentUser();
        String currentId = AllCollection.getInstance().getCurrentUser().getUserId();
        currentUser = AllCollection.getInstance().getUserList().findUserById(currentId);


        joinAsParticipant.setOnMouseEntered(event -> {
            zoomButton(joinAsParticipant, true);
        });

        joinAsParticipant.setOnMouseExited(event -> {
            zoomButton(joinAsParticipant, false);
        });

        joinAsStaff.setOnMouseEntered(event -> {
            zoomButton(joinAsStaff, true);
        });

        joinAsStaff.setOnMouseExited(event -> {
            zoomButton(joinAsStaff, false);
        });
    }

    @FXML
    public void onJoinAsParticipant() {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy, HH:mm");
        String formattedStartParticipantRegistration = outputFormat.format(event.getParticipatedRegistrationStartTime());
        String formattedEndParticipantRegistration = outputFormat.format(event.getParticipatedRegistrationEndTime());

        if (participantRegistrationIsEnded()) {
            joinAsParticipant.setVisible(false);
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

        showButtons();
        updateUI();
    }

    @FXML
    public void onJoinAsStaff() {
        try {
            PageHistory.getInstance().pushStack("events");
            FXRouter.goTo("team-registration", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onMoreInfo() {
        try {
            PageHistory.getInstance().pushStack(currentPage);
            FXRouter.goTo("event-info", event);
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

    private void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    private void setData(Event event) {
        this.event = event;
        event.calculateSoldAndUnsold();

        if (event.getStatus().equalsIgnoreCase("Ended")) {
            joinAsStaff.setDisable(true);
            joinAsStaff.setStyle("-fx-background-color: #131113");
            joinAsParticipant.setDisable(true);
            joinAsParticipant.setStyle("-fx-background-color: #131113");
        }
        statusLabel.setText(event.getStatus());
        StatusSetting.setupStatusLabel(statusLabel, event, 10);

        updateUI();
        showButtons();

        eventImageView.setImage(event.getEventImage());
        AdjustImageView.setViewPortImageView(eventImageView, event.getEventImage());
        AdjustImageView.radiusImageView(eventImageView, 55);
    }

    public void setCardDetail(Event event, String currentPage) {
        setData(event);
        setCurrentPage(currentPage);
    }

    private void showButtons() {
        if (currentUser != null ) {
            if (currentUser.equals(event.getHostUser()) || currentUser.isAdmin()) {
                joinAsParticipant.setVisible(false);
                joinAsStaff.setVisible(false);
            } else if (currentUser.getParticipatedEvent().getEventArrayList().contains(event)) {
                joinAsParticipant.setVisible(true);
                joinAsStaff.setDisable(true);
                joinAsStaff.setStyle("-fx-background-color: #131113");
                joinAsParticipant.setMouseTransparent(true);
                joinAsParticipant.setDisable(false);
                joinAsParticipant.setStyle("-fx-background-color: #131113");
            } else if (currentUser.getStaffParticipatedEvent().getEventArrayList().contains(event)) {
                joinAsParticipant.setDisable(true);
                joinAsParticipant.setStyle("-fx-background-color: #131113");
                joinAsStaff.setVisible(true);
                joinAsStaff.setStyle("-fx-background-color: #131113");
            } else if (event.isFull()) {
                joinAsParticipant.setDisable(true);
                joinAsParticipant.setStyle("-fx-background-color: #131113");
            }

            if (!event.isEnableStaffRegistration()) {
                joinAsStaff.setDisable(true);
                joinAsStaff.setStyle("-fx-background-color: #131113");
            }
        }
    }

    private void zoomButton(Button button, boolean zoomIn) {
        double targetScale = zoomIn ? 1.2 : 1.0;

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), button);
        scaleTransition.setToX(targetScale);
        scaleTransition.setToY(targetScale);
        scaleTransition.play();
    }

    private void updateUI() {
        if (event != null) {
            categoryLabel.setText(event.getCategory());

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy, HH:mm");
            String startTime = outputFormat.format(event.getStartTime());
            String endTime = outputFormat.format(event.getEndTime());

            startDateLabel.setText(startTime);
            endDateLabel.setText(endTime);

            nameEventLabel.setText(event.getEventName());
            placeLabel.setText(event.getPlace());
            priceLabel.setText(String.format("%.2f",event.getPrice()));

            event.calculateSoldAndUnsold();
            if (event.isNoLimitMaxParticipant()) {
                maxParticipantsLabel.setText("No Limit");
            } else {
                maxParticipantsLabel.setText(String.valueOf(event.getMaxParticipants()));
            }
            soldLabel.setText(String.valueOf(event.getSold()));
            if(event.getUnSold() == -1){
                unsoldLabel.setText("No Limit");
            }else {
                unsoldLabel.setText(String.valueOf(event.getUnSold()));
            }
        }
    }

}
