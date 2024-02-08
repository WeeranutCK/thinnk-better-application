package cs211.project.services;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


public class EventListDatasource implements Datasource<EventList> {
    private String directoryName;
    private String fileName;


    public EventListDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
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
    public EventList readData() {
        EventList eventArrayList = new EventList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader (
                fileInputStream,
                StandardCharsets.UTF_8
        );
        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line = "";

        try {
            while( (line = buffer.readLine()) != null ) {

                if (line.equals("")) continue;

                ArrayList<String> stringArrayList = StringAdjustment.splitToArrayList(line);

                String eventId = stringArrayList.get(0).trim();
                String eventName = StringAdjustment.replaceCodeToString(stringArrayList.get(1).trim());
                Date startTime = null;
                try {
                    startTime = TimeConversion.getDate(stringArrayList.get(2).trim());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Date endTime = null;
                try {
                    endTime = TimeConversion.getDate(stringArrayList.get(3).trim());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String category = stringArrayList.get(4).trim();
                int maxParticipants = Integer.parseInt(stringArrayList.get(5));
                String place = StringAdjustment.replaceCodeToString(stringArrayList.get(6).trim());
                double price = Double.parseDouble(stringArrayList.get(7));
                String imageFormat = stringArrayList.get(8).trim();
                boolean enableStaffRegistration = Boolean.parseBoolean(stringArrayList.get(9).trim());
                int maxStaffTeam = Integer.parseInt(stringArrayList.get(10).trim());
                int maxTeamMembers = Integer.parseInt(stringArrayList.get(11).trim());

                String description = stringArrayList.get(12).trim().equals("null")? "" : StringAdjustment.replaceCodeToString(stringArrayList.get(12));
                Date participatedRegistrationStartTime = null;
                Date participatedRegistrationEndTime = null;
                try {
                    participatedRegistrationStartTime = TimeConversion.getDate(stringArrayList.get(13).trim());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                try {
                    participatedRegistrationEndTime = TimeConversion.getDate(stringArrayList.get(14).trim());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                eventArrayList.addNewEvent(eventId, eventName, category, description, place,
                        startTime, endTime, maxParticipants, price,
                        imageFormat, enableStaffRegistration, maxStaffTeam,
                        maxTeamMembers, participatedRegistrationStartTime, participatedRegistrationEndTime);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return eventArrayList;
    }

    @Override
    public void writeData(EventList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter (
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            for (Event event: data.getEventArrayList()) {

                String description  = event.getDescription().isEmpty()? "null" : StringAdjustment.replaceStringToCode(event.getDescription());
                String place  = StringAdjustment.replaceStringToCode(event.getPlace());
                String name = StringAdjustment.replaceStringToCode(event.getEventName());

                String line = event.getEventId() + "," +
                        name + "," +
                        TimeConversion.getFormattedDate(event.getStartTime()) + "," +
                        TimeConversion.getFormattedDate(event.getEndTime()) + "," +
                        event.getCategory() + "," +
                        event.getMaxParticipants() + "," +
                        place + "," +
                        event.getPrice() + "," +
                        event.getEventImageFormat() + "," +
                        event.isEnableStaffRegistration() + "," +
                        event.getMaxStaffTeams() + "," +
                        event.getMaxTeamMembers() + "," +
                        description + "," +
                        TimeConversion.getFormattedDate(event.getParticipatedRegistrationStartTime()) + "," +
                        TimeConversion.getFormattedDate(event.getParticipatedRegistrationEndTime());
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