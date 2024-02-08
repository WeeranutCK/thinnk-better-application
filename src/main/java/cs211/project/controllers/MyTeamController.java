package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.collections.EventList;
import cs211.project.services.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyTeamController {
    @FXML
    private VBox chatVBox;
    @FXML
    private Button editButton;
    @FXML
    private Button editTeamButton;
    @FXML
    private VBox navBarVBox;
    @FXML
    private VBox scheduleVBox;
    @FXML
    private ComboBox<Event> selectEventComboBox;
    @FXML
    private ComboBox<StaffTeam> selectStaffTeamComboBox;
    @FXML
    private VBox staffBlockVBox;
    @FXML
    private Button teamDetailButton;
    @FXML
    private Button allTeamsButton;


    private ChatBlockController chatBlockController;
    private Event event;
    private Map<String, Event> eventIdToEventMap = new HashMap<>();
    private ScheduleBlockController scheduleBlockController;
    private ArrayList<Staff> selectedStaff = new ArrayList<>();
    private StaffTeam staffTeam;
    private ObservableList<Staff> staffObservableList;
    private Map<String, StaffTeam> staffTeamIdToStaffTeamMap = new HashMap<>();


    @FXML
    private void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        event = null;

        Object data = FXRouter.getData();
        if (data instanceof StaffTeam) {
            staffTeam = (StaffTeam) data;
            event = staffTeam.getEvent();
        } else if (data instanceof Event) {
            event = (Event) data;
        }

        pageSetup();
        Loader.loadNavBar(navBarVBox);
    }

    @FXML
    private void onEditButtonClicked() {
        try {
            PageHistory.getInstance().pushStack("my-team");
            FXRouter.goTo("edit-event", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEditTeamButtonClicked() {
        try {
            PageHistory.getInstance().pushStack("my-team");
            FXRouter.goTo("edit-team", staffTeam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEventComboBoxSelected() {
        onEventComboBoxSelectedMethod();

        staffTeam = selectStaffTeamComboBox.getValue();
        setupComponent();
        checkRoleComponent();
    }

    @FXML
    private void onStaffTeamComboBoxSelected() {
        onStaffTeamComboBoxSelectedMethod();
        staffTeam = selectStaffTeamComboBox.getValue();
        setupComponent();
        checkRoleComponent();
    }

    @FXML
    private void onTeamDetailButtonClicked() {
        try {
            PageHistory.getInstance().pushStack("my-team");
            FXRouter.goTo("team-detail", staffTeam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onAllTeamsButtonClicked() {
        try {
            PageHistory.getInstance().pushStack("my-team");
            FXRouter.goTo("all-team", event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void checkRoleComponent() {
        if (AllCollection.getInstance().getCurrentUser().isAdmin() ||
                event != null && event.isHost(AllCollection.getInstance().getCurrentUser().getUserId()) ||
                staffTeam != null) {
            editButton.setDisable(false);
            scheduleBlockController.setScheduleEditable(true);

            if (staffTeam != null && staffTeam.isHeadTeam(AllCollection.getInstance().getCurrentUser())) {
                editTeamButton.setVisible(true);
            } else {
                editTeamButton.setVisible(true);
            }
        } else {
            editTeamButton.setVisible(false);
            editButton.setDisable(true);
            scheduleBlockController.setScheduleEditable(false);
        }

        if (event != null && event.getEndTime().getTime() < TimeConversion.getNowDate().getTime()) {
            scheduleBlockController.setScheduleEditable(false);
            scheduleBlockController.setScheduleButtonVisible(false);
        }

        if (staffTeam == null) {
            chatBlockController.setEnableSendTextDisable(true);
            teamDetailButton.setDisable(true);
        } else {
            chatBlockController.setEnableSendTextDisable(false);
            teamDetailButton.setDisable(false);
        }

        if (event == null) {
            allTeamsButton.setDisable(true);
        } else {
            allTeamsButton.setDisable(false);
        }
    }

    private void clearComponent() {
        if (chatBlockController != null) chatBlockController.clearChat();
        chatVBox.getChildren().clear();
        if (scheduleBlockController != null) scheduleBlockController.clearSchedule();
        scheduleVBox.getChildren().clear();
        editTeamButton.setDisable(false);
        allTeamsButton.setDisable(false);
        teamDetailButton.setDisable(false);
        clearTableView();
    }

    private void clearTableView() {
        if (staffObservableList != null) {
            staffObservableList.clear();
        }
    }

    public void onEventComboBoxSelectedMethod() {
        Event selectedEvent = selectEventComboBox.getValue();
        if (selectedEvent != null) {
            event = selectedEvent;
            selectStaffTeamComboBox.setValue(null);
        }

        if (staffTeam == null || event == null) {
            clearComponent();
            setupComponent();
            checkRoleComponent();
        } else {
            clearComponent();
        }

        selectStaffTeamComboBox.getItems().clear();
        if (event != null) {
            if (!AllCollection.getInstance().getCurrentUser().isAdmin() && !event.isHost(AllCollection.getInstance().getCurrentUser().getUserId())) {
                for (StaffTeam staffTeam : AllCollection.getInstance().getStaffTeamList().getStaffTeamArrayList()) {
                    if ((staffTeam.getStaffList().findStaffInTeamByUserId(AllCollection.getInstance().getCurrentUser().getUserId()) != null
                            || staffTeam.getHeadTeam() != null && staffTeam.getHeadTeam().getUser().getUserId().equals(AllCollection.getInstance().getCurrentUser().getUserId())
                            ) && event.equals(staffTeam.getEvent())) {
                        selectStaffTeamComboBox.getItems().add(staffTeam);
                        staffTeamIdToStaffTeamMap.put(staffTeam.getTeamId(), staffTeam);
                    }
                }
            } else {
                for (StaffTeam staffTeam: AllCollection.getInstance().getStaffTeamList().getStaffTeamArrayList()) {
                    if (event.equals(staffTeam.getEvent())) {
                        selectStaffTeamComboBox.getItems().add(staffTeam);
                        staffTeamIdToStaffTeamMap.put(staffTeam.getTeamId(), staffTeam);
                    }
                }
            }
        }

        selectStaffTeamComboBox.setConverter(new StringConverter<StaffTeam>() {
            @Override
            public String toString(StaffTeam staffTeam) {
                return staffTeam != null ? staffTeam.getTeamName() : "";
            }

            @Override
            public StaffTeam fromString(String staffTeamId) {
                return staffTeamIdToStaffTeamMap.get(staffTeamId);
            }
        });

        if (staffTeam == null) {
            selectStaffTeamComboBox.getSelectionModel().selectFirst();
            staffTeam = selectStaffTeamComboBox.getValue();
        } else {
            selectStaffTeamComboBox.setValue(staffTeam);
        }

        onStaffTeamComboBoxSelectedMethod();

        if (staffTeam == null) {
            editTeamButton.setDisable(true);
        }
    }

    private void onStaffTeamComboBoxSelectedMethod() {
        StaffTeam selectedStaffTeam = selectStaffTeamComboBox.getValue();
        if (selectedStaffTeam != null) {
            selectedStaff.clear();
            staffTeam = selectedStaffTeam;
        } else {
            clearTableView();
        }

        if (staffTeam == null || event == null) {
            clearComponent();
            setupComponent();
            checkRoleComponent();
        }
    }

    public void pageSetup() {
        selectEventComboBox.getItems().clear();
        selectStaffTeamComboBox.getItems().clear();

        setSelectEventComboBox();

        onEventComboBoxSelectedMethod();

        StaffTeam selectedStaffTeam = selectStaffTeamComboBox.getValue();
        if (selectedStaffTeam != null) {
            staffTeam = selectedStaffTeam;
        }

        setupComponent();
    }

    private void setSelectEventComboBox() {
        selectEventComboBox.getItems().clear();
        eventIdToEventMap.clear();

        if (!AllCollection.getInstance().getCurrentUser().isAdmin()) {
            EventList allEvent = new EventList();
            allEvent.getEventArrayList().addAll(AllCollection.getInstance().getCurrentUser().getHostedEvent().getEventArrayList());
            allEvent.getEventArrayList().addAll(AllCollection.getInstance().getCurrentUser().getStaffParticipatedEvent().getEventArrayList());

            for (Event event : allEvent.getEventArrayList()) {
                if (event != null) {
                    selectEventComboBox.getItems().add(event);
                    eventIdToEventMap.put(event.getEventId(), event);
                }
            }
        } else {
            for (Event event : AllCollection.getInstance().getEventList().getEventArrayList()) {
                selectEventComboBox.getItems().add(event);
                eventIdToEventMap.put(event.getEventId(), event);
            }
        }

        selectEventComboBox.setConverter(new StringConverter<Event>() {
            @Override
            public String toString(Event event) {
                return event != null ? event.getEventName() : "";
            }

            @Override
            public Event fromString(String eventId) {
                return eventIdToEventMap.get(eventId);
            }
        });

        if (event == null) {
            event = selectEventComboBox.getValue();
        } else {
            selectEventComboBox.setValue(event);
        }

        onEventComboBoxSelectedMethod();
    }

    public void setupComponent() {
        if (chatBlockController != null) {
            chatBlockController.clearChat();
            chatVBox.getChildren().clear();
        }
        if (scheduleBlockController != null) {
            scheduleBlockController.clearSchedule();
            scheduleVBox.getChildren().clear();
        }

        chatBlockController = Loader.loadChat(chatVBox);
        scheduleBlockController = Loader.loadSchedule(scheduleVBox);
        checkRoleComponent();

        if (event != null) {
            if (staffTeam != null) {
                chatBlockController.setStaffTeamId(staffTeam.getTeamId());
                chatBlockController.loadChat(false);
            }

            scheduleBlockController.setScheduleDetails(event, event.getStaffTeamList(), "my-team");
            scheduleBlockController.showSchedule();
            scheduleBlockController.setSortComboBox("Any");
        }

        staffBlockVBox.getChildren().clear();
        Loader.loadStaffBlock(staffBlockVBox, staffTeam, this);
    }

    public void setStaffTeam(StaffTeam staffTeam) {
        this.staffTeam = staffTeam;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
