package cs211.project.models;

import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;

import java.util.Calendar;
import java.util.Date;

public class Schedule implements Comparable {
    private String activityId;
    private String activityName;
    private String description;
    private Date endTime;
    private Event event;
    private StaffTeam staffTeam;
    private Date startTime;


    public Schedule(String activityName, String activityId, String description, Date startTime, Date endTime) {
        this.activityName = activityName;
        this.activityId = activityId;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(String activityName, Event event, String description, Date startTime, Date endTime, StaffTeam staffTeam) {
        this.activityName = activityName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.event = event;
        this.staffTeam = staffTeam;
        activityId = IdGenerator.generateId("schedule", AllCollection.getInstance().getScheduleList().getScheduleArrayList().size()).trim();
    }


    @Override
    public int compareTo(Object object) {
        Schedule schedule = (Schedule) object;
        if (startTime.getTime() >= schedule.getStartTime().getTime()) {
            return 1;
        } else if (startTime.getTime() == schedule.getStartTime().getTime()) {
            return 0;
        }
        return -1;
    }

    public String getStartDayString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        int dayOfWeekValue = calendar.get(Calendar.DAY_OF_WEEK);

        return switch (dayOfWeekValue) {
            case Calendar.SUNDAY -> "SUNDAY";
            case Calendar.MONDAY -> "MONDAY";
            case Calendar.TUESDAY -> "TUESDAY";
            case Calendar.WEDNESDAY -> "WEDNESDAY";
            case Calendar.THURSDAY -> "THURSDAY";
            case Calendar.FRIDAY -> "FRIDAY";
            case Calendar.SATURDAY -> "SATURDAY";
            default -> throw new IllegalStateException("Invalid day of the week value!");
        };
    }

    public boolean isActivityId(String activityId) {
        return this.activityId.equals(activityId);
    }

    public boolean isActivityName(String activityName) {
        return this.activityName.equals(activityName);
    }


    public String getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Event getEvent() {
        return event;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public StaffTeam getStaffTeam() {
        return staffTeam;
    }


    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setStaffTeam(StaffTeam staffTeam) {
        this.staffTeam = staffTeam;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
