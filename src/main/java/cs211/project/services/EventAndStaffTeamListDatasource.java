package cs211.project.services;

import cs211.project.models.Event;
import cs211.project.models.StaffTeam;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.StaffTeamList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EventAndStaffTeamListDatasource implements Datasource<StaffTeamList> {

    private String directoryName;
    private String fileName;
    private EventList eventList;
    private StaffTeamList staffTeamList;

    public EventAndStaffTeamListDatasource(String directoryName, String fileName, EventList eventList, StaffTeamList staffTeamList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.eventList = eventList;
        this.staffTeamList = staffTeamList;
        checkFileIsExisted();
    }

    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = directoryName + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public StaffTeamList readData() {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );

        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line = "";
        try {
            while ((line = buffer.readLine()) != null) {
                if (line.equals("")) continue;

                String[] data = line.split(",");

                StaffTeam staffTeam = staffTeamList.findStaffTeamById(data[0]);
                Event event = eventList.findEventById(data[1]);

                staffTeam.setEvent(event);
                StaffTeamList list = event.getStaffTeamList();

                if (list == null) {
                    list = new StaffTeamList();
                }

                event.setStaffTeamList(list);
                event.getStaffTeamList().getStaffTeamArrayList().add(staffTeam);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void writeData(StaffTeamList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            for (StaffTeam staffTeam : data.getStaffTeamArrayList()) {
                String line = staffTeam.getTeamId() + "," +
                        staffTeam.getEvent().getEventId();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
