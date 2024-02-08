package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;

public class EventsHistoryController extends EventsController {
    @FXML
    private ComboBox<String> roleComboBox;
    private String filterMode;

    @Override
    public void setFilterMode() {
        try {
            filterMode = (String) FXRouter.getData();
            if (filterMode == null) {
                filterMode = "All";
            }
        } catch (Exception e) {
            filterMode = "All";
        }
        roleComboBox.setValue(filterMode);
    }

    @Override
    protected List<Event> filterByRoleComboBox(List<Event> filteredEvents) {
        filterMode = roleComboBox.getValue();
        List<Event> returnEvent = new ArrayList<>();
        if (filterMode != null && !filterMode.equalsIgnoreCase("All")) {
            if ("Staff".equalsIgnoreCase(filterMode)) {
                for (Event event : filteredEvents) {
                    if(AllCollection.getInstance().getCurrentUser().getStaffParticipatedEvent().getEventArrayList().contains(event)) {
                        returnEvent.add(event);
                    }
                }
            } else if ("Participant".equalsIgnoreCase(filterMode)) {
                for (Event event : filteredEvents) {
                    if(AllCollection.getInstance().getCurrentUser().getParticipatedEvent().getEventArrayList().contains(event)) {
                        returnEvent.add(event);
                    }
                }
            }
        } else {
            return filteredEvents;
        }
        return returnEvent;
    }

    @Override
    protected void setupCurrentPage() {
        currentPage = "events-history";
        roleComboBox.getItems().addAll("All", "Participant", "Staff");
    }

    @Override
    protected void setupEventList() {
        eventList.getEventArrayList().clear();
        eventList.getEventArrayList().addAll(AllCollection.getInstance().getCurrentUser().getStaffParticipatedEvent().getEventArrayList());
        eventList.getEventArrayList().addAll(AllCollection.getInstance().getCurrentUser().getParticipatedEvent().getEventArrayList());
    }
}
