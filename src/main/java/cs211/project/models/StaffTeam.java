package cs211.project.models;

import cs211.project.models.collections.EventList;
import cs211.project.models.collections.StaffList;
import cs211.project.models.collections.UserList;
import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;
import cs211.project.services.TimeConversion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class StaffTeam {
    private Date createDate;
    private Date endTimeRegistration;
    private Event event;
    private Staff headTeam;
    private int maxMembers;
    private Schedule schedule;
    private String teamId;
    private String teamName;
    private StaffList staffList;
    private Date startTimeRegistration;
    private BooleanProperty isSelected;
    private UserList registrants;
    private boolean isTeamAutoAccepting;
    private boolean isMultiTeamAllowed;

    public StaffTeam(String teamId, String teamName, Event event, Date startTimeRegistration, Date endTimeRegistration, boolean isTeamAutoAccepting, boolean isMultiTeamAllowed) {
        this.startTimeRegistration = startTimeRegistration;
        this.endTimeRegistration = endTimeRegistration;
        this.teamId = teamId;
        this.teamName = teamName;
        this.event = event;
        createDate = TimeConversion.getNowDate();
        this.schedule = null;
        staffList = new StaffList();
        isSelected = new SimpleBooleanProperty(false);
        registrants = new UserList();
        this.isTeamAutoAccepting = isTeamAutoAccepting;
        this.isMultiTeamAllowed = isMultiTeamAllowed;
    }

    public StaffTeam(String teamName, int maxMembers, Event event, Date startTimeRegistration, Date endTimeRegistration, boolean isTeamAutoAccepting, boolean isMultiTeamAllowed) {
        this.startTimeRegistration = startTimeRegistration;
        this.endTimeRegistration = endTimeRegistration;
        this.teamName = teamName;
        this.maxMembers = maxMembers;
        this.event = event;
        this.isTeamAutoAccepting = isTeamAutoAccepting;
        this.isMultiTeamAllowed = isMultiTeamAllowed;
        teamId = IdGenerator.generateId("staff-team",
                AllCollection.getInstance().getStaffTeamList().getStaffTeamArrayList().size());
        createDate = TimeConversion.getNowDate();
        schedule = null;
        staffList = new StaffList();
        isSelected = new SimpleBooleanProperty(false);
        registrants = new UserList();
    }

    public StaffTeam(String teamName, int maxMembers, Event event, StaffList staffList, Date startTimeRegistration, Date endTimeRegistration, boolean isTeamAutoAccepting, boolean isMultiTeamAllowed) {
        this.startTimeRegistration = startTimeRegistration;
        this.endTimeRegistration = endTimeRegistration;
        this.teamName = teamName;
        this.maxMembers = maxMembers;
        this.event = event;
        this.isTeamAutoAccepting = isTeamAutoAccepting;
        this.isMultiTeamAllowed = isMultiTeamAllowed;
        teamId = IdGenerator.generateId("staff-team",
                AllCollection.getInstance().getStaffTeamList().getStaffTeamArrayList().size());
        createDate = TimeConversion.getNowDate();
        schedule = null;
        this.staffList = staffList;
        isSelected = new SimpleBooleanProperty(false);
        registrants = new UserList();
    }

    public StaffTeam(Date createDate, int maxMembers ,String teamId, String teamName, Date startTimeRegistration, Date endTimeRegistration, boolean isTeamAutoAccepting, boolean isMultiTeamAllowed) {
        this.startTimeRegistration = startTimeRegistration;
        this.endTimeRegistration = endTimeRegistration;
        this.createDate = createDate;
        this.maxMembers = maxMembers;
        this.teamId = teamId;
        this.teamName = teamName;
        this.isTeamAutoAccepting = isTeamAutoAccepting;
        this.isMultiTeamAllowed = isMultiTeamAllowed;
        staffList = new StaffList();
        EventList eventList = new EventList();
        isSelected = new SimpleBooleanProperty(false);
        registrants = new UserList();
    }


    public boolean isClose() {
        if (TimeConversion.getNowDate().before(endTimeRegistration)) {
            return false;
        }
        else return true;
    }

    public boolean isFull() {
        if (getAmountTeamMember() < maxMembers) {
            return false;
        }
        return true;
    }

    public String statusRegistration() {
        if (TimeConversion.getNowDate().before(startTimeRegistration)) {
            return "Not Open";
        } else if (isFull()) {
            return "Full";
        } else if (TimeConversion.getNowDate().after(endTimeRegistration)) {
            return "Close";
        } else {
            return "Available";
        }
    }

    public static boolean deleteStaffTeam(ArrayList<StaffTeam> staffTeamArrayList, StaffTeam staffTeamToDelete) {
        if (staffTeamArrayList != null) {
            return staffTeamArrayList.remove(staffTeamToDelete);
        } else {
            return false;
        }
    }

    public boolean isStaffTeamName(String name) {
        return teamName.equals(name);
    }

    public boolean isHeadTeam(Staff staff) {
        if (headTeam != null) {
            return headTeam.equals(staff);
        }
        return false;
    }

    public boolean isHeadTeam(User user) {
        if (headTeam != null && headTeam.getUser() != null) {
            return headTeam.getUser().equals(user);
        }
        return false;
    }

    public boolean isSelected() {
        return isSelected.get();
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public Staff isStaffInTeam(String userId) {
        return staffList.findStaffInTeamByUserId(userId);
    }

    public void kickMembers(ArrayList<Staff> staffArrayList) {
        if (staffArrayList != null) {
            Iterator<Staff> iterator = staffList.getStaffArrayList().iterator();
            while (iterator.hasNext()) {
                Staff staff = iterator.next();
                if (staffArrayList.contains(staff)) {
                    if (isHeadTeam(staff)) {
                        headTeam = null;
                    }
                    iterator.remove();
                    if (event.getStaffTeamList().findTeamsOfUser(staff.getUser()).getNumberOfTeams() == 0) {
                        staff.getUser().getStaffParticipatedEvent().getEventArrayList().remove(staff.getStaffTeam().event);
                    }
                }
            }
        }
    }

    public void regisTeam(User user) {
        if (user != null) {
            registrants.getUserArrayList().add(user);
        }
    }

    public void deleteRegistrant(User user) {
        registrants.getUserArrayList().remove(user);
    }

    public boolean isStaffTeamId(String staffTeamId) {
        return this.teamId.equals(staffTeamId);
    }

    public int findAvailableTeamSpots() {
        return maxMembers - staffList.getStaffArrayList().size();
    }
    public int getAmountTeamMember() {
        return staffList.getStaffArrayList().size();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getEndTimeRegistration() {
        return endTimeRegistration;
    }

    public Event getEvent() {
        return event;
    }

    public Staff getHeadTeam() {
        return headTeam;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public StaffList getStaffList() {
        return staffList;
    }

    public Date getStartTimeRegistration() {
        return startTimeRegistration;
    }

    public UserList getRegistrants() {
        return registrants;
    }

    public boolean getIsTeamAutoAccepting() {
        return isTeamAutoAccepting;
    }

    public boolean getIsMultiTeamAllowed() {
        return isMultiTeamAllowed;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setEndTimeRegistration(Date endTimeRegistration) {
        this.endTimeRegistration = endTimeRegistration;
    }

    public void setStartTimeRegistration(Date startTimeRegistration) {
        this.startTimeRegistration = startTimeRegistration;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public void setHeadTeam(Staff headTeam) {
        this.headTeam = headTeam;
    }

    public void setTeamAutoAccepting(boolean teamAutoAccepting) {
        isTeamAutoAccepting = teamAutoAccepting;
    }

    public void setMultiTeamAllowed(boolean multiTeamAllowed) {
        isMultiTeamAllowed = multiTeamAllowed;
    }
}
