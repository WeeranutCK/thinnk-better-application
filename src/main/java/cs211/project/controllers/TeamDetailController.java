package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.StaffTeam;
import cs211.project.models.User;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.Loader;
import cs211.project.services.PageHistory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TeamDetailController {
    @FXML
    private CheckBox autoAcceptingCheckBox;
    @FXML
    private CheckBox multiTeamAllowedCheckBox;
    @FXML
    private TextField numberOfParticipantsTextField;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label eventEndTimeLabel;
    @FXML
    private Label eventStartTimeLabel;
    @FXML
    private TextField headTeamTextField;
    @FXML
    private TextField maxParticipantsTextField;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField teamNameTextField;
    @FXML
    private Label teamNameLabel;
    @FXML
    private VBox staffBlockVBox;
    @FXML
    private VBox scheduleVBox;
    @FXML
    private VBox registrantsBlockVBox;
    @FXML
    private Button myTeamButton;
    @FXML
    private Button actionButton;
    @FXML
    private HBox buttonsHBox;
    @FXML
    private Button editTeamButton;
    @FXML
    private VBox mainVBox;

    private StaffTeam currentStaffTeam;
    private Event currentEvent;
    private String role;
    private ScheduleBlockController scheduleBlockController;
    private StaffBlockController staffBlockController;
    private RegistrantsBlockController registrantsBlockController;
    private VBox parentOfScheduleBlock;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        currentStaffTeam = (StaffTeam) FXRouter.getData();
        currentEvent = currentStaffTeam.getEvent();
        setupPage();
    }

    @FXML
    private void onBackButtonClicked() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack(), currentStaffTeam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEditButtonClicked() {
        try {
            PageHistory.getInstance().pushStack("team-detail");
            FXRouter.goTo("edit-team", currentStaffTeam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onMyTeamButtonClicked() {
        try {
            FXRouter.goTo("my-team", currentStaffTeam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onActionButtonClicked() {
        if (actionButton.getText().equalsIgnoreCase("regis")) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Team Registration",
                            "Are you sure you want to regis this team?",
                            currentStaffTeam.getTeamName());

            if (confirmDialogController.getIsConfirm()) {
                currentStaffTeam.regisTeam(AllCollection.getInstance().getCurrentUser());
                AllCollection.getInstance().writeStaffTeamData();
                setButton();
                updateRegistrantsBlock();
            }
        } else if (actionButton.getText().equalsIgnoreCase("join")) {
            ConfirmDialogController confirmDialogController =
                    Loader.loadDialog("Confirm Joining Team",
                            "Are you sure you want to join this team?",
                            currentStaffTeam.getTeamName());

            if (confirmDialogController.getIsConfirm()) {
                if (currentStaffTeam.getIsMultiTeamAllowed()) {
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
                mainVBox.getChildren().add(scheduleVBox);
                AllCollection.getInstance().getStaffList().createNewStaffJoinTeam(currentEvent, currentStaffTeam, AllCollection.getInstance().getCurrentUser());
                AllCollection.getInstance().writeUser();
                AllCollection.getInstance().writeStaffTeamData();
                AllCollection.getInstance().writeStaffData();
                setRole();
                setButton();
                updateStaffBlock();
                setupScheduleBlock();
                buttonsHBox.getChildren().add(myTeamButton);
                setupGeneralInformationBlock();
            }
        }
    }


    private void setupPage() {
        teamNameLabel.setText(currentStaffTeam.getTeamName());
        Loader.loadNavBar(navBarVBox);
        setRole();
        setButton();
        setupGeneralInformationBlock();
        setupScheduleComponent();
        setupStaffBlock();
        setupRegistrantsBlock();
        setupScheduleBlock();
    }

    private void setButton() {
        User currentUser = AllCollection.getInstance().getCurrentUser();
        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("host") || role.equalsIgnoreCase("head")) {
            editTeamButton.setVisible(true);
        } else {
            editTeamButton.setVisible(false);
        }
        boolean canStaffEventJoin = role.equalsIgnoreCase("staff event")
                                    && currentEvent.getStaffTeamList().findTeamsOfUser(currentUser).getStaffTeamArrayList().get(0).getIsMultiTeamAllowed();
        if (role.equalsIgnoreCase("visitor") || canStaffEventJoin) {
            if (!currentStaffTeam.statusRegistration().equalsIgnoreCase("available")) {
                buttonsHBox.getChildren().remove(actionButton);
            } else if (!currentStaffTeam.getIsMultiTeamAllowed() && canStaffEventJoin) {
                buttonsHBox.getChildren().remove(actionButton);
            } else {
                if (currentStaffTeam.getRegistrants().getUserArrayList().contains(currentUser)) {
                    buttonsHBox.getChildren().remove(actionButton);
                } else if (currentStaffTeam.getIsTeamAutoAccepting()) {
                    actionButton.setText("Join");
                } else {
                    actionButton.setText("Regis");
                }
            }
        } else {
            buttonsHBox.getChildren().remove(actionButton);
        }

        if (role.equalsIgnoreCase("participant") || role.equalsIgnoreCase("visitor") || role.equalsIgnoreCase("staff event")) {
            buttonsHBox.getChildren().remove(myTeamButton);
        }
    }

    private void setRole() {
        User currentUser = AllCollection.getInstance().getCurrentUser();
        if (currentUser.isAdmin()) {
            role = "admin";
        } else if (currentEvent.isHost(currentUser.getUserId())) {
            role = "host";
        } else if (currentStaffTeam.isHeadTeam(currentUser)) {
            role = "head";
        } else if (currentStaffTeam.isStaffInTeam(currentUser.getUserId()) != null) {
            role = "staff team";
        } else if (currentEvent.getStaffTeamList().findTeamsOfUser(currentUser).getNumberOfTeams() != 0) {
            role = "staff event";
        } else if (currentEvent.getParticipantList().getParticipantArrayList().contains(currentUser)) {
            role = "participant";
        } else {
            role = "visitor";
        }
    }

    private void setupScheduleBlock() {

        setupScheduleComponent();
        scheduleBlockController.setSortComboBox("Any");
        setComponentAppearance();
    }

    private void setComponentAppearance() {
        if (role.equalsIgnoreCase("head") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("host")) {
            scheduleBlockController.setScheduleButtonVisible(true);
        } else if (role.equalsIgnoreCase("staff team") || role.equalsIgnoreCase("staff event") || role.equalsIgnoreCase("participant")) {
            scheduleBlockController.setScheduleButtonVisible(false);
            scheduleBlockController.setScheduleEditable(false);
        } else {
            mainVBox.getChildren().remove(scheduleVBox);
        }

        if (currentEvent.getStatus().equalsIgnoreCase("ended")) {
            scheduleBlockController.setScheduleEditable(false);
            scheduleBlockController.setScheduleButtonVisible(false);
        }
    }

    private void setupScheduleComponent() {
        if (scheduleBlockController != null) {
            scheduleBlockController.clearSchedule();
            scheduleVBox.getChildren().clear();
        }

        scheduleBlockController = Loader.loadSchedule(scheduleVBox);
        scheduleBlockController.setEvent(currentEvent);
        scheduleBlockController.setScheduleDetails(currentEvent, currentEvent.getStaffTeamList(), "team-detail");
        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("host") || role.equalsIgnoreCase("head")) {
            scheduleBlockController.setScheduleEditable(true);
            scheduleBlockController.setScheduleButtonVisible(true);
        } else {
            scheduleBlockController.setScheduleEditable(false);
            scheduleBlockController.setScheduleButtonVisible(false);
        }
    }

    public void setupGeneralInformationBlock() {
        setupData();
    }

    private void setupData() {
        teamNameTextField.setText(currentStaffTeam.getTeamName());
        String showEventNameLabel = "Event : ";
        eventNameLabel.setText("(" + showEventNameLabel + currentEvent.getEventName() + ")");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        eventStartTimeLabel.setText(simpleDateFormat.format(currentEvent.getStartTime()));
        eventEndTimeLabel.setText(simpleDateFormat.format(currentEvent.getEndTime()));
        startTimeTextField.setText(simpleDateFormat.format(currentStaffTeam.getStartTimeRegistration()));
        endTimeTextField.setText(simpleDateFormat.format(currentStaffTeam.getEndTimeRegistration()));
        teamNameTextField.setText(currentStaffTeam.getTeamName());
        maxParticipantsTextField.setText(String.valueOf(currentStaffTeam.getMaxMembers()));
        numberOfParticipantsTextField.setText(String.valueOf(currentStaffTeam.getStaffList().getStaffArrayList().size()));
        autoAcceptingCheckBox.setSelected(currentStaffTeam.getIsTeamAutoAccepting());
        multiTeamAllowedCheckBox.setSelected(currentStaffTeam.getIsMultiTeamAllowed());
        headTeamTextField.setText(currentStaffTeam.getHeadTeam() == null? "Not Assigned" : currentStaffTeam.getHeadTeam().getUser().getUsername());
    }

    private void setupStaffBlock() {
        staffBlockVBox.getChildren().clear();
        staffBlockController = Loader.loadStaffBlock(staffBlockVBox, currentStaffTeam, this);
    }

    private void setupRegistrantsBlock() {
        registrantsBlockVBox.getChildren().clear();
        registrantsBlockController = Loader.loadRegistrantsBlock(registrantsBlockVBox, currentStaffTeam, staffBlockController);
    }

    private void updateStaffBlock() {
        staffBlockController.updateStaffTableDataList();
    }

    private void updateRegistrantsBlock() {
        registrantsBlockController.updateTableDataList();
    }

    public void backToPreviousPage() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack(), currentEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
