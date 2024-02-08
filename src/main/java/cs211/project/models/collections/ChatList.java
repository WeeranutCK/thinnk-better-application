package cs211.project.models.collections;

import cs211.project.models.Chat;
import cs211.project.models.StaffTeam;
import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;

import java.util.ArrayList;
import java.util.Date;

public class ChatList {
    private ArrayList<Chat> chatArrayList;


    public ChatList() {
        chatArrayList = new ArrayList<>();
    }


    public void addNewChat(String chatId, String userId, String staffTeamId, String message, Date time) {
        chatId = chatId.trim();
        userId = userId.trim();
        message = message.trim();
        if (!chatId.equals("")) {
            Chat exist = findChatById(chatId);
            if (exist == null) {
                chatArrayList.add(new Chat(chatId, userId, staffTeamId, message, time));
            }
        }
    }

    public void addNewChat(Chat chat) {
        chatArrayList.add(chat);
    }

    public void createNewChat(String userId, String staffTeamId, String message, Date time) {
        userId = userId.trim();
        message = message.trim();
        String newId = IdGenerator.generateId("chat", AllCollection.getInstance().getChatList().getChatArrayList().size()).trim();
        chatArrayList.add(new Chat(newId, userId, staffTeamId, message, time));
    }

    public Chat findChatById(String chatId) {
        for (Chat chat: chatArrayList) {
            if (chat.isChatId(chatId)) {
                return chat;
            }
        }
        return null;
    }

    public void removeChatHistoryOfStaffTeam(StaffTeam staffTeam) {
        ArrayList<Chat> chatsToRemove = new ArrayList<>();
        for (Chat chat : AllCollection.getInstance().getChatList().getChatArrayList()) {
            if (chat.getStaffTeamId().equals(staffTeam.getTeamId())) {
                chatsToRemove.add(chat);
            }
        }
        chatArrayList.removeAll(chatsToRemove);
    }


    public ArrayList<Chat> getChatArrayList() {
        return chatArrayList;
    }
}
