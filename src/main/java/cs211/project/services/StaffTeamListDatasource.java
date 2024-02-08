package cs211.project.services;

import cs211.project.models.StaffTeam;
import cs211.project.models.collections.StaffTeamList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class StaffTeamListDatasource implements Datasource<StaffTeamList> {
    private String directoryName;
    private String fileName;


    public StaffTeamListDatasource(String directoryName, String fileName) {
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
    public StaffTeamList readData() {

        StaffTeamList staffTeamList = new StaffTeamList();
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

                ArrayList<String> stringArrayList = StringAdjustment.splitToArrayList(line);

                String teamId = stringArrayList.get(0).trim();
                String teamName = StringAdjustment.replaceCodeToString(stringArrayList.get(1).trim());
                int maxMembers = Integer.parseInt(stringArrayList.get(2));

                Date createDate = TimeConversion.getDate(stringArrayList.get(3).trim());
                Date startTime = TimeConversion.getDate(stringArrayList.get(4).trim());
                Date endTime = TimeConversion.getDate(stringArrayList.get(5).trim());
                boolean isTeamAutoAccepting = Boolean.parseBoolean(stringArrayList.get(6).trim());
                boolean isMultiTeamAllowed = Boolean.parseBoolean(stringArrayList.get(7).trim());

                StaffTeam staffTeam = new StaffTeam(createDate, maxMembers, teamId, teamName, startTime, endTime, isTeamAutoAccepting, isMultiTeamAllowed);
                staffTeamList.addStaffTeam(staffTeam);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error Read Line: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error Parsing Number: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return staffTeamList;
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
                String teamId = staffTeam.getTeamId();
                String teamName = StringAdjustment.replaceStringToCode(staffTeam.getTeamName());
                String createDate = TimeConversion.getFormattedDate(staffTeam.getCreateDate());
                String line = teamId + "," +
                        teamName + "," +
                        staffTeam.getMaxMembers() + "," +
                        createDate + "," +
                        TimeConversion.getFormattedDate(staffTeam.getStartTimeRegistration()) + "," +
                        TimeConversion.getFormattedDate(staffTeam.getEndTimeRegistration()) + "," +
                        staffTeam.getIsTeamAutoAccepting() + "," +
                        staffTeam.getIsMultiTeamAllowed();
                buffer.append(line);
                buffer.append("\n");
            }
            buffer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}