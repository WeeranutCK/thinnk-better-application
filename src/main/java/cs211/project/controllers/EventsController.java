package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.services.AllCollection;
import cs211.project.services.FXRouter;
import cs211.project.services.Loader;
import cs211.project.services.PageHistory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventsController {
    @FXML
    private ScrollPane eventCardScrollPane;
    @FXML
    private VBox eventCardVBox;
    @FXML
    private ComboBox<String> eventStatusComboBox;
    @FXML
    private VBox navBarVBox;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private ComboBox<String> sortMenuComboBox;
    @FXML
    private ComboBox<String> sortMethodComboBox;

    protected String currentPage;
    protected EventList eventList;
    protected boolean isEventEnded;
    protected static final int EVENTS_BATCH_SIZE = 15;
    protected int tailEventIndex;
    protected String searchString;


    @FXML
    protected void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        setFilterMode();
        tailEventIndex = 0;
        searchString = null;
        eventList = new EventList();
        setupEventList();
        setupCurrentPage();
        eventStatusComboBoxSetUp();

        searchBarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> eventCardScrollPane.setVvalue(0));
            if (newValue.isEmpty()) {
                searchString = null;
            } else {
                searchString = newValue;
            }
            clearEventCards();
            showEvent();
        });

        eventCardScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                    if (eventList.getEventArrayList().size() <= EVENTS_BATCH_SIZE) {
                        isEventEnded = true;
                    }

                    if (tailEventIndex >= eventList.getEventArrayList().size() - 1) {
                        isEventEnded = true;
                    }

                    if (newValue.doubleValue() >= 0.85 && !isEventEnded) {
                        Platform.runLater(() -> eventCardScrollPane.setVvalue(0.84));
                        showEvent();
                    }
                }
        );
        pageSetup();
    }

    @FXML
    private void onAddNew() {
        try {
            PageHistory.getInstance().pushStack(currentPage);
            FXRouter.goTo("create-new-event");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void updateSort() {
        clearEventCards();
        showEvent();
    }


    protected void setFilterMode() {}

    private void eventStatusComboBoxSetUp() {
        ArrayList<String> eventStatus = new ArrayList<>();
        eventStatus.add("All");
        eventStatus.add("Soon");
        eventStatus.add("On Going");
        eventStatus.add("Ended");

        eventStatusComboBox.getItems().addAll(eventStatus);

        eventStatusComboBox.setValue("All");
    }

    protected void pageSetup() {
        Loader.loadNavBar(navBarVBox);
        sortMenuComboBox.getItems().addAll("Name", "Date");
        sortMenuComboBox.setValue("Name");
        sortMethodComboBox.getItems().addAll("First", "Last");
        sortMethodComboBox.setValue("First");
        showEvent();
    }

    protected void showEvent() {
        if (eventList.getEventArrayList().size() > 0 &&
                Math.min(tailEventIndex,
                        eventList.getEventArrayList().size() - 1) < eventList.getEventArrayList().size()) {

            List<Event> filteredEvents = new ArrayList<>();
            for (Event event : eventList.getEventArrayList()) {
                if ((searchString == null || event.getEventName().toLowerCase().contains(searchString.toLowerCase()))
                        && ("All".equals(eventStatusComboBox.getValue()) || eventStatusComboBox.getValue().equals(event.getStatus()))) {
                    filteredEvents.add(event);
                }
            }
            filteredEvents = filterByRoleComboBox(filteredEvents);

            String sortingCriteria = sortMenuComboBox.getValue();
            boolean reverseOrder = "Last".equals(sortMethodComboBox.getValue());

            if ("Name".equals(sortingCriteria)) {
                Collections.sort(filteredEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        int result = event1.getEventName().compareToIgnoreCase(event2.getEventName());
                        return reverseOrder ? -result : result;
                    }
                });
            } else if ("Date".equals(sortingCriteria)) {
                Collections.sort(filteredEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        int result = event1.getStartTime().compareTo(event2.getStartTime());
                        return reverseOrder ? -result : result;
                    }
                });
            }
            for (int i = tailEventIndex; i <= Math.min(tailEventIndex + EVENTS_BATCH_SIZE, filteredEvents.size() - 1); i++) {
                Event event = filteredEvents.get(i);

                try {
                    FXMLLoader componentLoader = new FXMLLoader(
                            Loader.class.getResource("/cs211/project/components/card.fxml")
                    );
                    VBox childVBox = componentLoader.load();

                    CardController cardController = componentLoader.getController();
                    cardController.setCardDetail(event, currentPage);
                    eventCardVBox.getChildren().add(childVBox);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            tailEventIndex += EVENTS_BATCH_SIZE + 1;
        }
    }

    protected void clearEventCards() {
        eventCardVBox.getChildren().clear();
        tailEventIndex = 0;
        isEventEnded = false;
    }

    protected List<Event> filterByRoleComboBox(List<Event> filteredEvents) {
        return filteredEvents;
    }

    protected void setupEventList() {
        eventList.getEventArrayList().addAll(AllCollection.getInstance().getEventList().getEventArrayList());
    }

    protected void setupCurrentPage() {
        currentPage = "events";
    }
}