package cs211.project.models;

import cs211.project.models.collections.ParticipantList;
import cs211.project.models.collections.ScheduleList;
import cs211.project.models.collections.StaffTeamList;
import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;
import cs211.project.services.TimeConversion;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;


public class Event {
    private ScheduleList activityList;
    private String category;
    private Date endTime;
    private String eventId;
    private Image eventImage;
    private String eventImageFormat;
    private String eventName;
    private boolean enableStaffRegistration;
    private String description;
    private User hostUser;
    private int maxParticipants;
    private int maxTeamMembers;
    private int maxStaffTeams;
    private ParticipantList participantList;
    private String place;
    private double price;
    private StaffTeamList staffTeamList;
    private Date startTime;
    private int sold;
    private int unSold;
    private Date participatedRegistrationStartTime;
    private Date participatedRegistrationEndTime;
    private final Image DEFAULT_EVENT_IMAGE = new Image(getClass().getResourceAsStream("/cs211/project/images/background-image.png"));


    public Event(String eventName,
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

        this.activityList = new ScheduleList();
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.description = description;
        this.hostUser = hostUser;
        this.maxParticipants = maxParticipants;
        this.place = place;
        this.price = price;
        this.participatedRegistrationEndTime = participatedRegistrationEndTime;
        this.participatedRegistrationStartTime = participatedRegistrationStartTime;

        eventId = IdGenerator.generateId("event",
                AllCollection.getInstance().getUserList().getUserArrayList().size());

        maxTeamMembers = 0;
        maxStaffTeams = 0;
        activityList = new ScheduleList();
        staffTeamList = new StaffTeamList();
        participantList = new ParticipantList();
        enableStaffRegistration = false;

        if (imageFile != null) {
            eventImageFormat = getFileExtension(imageFile);
            copyFile(imageFile);
        } else {
            eventImageFormat = "null";
        }
        setEventImage();
    }

    public Event(String eventName,
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

        this.activityList = new ScheduleList();
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.description = description;
        this.hostUser = hostUser;
        this.maxParticipants = maxParticipants;
        this.participantList = new ParticipantList();
        this.place = place;
        this.price = price;
        this.staffTeamList = new StaffTeamList();
        this.enableStaffRegistration = enableStaffRegistration;
        this.maxStaffTeams = maxStaffTeams;
        this.maxTeamMembers = maxTeamMembers;
        this.participatedRegistrationEndTime = participatedRegistrationEndTime;
        this.participatedRegistrationStartTime = participatedRegistrationStartTime;

        eventId = IdGenerator.generateId("event",
                AllCollection.getInstance().getUserList().getUserArrayList().size());
        if (imageFile != null) {
            eventImageFormat = getFileExtension(imageFile);
            copyFile(imageFile);
        } else {
            eventImageFormat = "null";
        }
        setEventImage();
    }

    public Event(String eventId,
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
                 Date participantRegistrationStartTime,
                 Date participantRegistrationEndTime) {

        this.activityList = new ScheduleList();
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participantList = new ParticipantList();
        this.place = place;
        this.price = price;
        this.eventImageFormat = eventImageFormat;
        this.enableStaffRegistration = enableStaffRegistration;
        this.maxStaffTeams = maxStaffTeams;
        this.maxTeamMembers = maxTeamMembers;
        this.staffTeamList = new StaffTeamList();
        this.participatedRegistrationEndTime = participantRegistrationEndTime;
        this.participatedRegistrationStartTime = participantRegistrationStartTime;
        setEventImage();
    }


    public void addActivityToEvent(Schedule schedule) {
        activityList.addNewSchedule(schedule);
    }

    public void calculateSoldAndUnsold() {
        sold = participantList.getParticipantArrayList().size();
        if (maxParticipants != -1) {
            unSold = maxParticipants - sold;
        } else {
            unSold = -1;
        }
    }

    public int calculateStaffTeamLeft() {
        if (isEnableStaffRegistration()) {
            return maxStaffTeams - staffTeamList.getStaffTeamArrayList().size();
        }
        return 0;
    }

    public void changeEventImage(File fileImage) {
        if (fileImage == null) {
            eventImageFormat = "null";
        } else {
            eventImageFormat = getFileExtension(fileImage);
            copyFile(fileImage);
            setEventImage();
        }
    }

    private void copyFile(File file) {
        Path targetPath = Path.of("data/events/event-images");
        try {
            Files.createDirectories(targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.copy(file.toPath(), Path.of(targetPath + File.separator + eventId + "." + eventImageFormat), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAnyMaxTeamMembers() {
        return maxTeamMembers == -1;
    }

    public boolean isEnableStaffRegistration() {
        return enableStaffRegistration;
    }

    public boolean isEventId(String eventId) {
        return this.eventId.equals(eventId);
    }

    public boolean isEventName(String eventName) {return this.eventName.equals(eventName);}

    public boolean isFull() {
        int numberOfParticipants = participantList.getParticipantArrayList().size();
        if (maxParticipants == -1 || numberOfParticipants < maxParticipants) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isHost(String userId) {
        return hostUser.getUserId().equals(userId);
    }

    public boolean isNoLimitMaxParticipant() {
        return maxParticipants == -1;
    }

    public boolean isNoLitMaxStaffTeams() {
        return maxStaffTeams == -1;
    }

    public boolean isStaffTeamLimitReached() {
        if (!isNoLitMaxStaffTeams() && getStaffTeamList().getStaffTeamArrayList().size() >= maxStaffTeams) {
            return true;
        }
        return false;
    }

    public boolean removeParticipantById(String participantId) {
        Participant participantToRemove = participantList.findParticipantByParticipantId(participantId);

        if (participantToRemove != null) {
            boolean removed = participantList.getParticipantArrayList().remove(participantToRemove);

            if (removed) {
                calculateSoldAndUnsold();
            }
            return removed;
        }
        return false;
    }

    public boolean removeStaffTeamById(String staffTeamId) {
        StaffTeam staffTeamToRemove = staffTeamList.findStaffTeamById(staffTeamId);

        if (staffTeamToRemove != null) {
            boolean removed = staffTeamList.getStaffTeamArrayList().remove(staffTeamToRemove);

            if (removed) {
                calculateStaffTeamLeft();
            }
            return removed;
        }
        return false;
    }


    public ScheduleList getActivityList() {
        return activityList;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() { return description; }

    public Date getEndTime() {
        return endTime;
    }

    public String getEventId() {
        return eventId;
    }

    public Image getEventImage() {
        return eventImage;
    }

    public String getEventImageFormat() {
        return eventImageFormat;
    }

    public String getEventName() {
        return eventName;
    }

    private String getFileExtension(File file) {
        String path = file.getPath();
        String[] splitPath = path.split("\\.");
        String fileExtension = splitPath[splitPath.length - 1];
        return fileExtension;
    }

    public User getHostUser() {
        return hostUser;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getMaxStaffTeams() {
        return maxStaffTeams;
    }

    public int getMaxTeamMembers() {
        return maxTeamMembers;
    }

    public ParticipantList getParticipantList() {
        return participantList;
    }

    public String getPlace() {
        return place;
    }

    public double getPrice() {
        return price;
    }

    public int getSold() {
        return sold;
    }

    public StaffTeamList getStaffTeamList() {
        return staffTeamList;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getStatus() {
        if (TimeConversion.getNowDate().before(startTime) && TimeConversion.getNowDate().before(endTime)) {
            return "Soon";
        }
        else if (TimeConversion.getNowDate().after(endTime) && TimeConversion.getNowDate().after(startTime)){
            return "Ended";
        }
        else {
            return "On Going";
        }
    }

    public int getUnSold() {
        return unSold;
    }


    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnableStaffRegistration(boolean enableStaffRegistration) {
        this.enableStaffRegistration = enableStaffRegistration;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setEventImage() {
        if (eventImageFormat == null || eventImageFormat.equals("null")) {
            eventImage = DEFAULT_EVENT_IMAGE;
        } else {
            String filePath = "data/events/event-images/";
            String fileName = eventId + "." + eventImageFormat;
            File file = new File(filePath + fileName);
            eventImage = new Image(file.toURI().toString());
        }
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setHostUser(User hostUser) {
        this.hostUser = hostUser;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setMaxStaffTeams(int maxStaffTeams) {
        this.maxStaffTeams = maxStaffTeams;
    }

    public void setMaxTeamMembers(int maxTeamMembers) {
        this.maxTeamMembers = maxTeamMembers;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStaffTeamList(StaffTeamList staffTeamList) {
        this.staffTeamList = staffTeamList;
    }

    public void setStartTime(Date startDate) {
        this.startTime = startDate;
    }

    public Date getParticipatedRegistrationEndTime() {
        return participatedRegistrationEndTime;
    }

    public Date getParticipatedRegistrationStartTime() {
        return participatedRegistrationStartTime;
    }

    public void setParticipatedRegistrationStartTime(Date participatedRegistrationStartTime) {
        this.participatedRegistrationStartTime = participatedRegistrationStartTime;
    }

    public void setParticipatedRegistrationEndTime(Date participatedRegistrationEndTime) {
        this.participatedRegistrationEndTime = participatedRegistrationEndTime;
    }
}
