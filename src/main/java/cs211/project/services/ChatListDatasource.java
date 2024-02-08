package cs211.project.services;

import cs211.project.models.Chat;
import cs211.project.models.collections.ChatList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

public class ChatListDatasource implements Datasource<ChatList> {
    private String directoryName;
    private String fileName;


    public ChatListDatasource(String directoryName, String fileName) {
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
    public ChatList readData() {
        ChatList chats = new ChatList();
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

                String[] data = line.split(",", 5);

                String chatId = data[0].trim().equals("null") ? null : data[0].trim();
                String userId = data[1].trim().equals("null") ? null : data[1].trim();
                String staffTeamId = data[2].trim().equals("null") ? null : data[2].trim();
                Date time = TimeConversion.getDate(data[3].trim());
                String message = data[4].trim().equals("null") ? null : StringAdjustment.replaceCodeToString(data[4].trim());

                chats.addNewChat(chatId, userId, staffTeamId, message, time);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return chats;
    }

    @Override
    public void writeData(ChatList data) {
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
            for (Chat chat: data.getChatArrayList()) {
                chat.getMessage().split(" ");

                String line = chat.getChatId() + "," +
                              chat.getUserId() + "," +
                              chat.getStaffTeamId() + "," +
                              TimeConversion.getFormattedDate(chat.getTime()) + "," +
                              StringAdjustment.replaceStringToCode(chat.getMessage());

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
