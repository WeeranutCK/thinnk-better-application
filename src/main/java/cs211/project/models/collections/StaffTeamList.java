package cs211.project.models.collections;

import cs211.project.models.Event;
import cs211.project.models.StaffTeam;
import cs211.project.models.User;

import java.util.ArrayList;
import java.util.Date;

public class StaffTeamList {
    private ArrayList<StaffTeam> staffTeamArrayList;

    public StaffTeamList() {
        staffTeamArrayList = new ArrayList<>();
    }

    public void addStaffTeam(StaffTeam staffTeam) {
        staffTeamArrayList.add(staffTeam);
    }

    public void createNewStaffTeam(String teamName, int maxMembers, Event event, Date startTimeRegistration, Date endTimeRegistration, boolean isTeamAutoAccepting, boolean isMultiTeamAllowed) {
        teamName = teamName.trim();
        StaffTeam staffTeam = new StaffTeam(teamName, maxMembers, event, startTimeRegistration, endTimeRegistration, isTeamAutoAccepting, isMultiTeamAllowed);
        staffTeamArrayList.add(staffTeam);
        event.getStaffTeamList().staffTeamArrayList.add(staffTeam);
    }

    public StaffTeam findStaffTeamById(String staffTeamId) {
        for (StaffTeam staffTeam : staffTeamArrayList) {
            if (staffTeam.isStaffTeamId(staffTeamId)) {
                return staffTeam;
            }
        }
        return null;
    }

    public StaffTeam findStaffTeamByName(String teamName) {
        for (StaffTeam staffTeam : staffTeamArrayList) {
            if (staffTeam.isStaffTeamName(teamName)) {
                return staffTeam;
            }
        }
        return null;
    }

    public StaffTeamList findTeamsOfUser(User user) {
        StaffTeamList staffTeamList = new StaffTeamList();
        for (StaffTeam staffTeam : staffTeamArrayList) {
            if (staffTeam.isStaffInTeam(user.getUserId()) != null) {
                staffTeamList.addStaffTeam(staffTeam);
            }
        }
        return staffTeamList;
    }

    public int getNumberOfTeams() {
        return staffTeamArrayList.size();
    }

    public boolean removeStaffTeam(StaffTeam staffTeam) {
        if(staffTeamArrayList.contains(staffTeam)) {
            staffTeamArrayList.remove(staffTeam);
            return true;
        }
        return false;
    }

    public ArrayList<StaffTeam> getStaffTeamArrayList() {
        return staffTeamArrayList;
    }
}
