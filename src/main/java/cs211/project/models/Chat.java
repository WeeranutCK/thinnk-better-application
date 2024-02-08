package cs211.project.models;

import java.util.Date;

public class Chat {
    private String chatId;
    private String message;
    private String staffTeamId;
    private Date time;
    private String userId;


    public Chat(String chatId, String userId, String staffTeamId, String message, Date time) {
        this.chatId = chatId;
        this.userId = userId;
        this.staffTeamId = staffTeamId;
        this.message = message;
        this.time = time;
    }


    public boolean isChatId(String chatId) {
        return this.chatId.equals(chatId);
    }


    public String getChatId() {
        return chatId;
    }

    public String getStaffTeamId() {
        return staffTeamId;
    }

    public String getMessage() {
        return message;
    }

    public Date getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
