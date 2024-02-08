package cs211.project.controllers;

import cs211.project.models.Chat;
import cs211.project.models.collections.ChatList;
import cs211.project.models.collections.UserList;
import cs211.project.services.AllCollection;
import cs211.project.services.Loader;
import cs211.project.services.TimeConversion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatBlockController {
    @FXML
    private VBox chatComponentVBox;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private Button postButton;
    @FXML
    private ScrollPane scrollPane;


    private static final int CHAT_BATCH_SIZE = 5;
    private ChatList chatList;
    private int headChatIndex;
    private boolean isChatEnded;
    private String staffTeamId;
    private int tailChatIndex;
    private UserList userList;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        chatList = new ChatList();
        userList = AllCollection.getInstance().getUserList();
        isChatEnded = false;

        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                createChatMethod();
            } else if (event.getCode() == KeyCode.ENTER && event.isShiftDown()) {
                messageTextArea.appendText("\n");
            }
        });

        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (chatList.getChatArrayList().size() <= CHAT_BATCH_SIZE)
            {
                isChatEnded = true;
            }

            if (headChatIndex < 0) {
                isChatEnded = true;
                headChatIndex = -1;
            }

            if (newValue.doubleValue() <= 0.2 && !isChatEnded) {
                Platform.runLater(() -> scrollPane.setVvalue(0.21));
                loadChat(true);
            }
        });

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }


    @FXML
    private void onPostButtonClicked() {
        createChatMethod();
    }



    private void addChatComponents(List<Chat> chats) {
        for (Chat chat : chats) {
            addChatComponent(chat);
        }
    }

    private void addChatComponentsAtIndex(List<Chat> chats, int index) {
        for (Chat chat : chats) {
            addChatComponentAtIndex(chat, index);
        }
    }


    private void addChatComponent(Chat chat) {
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/chat.fxml")
            );
            VBox childVBox = componentLoader.load();

            ChatController chatController = componentLoader.getController();
            chatController.setChatDetails(chat, userList.findUserById(chat.getUserId()));

            chatComponentVBox.getChildren().add(childVBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addChatComponentAtIndex(Chat chat, int index) {
        try {
            FXMLLoader componentLoader = new FXMLLoader(
                    Loader.class.getResource("/cs211/project/components/chat.fxml")
            );
            VBox childVBox = componentLoader.load();

            ChatController chatController = componentLoader.getController();
            chatController.setChatDetails(chat, userList.findUserById(chat.getUserId()));

            chatComponentVBox.getChildren().add(index, childVBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearChat() {
        staffTeamId = null;
        chatComponentVBox.getChildren().clear();
        chatList.getChatArrayList().clear();
    }

    private void createChatMethod() {
        String message = messageTextArea.getText().trim();

        if (!message.isEmpty()) {
            chatList.createNewChat(
                    AllCollection.getInstance().getCurrentUser().getUserId(),
                    staffTeamId,
                    message,
                    TimeConversion.getNowDate());
            AllCollection.getInstance().getChatList().getChatArrayList()
                         .add(chatList.getChatArrayList().get(chatList.getChatArrayList().size() - 1));


            AllCollection.getInstance().writeChatData();
            messageTextArea.clear();

            Chat chat = chatList.getChatArrayList().get(chatList.getChatArrayList().size() - 1);
            try {
                FXMLLoader componentLoader = new FXMLLoader(
                        Loader.class.getResource("/cs211/project/components/chat.fxml")
                );
                VBox childVBox = componentLoader.load();

                ChatController chatController = componentLoader.getController();
                chatController.setChatDetails(chat, userList.findUserById(chat.getUserId()));

                chatComponentVBox.getChildren().add(childVBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    public void loadChat(boolean isPrevious) {
        if (staffTeamId != null) {
            if (!isPrevious) {
                int startIndex = tailChatIndex;
                int endIndex = Math.min(startIndex + CHAT_BATCH_SIZE - 1, chatList.getChatArrayList().size() - 1);

                List<Chat> newChats = new ArrayList<>();
                for (int i = startIndex; i <= endIndex; i++) {
                    newChats.add(chatList.getChatArrayList().get(i));
                }
                addChatComponents(newChats);

                tailChatIndex = endIndex;
            } else {
                if (headChatIndex >= 0) {
                    int endIndex = headChatIndex;
                    int startIndex = Math.max(endIndex - CHAT_BATCH_SIZE + 1, 0);

                    List<Chat> newChats = new ArrayList<>();
                    for (int i = endIndex; i >= startIndex; i--) {
                        newChats.add(chatList.getChatArrayList().get(i));
                    }
                    addChatComponentsAtIndex(newChats, 0);

                    headChatIndex = startIndex - 1;
                }
            }
        } else {
            clearChat();
        }
    }

    public void setStaffTeamId(String eventId) {
        this.staffTeamId = eventId;

        for (Chat chat : AllCollection.getInstance().getChatList().getChatArrayList()) {
            if (chat.getStaffTeamId().equals(staffTeamId)) {
                chatList.addNewChat(chat);
            }
        }

        headChatIndex = Math.max(chatList.getChatArrayList().size() - CHAT_BATCH_SIZE - 1, 0);
        tailChatIndex = Math.max(chatList.getChatArrayList().size() - CHAT_BATCH_SIZE, 0);
    }

    public void setEnableSendTextDisable(boolean b) {
        postButton.setDisable(b);
        messageTextArea.setDisable(b);
    }
}
