package cs211.project.models.collections;

import cs211.project.models.Event;
import cs211.project.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


public class EventList {
    private ArrayList<Event> eventArrayList;


    public EventList() {

        eventArrayList = new ArrayList<>();
    }


    public void addNewEvent(String eventId,
                            String eventName,
                            String category,
                            String description,
                            String place,
                            Date startTime,
                            Date endTime,
                            int maxParticipants,
                            double price,
                            String eventImageFormat,
                            boolean enableStaffRegistration,
                            int maxStaffTeams,
                            int maxTeamMembers,
                            Date participatedRegistrationStartTime,
                            Date participatedRegistrationEndTime)
    {

        eventId = eventId.trim();
        category = category.trim();
        eventName = eventName.trim();
        place = place.trim();
        eventImageFormat = eventImageFormat.trim();

        if(!eventId.equals("")) {
            Event exist = findEventById(eventId);
            if (exist == null) {
                eventArrayList.add(new Event(eventId,
                        eventName,
                        category,
                        description,
                        place,
                        startTime,
                        endTime,
                        maxParticipants,
                        price,
                        eventImageFormat,
                        enableStaffRegistration,
                        maxStaffTeams,
                        maxTeamMembers,
                        participatedRegistrationStartTime,
                        participatedRegistrationEndTime
                        ));
            }
        }
    }

    public void createNewEvent(String eventName,
                               String category,
                               String description,
                               String place,
                               Date startTime,
                               Date endTime,
                               int maxParticipants,
                               double price,
                               User hostUser,
                               File imageFile,
                               boolean enableStaffRegistration,
                               int maxStaffTeams,
                               int maxTeamMembers,
                               Date participatedRegistrationStartTime,
                               Date participatedRegistrationEndTime) {

        category = category.trim();
        eventName = eventName.trim();
        place = place.trim();

        if(!eventName.equals("")) {
            Event exist = findEventByName(eventName);
            if (exist == null) {
                Event temp = new Event(eventName,
                        category,
                        description,
                        place,
                        startTime,
                        endTime,
                        maxParticipants,
                        price,
                        hostUser,
                        imageFile,
                        enableStaffRegistration,
                        maxStaffTeams,
                        maxTeamMembers,
                        participatedRegistrationStartTime,
                        participatedRegistrationEndTime
                );
                eventArrayList.add(temp);
                hostUser.getHostedEvent().getEventArrayList().add(temp);
            }
        }
    }

    public void createNewEvent(String eventName,
                               String category,
                               String description,
                               String place,
                               Date startTime,
                               Date endTime,
                               int maxParticipants,
                               double price,
                               User hostUser,
                               File imageFile,
                               Date participatedRegistrationStartTime,
                               Date participatedRegistrationEndTime
                               ) {

        category = category.trim();
        eventName = eventName.trim();
        place = place.trim();

        if(!eventName.equals("")) {
            Event exist = findEventByName(eventName);
            if (exist == null) {
                Event temp = new Event(eventName,
                        category,
                        description,
                        place,
                        startTime,
                        endTime,
                        maxParticipants,
                        price,
                        hostUser,
                        imageFile,
                        participatedRegistrationStartTime,
                        participatedRegistrationEndTime);
                eventArrayList.add(temp);
                hostUser.getHostedEvent().getEventArrayList().add(temp);
            }
        }
    }

    public Event findEventById(String eventId) {
        for(Event event : eventArrayList) {
            if (event.isEventId(eventId)) {
                return event;
            }
        }
        return null;
    }

    public Event findEventByName(String eventName) {
        for(Event event : eventArrayList) {
            if (event.isEventName(eventName)) {
                return event;
            }
        }
        return null;
    }


    public ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

}
