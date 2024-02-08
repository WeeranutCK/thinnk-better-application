package cs211.project.services;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ThemeDatasource implements DatasourcePair {
    private String directoryName;
    private String fileName;
    private UserList userList;

    public ThemeDatasource(String directoryName, String fileName, UserList userList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
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
            while ((line = buffer.readLine()) != null){
                ArrayList<String> stringArrayList = StringAdjustment.splitToArrayList(line);

                String userId = stringArrayList.get(0);
                String theme = stringArrayList.get(1);
                String fontFamily = stringArrayList.get(2);
                String fontSize = stringArrayList.get(3);

                User user = userList.findUserById(userId);
                if (user != null) {
                    user.getTheme().setCurrentTheme(theme);
                    user.getTheme().setCurrentFontSize(fontSize);
                    user.getTheme().setCurrentFontFamily(fontFamily);
                }
            }
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
            for (User user : AllCollection.getInstance().getUserList().getUserArrayList()) {
                String line = user.getUserId() + "," +
                              user.getTheme().getCurrentTheme() + "," +
                              user.getTheme().getCurrentFontFamily() + "," +
                              user.getTheme().getCurrentFontSize();
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
