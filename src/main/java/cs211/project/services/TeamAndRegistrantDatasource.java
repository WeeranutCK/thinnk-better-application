package cs211.project.services;

import cs211.project.models.StaffTeam;
import cs211.project.models.User;
import cs211.project.models.collections.StaffTeamList;
import cs211.project.models.collections.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class TeamAndRegistrantDatasource implements DatasourcePair {

    private String directoryName;
    private String fileName;
    private StaffTeamList staffTeamList;
    private UserList userList;

    public TeamAndRegistrantDatasource(String directoryName, String fileName, StaffTeamList staffTeamList, UserList userList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.userList = userList;
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

                StaffTeam staffTeam = staffTeamList.findStaffTeamById(data[0]);
                User registrant = userList.findUserById(data[1]);

                staffTeam.regisTeam(registrant);

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
            String line = "";
            for (StaffTeam staffTeam :staffTeamList.getStaffTeamArrayList()){
                for (User registrant : staffTeam.getRegistrants().getUserArrayList()) {
                    line = staffTeam.getTeamId() + ',' + registrant.getUserId();

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
