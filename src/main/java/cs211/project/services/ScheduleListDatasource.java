package cs211.project.services;

import cs211.project.models.Schedule;
import cs211.project.models.collections.ScheduleList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

public class ScheduleListDatasource implements Datasource<ScheduleList> {
    private String directoryName;
    private String fileName;


    public ScheduleListDatasource(String directoryName, String fileName) {
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
    public ScheduleList readData() {
        ScheduleList schedules = new ScheduleList();
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

                String activityId = data[0].trim().equals("null") ? null : data[0].trim();
                String activityName = data[1].trim().equals("null") ? null : StringAdjustment.replaceCodeToString(data[1].trim());
                String description = data[2].trim().equals("null") ? "" : StringAdjustment.replaceCodeToString(data[2].trim());
                Date startTime = TimeConversion.getDate(data[3].trim());
                Date endTime = TimeConversion.getDate(data[4].trim());

                schedules.addNewSchedule(activityName, activityId, description, startTime, endTime);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return schedules;
    }

    @Override
    public void writeData(ScheduleList data) {
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

            for (Schedule schedule: data.getScheduleArrayList()) {
                String description = schedule.getDescription().isEmpty() || schedule.getDescription() == null? "null" : schedule.getDescription();
                description = StringAdjustment.replaceStringToCode(description);
                String activityName = StringAdjustment.replaceStringToCode(schedule.getActivityName());
                String line = schedule.getActivityId() + "," +
                              activityName + "," +
                              description + "," +
                              TimeConversion.getFormattedDate(schedule.getStartTime()) + "," +
                              TimeConversion.getFormattedDate(schedule.getEndTime());

                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
