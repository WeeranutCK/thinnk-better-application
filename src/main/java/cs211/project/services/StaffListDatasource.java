package cs211.project.services;

import cs211.project.models.Staff;
import cs211.project.models.collections.StaffList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class StaffListDatasource implements Datasource<StaffList> {
    private String directoryName;
    private String fileName;

    public StaffListDatasource(String directoryName, String fileName) {
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
    public StaffList readData() {
        String filePath = directoryName + File.separator + fileName;
        StaffList staffList = new StaffList();

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
                try {
                    ArrayList<String> stringArrayList = StringAdjustment.splitToArrayList(line);
                    String staffId = stringArrayList.get(0).trim();
                    Date date = TimeConversion.getDate(stringArrayList.get(1).trim());

                    if (!staffId.isEmpty()) {
                        staffList.addStaff(date, staffId);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found");
        } catch (IOException e) {
            System.err.println("Error Read Line");
        }

        return staffList;
    }

    @Override
    public void writeData(StaffList data) {
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
            for (Staff staff : data.getStaffArrayList()) {
                String joinDate = TimeConversion.getFormattedDate(staff.getJoinDate());
                String line = staff.getStaffId() + "," +
                        joinDate;
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
