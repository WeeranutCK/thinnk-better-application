package cs211.project.services;

import cs211.project.models.User;
import cs211.project.models.collections.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserListDatasource implements Datasource<UserList> {
    private String directoryName;
    private String fileName;

    public UserListDatasource(String directoryName, String fileName) {
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
    public UserList readData() {
        UserList userList = new UserList();

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

                String profileName = stringArrayList.get(0);
                String username = stringArrayList.get(1);
                String userId = stringArrayList.get(2);
                String email = stringArrayList.get(3);
                String dateOfBirthString = stringArrayList.get(4);
                String password = stringArrayList.get(5);
                String imageFormat = stringArrayList.get(6);
                String bio = stringArrayList.get(7).equals("null") ? "" : StringAdjustment.replaceCodeToString(stringArrayList.get(7));
                String timeStampsRead = stringArrayList.get(8);

                Date dateOfBirth = null;
                try {
                    dateOfBirth = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateOfBirthString);
                } catch (ParseException e) {
                    System.err.println("Can Not Parse To Date : " + username + ", " + userId);
                }


                ArrayList<Date> timeStamps = new ArrayList<>();

                if (!timeStampsRead.equals("null")) {
                    ArrayList<String> timeStampStringArrayList = StringAdjustment.splitToArrayList(timeStampsRead);
                    for (String string: timeStampStringArrayList) {
                        timeStamps.add(TimeConversion.getDate(string));
                    }
                }

                userList.addNewUser(dateOfBirth, email, password, profileName, imageFormat,
                        userId, username, timeStamps, bio);
            }
        } catch (IOException e) {
            System.err.println("Error Read Line");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return userList;

    }

    @Override
    public void writeData(UserList data) {
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
            for (User user : data.getUserArrayList()) {
                String profileImageFormat = user.getProfileImageFormat() == null ? "null" : user.getProfileImageFormat();
                String userId = user.getUserId() == null ? "null" : user.getUserId();
                String email = user.getEmail() == null ? "null" : user.getEmail();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String dateOfBirth = user.getDateOfBirth() == null ? "null" : dateFormat.format(user.getDateOfBirth());

                String bio = user.getBio().isEmpty() ? "null" : StringAdjustment.replaceStringToCode(user.getBio());

                ArrayList<String> timeStamps = new ArrayList<>();
                if (user.getTimeStamps().size() == 0) {
                    timeStamps.add("null");
                } else {
                    for (Date date : user.getTimeStamps()) {
                        timeStamps.add(TimeConversion.getFormattedDate(date));
                    }
                }

                String line = StringAdjustment.replaceStringToCode(user.getProfileName()) + "," +
                        StringAdjustment.replaceStringToCode(user.getUsername()) + "," +
                        userId + "," +
                        email + "," +
                        dateOfBirth + "," +
                        user.getPassword() + "," +
                        profileImageFormat + "," +
                        bio + "," +
                        timeStamps;
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
