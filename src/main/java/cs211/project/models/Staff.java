package cs211.project.models;

import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;
import cs211.project.services.TimeConversion;

import java.util.Date;

public class Staff {
    private Date joinDate;
    private String staffId;
    private StaffTeam staffTeam;
    private User user;


    public Staff(StaffTeam staffTeam, User user) {
        this.staffTeam = staffTeam;
        this.user = user;

        joinDate = TimeConversion.getNowDate();
        staffId = IdGenerator.generateId("staff",
                  AllCollection.getInstance().getStaffList().getStaffArrayList().size());
    }

    public Staff(Date joinDate, String staffId) {
        this.joinDate = joinDate;
        this.staffId = staffId;
    }


    public boolean isStaffId(String staffId) {
        return this.staffId.equals(staffId);
    }

    public boolean isStaffByUserId(String userId) {
        return this.user.userId.equals(userId);
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public StaffTeam getStaffTeam() {
        return staffTeam;
    }

    public User getUser() {
        return user;
    }


    public void setStaffTeam(StaffTeam staffTeam) {
        this.staffTeam = staffTeam;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
