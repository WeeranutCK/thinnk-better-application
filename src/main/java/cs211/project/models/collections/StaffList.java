package cs211.project.models.collections;

import cs211.project.models.Event;
import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.User;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Date;

public class StaffList {
    private SimpleBooleanProperty selectedProperty = new SimpleBooleanProperty(false);

    private ArrayList<Staff> staffArrayList;

    public StaffList() {
        staffArrayList = new ArrayList<>();
    }

    public void addStaff(Date joinDate, String staffId) {
        staffId = staffId.trim();

        if (!staffId.isEmpty() && findStaffByStaffId(staffId) == null) {
            staffArrayList.add(new Staff(joinDate, staffId));
        }
    }

    public void createNewStaffJoinTeam(Event event, StaffTeam staffTeam, User user) {
        boolean isNotStaffEvent = event.getStaffTeamList().findTeamsOfUser(user).getNumberOfTeams() == 0;
        boolean isMultiTeamAllowed = isNotStaffEvent ? true : event.getStaffTeamList().
                                                             findTeamsOfUser(user).
                                                             getStaffTeamArrayList().get(0).getIsMultiTeamAllowed();
        if (isNotStaffEvent || isMultiTeamAllowed) {
            Staff temp = new Staff(staffTeam, user);
            staffArrayList.add(temp);
            staffTeam.getStaffList().getStaffArrayList().add(temp);
            if (!user.getStaffParticipatedEvent().getEventArrayList().contains(event)){
                user.getStaffParticipatedEvent().getEventArrayList().add(event);
            }
        }
    }

    public Staff findStaffByStaffId(String staffId) {
        for (Staff staff : staffArrayList) {
            if(staff.isStaffId(staffId)) {
                return staff;
            }
        }
        return null;
    }

    public Staff findStaffInTeamByUserId(String userId) {
        for (Staff staff : staffArrayList) {
            if (staff.isStaffByUserId(userId)) {
                return staff;
            }
        }
        return null;
    }

    public ArrayList<Staff> getStaffArrayList() {
        return staffArrayList;
    }
}
