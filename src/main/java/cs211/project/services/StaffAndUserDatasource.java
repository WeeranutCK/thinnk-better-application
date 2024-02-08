package cs211.project.services;

import cs211.project.models.Staff;
import cs211.project.models.User;
import cs211.project.models.collections.StaffList;
import cs211.project.models.collections.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StaffAndUserDatasource implements DatasourcePair {
    private String directoryName;
    private String fileName;
    StaffList staffList;
    UserList userList;


    public StaffAndUserDatasource(String directoryName, String fileName, StaffList staffList, UserList userList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.staffList = staffList;
        this.userList = userList;
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
            String line = null;

            while ((line = buffer.readLine()) != null && !line.isEmpty()) {

                String[] data = line.split(",");

                Staff staffTemp = staffList.findStaffByStaffId(data[0]);
                User userTemp = userList.findUserById(data[1]);
                staffTemp.setUser(userTemp);
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
            for (Staff staff : AllCollection.getInstance().getStaffList().getStaffArrayList()) {
                if (staff.getUser() != null) {
                    String line = staff.getStaffId() + "," +
                            staff.getUser().getUserId();
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
