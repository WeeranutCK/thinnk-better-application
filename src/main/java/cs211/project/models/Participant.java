package cs211.project.models;

import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Date;

public class Participant {
    private Event event;
    private Date joinDate;
    private String participantId;
    private User user;

    private BooleanProperty isSelected;

    public Participant() {
        isSelected = new SimpleBooleanProperty(false);
    }

    public Participant(Event event, User user) {
        this.event = event;
        this.user = user;
        this.joinDate = new Date();
        participantId = IdGenerator.generateId("participant",
                AllCollection.getInstance().getParticipantList().getParticipantArrayList().size());
        isSelected = new SimpleBooleanProperty(false);
    }

    public Participant(Date joinDate, String participantId) {
        this.joinDate = joinDate;
        this.participantId = participantId;
        isSelected = new SimpleBooleanProperty(false);
    }

    public boolean isParticipantId(String participantId) {
        return this.participantId.equals(participantId);
    }

    public static boolean deleteParticipants(ArrayList<Participant> participantArrayList, Participant participantToDelete) {
        if (participantArrayList != null) {
            boolean removed = participantArrayList.remove(participantToDelete);
            return removed;
        }
        return false;
    }

    public Event getEvent() {
        return event;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public String getParticipantId() {
        return participantId;
    }

    public User getUser() {
        return user;
    }

    public boolean isSelected() {
        return isSelected.get();
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }


    public void setEvent(Event event) {
        this.event = event;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
