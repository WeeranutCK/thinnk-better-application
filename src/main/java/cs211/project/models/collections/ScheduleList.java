package cs211.project.models.collections;

import cs211.project.models.Event;
import cs211.project.models.Schedule;
import cs211.project.models.StaffTeam;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ScheduleList {
    private ArrayList<Schedule> scheduleArrayList;


    public ScheduleList() {
        scheduleArrayList = new ArrayList<>();
    }


    public void addNewSchedule(Schedule schedule) {
        scheduleArrayList.add(schedule);
    }

    public void addNewSchedule(String activityName, String activityId, String description, Date startTime, Date endTime) {
        activityName = activityName.trim();
        description = description.trim();
        if (!activityId.equals("")) {
            Schedule exist = findActivityById(activityId);
            if (exist == null) {
                scheduleArrayList.add(new Schedule(activityName, activityId, description, startTime, endTime));
            }
        }
    }

    public void createNewSchedule(String activityName, Event event, String description, Date startTime, Date endTime, StaffTeam staffTeam) {
        activityName = activityName.trim();
        description = description.trim();
        Schedule temp = new Schedule(activityName, event, description, startTime, endTime, staffTeam);
        scheduleArrayList.add(temp);
        event.getActivityList().getScheduleArrayList().add(temp);
        if (staffTeam != null) {
            staffTeam.setSchedule(temp);
        }
    }

    public Schedule findActivityById(String activityId) {
        for (Schedule schedule: scheduleArrayList) {
            if (schedule.isActivityId(activityId)) {
                return schedule;
            }
        }
        return null;
    }

    public Schedule findActivityByName(String activityName) {
        for (Schedule schedule: scheduleArrayList) {
            if (schedule.isActivityName(activityName)) {
                return schedule;
            }
        }
        return null;
    }

    public void removeSchedules(ArrayList<Schedule> scheduleList) {
        if (scheduleList != null) {
            Iterator<Schedule> iterator = scheduleArrayList.iterator();
            while (iterator.hasNext()) {
                Schedule schedule = iterator.next();
                if (scheduleList.contains(schedule)) {
                    iterator.remove();
                }
            }
        }
    }


    public ArrayList<Schedule> getScheduleArrayList() {
        return scheduleArrayList;
    }
}
