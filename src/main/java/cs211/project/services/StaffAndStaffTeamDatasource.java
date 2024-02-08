package cs211.project.services;

import cs211.project.models.Staff;
import cs211.project.models.StaffTeam;
import cs211.project.models.collections.StaffList;
import cs211.project.models.collections.StaffTeamList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StaffAndStaffTeamDatasource implements DatasourcePair {
    String directoryName;
    String fileName;
    StaffList staffList;
    StaffTeamList staffTeamList;


    public StaffAndStaffTeamDatasource(String directoryName, String fileName, StaffList staffList, StaffTeamList staffTeamList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.staffList = staffList;
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
    public void readData() {
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

                Staff staffTemp = staffList.findStaffByStaffId(data[0]);
                StaffTeam staffTeamTemp = staffTeamList.findStaffTeamById(data[1]);
                staffTemp.setStaffTeam(staffTeamTemp);
                staffTeamTemp.getStaffList().getStaffArrayList().add(staffTemp);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            for (Staff staff : staffList.getStaffArrayList()) {
                String line = staff.getStaffId() + "," +
                              staff.getStaffTeam().getTeamId();
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
