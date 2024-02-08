package cs211.project.services;

import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.collections.StaffList;
import cs211.project.models.collections.StaffTeamList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TeamAndHeadTeamDatasource implements DatasourcePair{
    private String directoryName;
    private String fileName;
    StaffList staffList;
    StaffTeamList staffTeamList;


    public TeamAndHeadTeamDatasource(String directoryName, String fileName, StaffTeamList staffTeamList, StaffList staffList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.staffTeamList = staffTeamList;
        this.staffList = staffList;
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
    public void readData() {
        String filePath = directoryName + File.separator + fileName;

        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream,
                    StandardCharsets.UTF_8
            );

            BufferedReader buffer = new BufferedReader(inputStreamReader);
            String line;

            while ((line = buffer.readLine()) != null && !line.isEmpty()) {

                String[] data = line.split(",");

                StaffTeam staffTeamTemp = staffTeamList.findStaffTeamById(data[0]);
                Staff staffTemp = staffList.findStaffByStaffId(data[1]);

                staffTeamTemp.setHeadTeam(staffTemp);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found");
        } catch (IOException e) {
            System.err.println("Error Read Line");
        }
    }

    @Override
    public void writeData() {
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
            for (StaffTeam staffTeam : staffTeamList.getStaffTeamArrayList()) {
                if (staffTeam.getHeadTeam() != null) {
                    String line = staffTeam.getTeamId() + "," +
                            staffTeam.getHeadTeam().getStaffId();
                    buffer.append(line);
                    buffer.append("\n");
                }
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
