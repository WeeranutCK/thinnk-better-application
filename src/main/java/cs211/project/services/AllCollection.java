package cs211.project.services;

import cs211.project.models.User;
import cs211.project.models.collections.*;

public class AllCollection {
    private static AllCollection instance = null;
    private ChatList chatList;
    private ChatListDatasource chatListDatasource;
    private EventList eventList;
    private EventListDatasource eventListDatasource;
    private ParticipantAndEventDatasource participantAndEventDatasource;
    private EventAndStaffTeamListDatasource eventAndStaffTeamDatasource;
    private UserAndParticipantedEventListDatasource userAndParticipantedEventListDatasource;
    private ParticipantList participantList;
    private ParticipantListDatasource participantListDatasource;
    private ScheduleList scheduleList;
    private ScheduleAndEventDatasource scheduleAndEventDatasource;
    private ScheduleAndStaffTeamDatasource scheduleAndStaffTeamDatasource;
    private ScheduleListDatasource scheduleListDatasource;
    private StaffAndStaffTeamDatasource staffAndStaffTeamDatasource;
    private StaffAndUserDatasource staffAndUserDatasource;
    private StaffList staffList;
    private StaffListDatasource staffListDatasource;
    private StaffTeamList staffTeamList;
    private StaffTeamListDatasource staffTeamListDatasource;
    private UserList userList;
    private UserListDatasource userListDatasource;
    private User currentUser = null;
    private ParticipantAndUserDatasource participantAndUserDatasource;
    private UserAndStaffParticipatedEventDatasource userAndStaffParticipatedEventDatasource;
    private EventAndHostUserDatasource eventAndHostUserDatasource;
    private WalletList walletList;
    private WalletListDatasource walletListDatasource;
    private UserAndWalletListDatasource userAndWalletListDatasource;
    private TeamAndHeadTeamDatasource teamAndHeadTeamDatasource;
    private String currentPage = null;
    private ThemeDatasource themeDatasource;
    private TeamAndRegistrantDatasource teamAndRegistrantDatasource;

    private AllCollection() {
        readAllData();
    }

    public static AllCollection getInstance() {
        if (instance == null) {
            instance = new AllCollection();
        }
        return instance;
    }

    public void logOutCurrentUSer() {
        currentUser = null;
    }

    private void readAllData() {
        readUserListData();
        readStaffListData();
        readParticipantListData();
        readStaffTeamListData();

        readStaffAndStaffTeamDatasource();
        readScheduleListData();
        readEventListData();
        readParticipantAndEventData();
        readScheduleAndEventDatasource();
        readScheduleAndStaffTeamDatasource();
        readChatListData();
        readStaffAndUserDatasource();
        readEventAndStaffTeamData();
        readParticipantAndUserData();
        readUserAndParticipatedEventData();
        readUserAndStaffParticipatedEventData();
        readEventAndHostUserData();
        readWalletData();
        readUserAndWalletData();
        readTeamAndHeadTeamData();
        readThemeData();
        readTeamAndRegistrantData();
    }

    private void readUserListData() {
        UserListDatasource datasource = new UserListDatasource(
                "data/users", "users.csv");
        userListDatasource = datasource;
        userList = datasource.readData();
    }

    private void readScheduleListData() {
        ScheduleListDatasource datasource = new ScheduleListDatasource(
                "data/schedules", "schedules.csv");
        scheduleListDatasource = datasource;
        scheduleList = datasource.readData();
    }

    private void readScheduleAndEventDatasource() {
        scheduleAndEventDatasource =
                new ScheduleAndEventDatasource(
                        "data/relations",
                        "schedule-and-event.csv",
                        scheduleList,
                        eventList);
        scheduleAndEventDatasource.readData();
    }

    private void readScheduleAndStaffTeamDatasource() {
        scheduleAndStaffTeamDatasource =
                new ScheduleAndStaffTeamDatasource(
                        "data/relations",
                        "schedule-and-staff-team.csv",
                        scheduleList,
                        staffTeamList);
        scheduleAndStaffTeamDatasource.readData();
    }

    private void readEventListData() {
        EventListDatasource datasource = new EventListDatasource(
                "data/events", "events.csv");
        eventListDatasource = datasource;
        eventList = datasource.readData();
    }

    private void readStaffListData() {
        StaffListDatasource datasource = new StaffListDatasource("data/staff", "staff.csv");
        staffListDatasource = datasource;
        staffList = datasource.readData();
    }

    private void readStaffAndStaffTeamDatasource() {
        staffAndStaffTeamDatasource =
                new StaffAndStaffTeamDatasource(
                        "data/relations",
                            "staff-and-staff-team.csv",
                                    staffList,
                                    staffTeamList);
        staffAndStaffTeamDatasource.readData();
    }

    private void readStaffAndUserDatasource() {
        staffAndUserDatasource = new StaffAndUserDatasource(
                        "data/relations",
                        "staff-and-user.csv",
                        staffList,
                        userList);
        staffAndUserDatasource.readData();
    }

    private void readChatListData() {
        ChatListDatasource datasource = new ChatListDatasource(
                "data/chats", "chats.csv");
        chatListDatasource = datasource;
        chatList = datasource.readData();
    }

    private void readStaffTeamListData() {
        StaffTeamListDatasource datasource = new StaffTeamListDatasource(
                "data/staff-teams", "staff-teams.csv");
        staffTeamListDatasource = datasource;
        staffTeamList = datasource.readData();
    }

    private void readTeamAndHeadTeamData() {
        teamAndHeadTeamDatasource =
                new TeamAndHeadTeamDatasource("data/relations",
                                                  "team-and-head-team.csv",
                                                           staffTeamList,
                                                           staffList);
        teamAndHeadTeamDatasource.readData();
    }

    private void readTeamAndRegistrantData() {
        teamAndRegistrantDatasource =
                new TeamAndRegistrantDatasource("data/relations",
                        "team-and-registrant.csv",
                        staffTeamList,
                        userList);
        teamAndRegistrantDatasource.readData();
    }

    private void readParticipantAndEventData() {
        participantAndEventDatasource =
                new ParticipantAndEventDatasource("data/relations",
                                            "participant-and-event.csv",
                                                    participantList,
                                                    eventList);
        participantAndEventDatasource.readData();
    }

    private void readEventAndStaffTeamData(){
        eventAndStaffTeamDatasource =
                new EventAndStaffTeamListDatasource("data/relations",
                        "event-and-staff-team.csv",
                        eventList,
                        staffTeamList);
        eventAndStaffTeamDatasource.readData();
    }

    private void readParticipantListData() {
        ParticipantListDatasource datasource = new ParticipantListDatasource(
                "data/participants", "participants.csv");
        participantListDatasource = datasource;
        participantList = datasource.readData();
    }

    private void readParticipantAndUserData() {
        participantAndUserDatasource = new ParticipantAndUserDatasource(
                "data/relations", "participant-and-user.csv", participantList, userList);
        participantAndUserDatasource.readData();
    }

    private void readUserAndParticipatedEventData() {
        userAndParticipantedEventListDatasource = new UserAndParticipantedEventListDatasource(
                "data/relations", "user-and-participated-event-list.csv", eventList, userList);
        userAndParticipantedEventListDatasource.readData();
    }

    private void readEventAndHostUserData() {
        eventAndHostUserDatasource = new EventAndHostUserDatasource(
                "data/relations", "event-and-host.csv", eventList, userList);
        eventAndHostUserDatasource.readData();
    }

    private void readUserAndStaffParticipatedEventData() {
        userAndStaffParticipatedEventDatasource  = new UserAndStaffParticipatedEventDatasource(
                "data/relations", "user-and-staff-participated-event-list.csv", userList, eventList);
        userAndStaffParticipatedEventDatasource.readData();
    }

    private void readWalletData() {
        WalletListDatasource datasource = new WalletListDatasource(
                "data/wallet", "wallet.csv");
        walletListDatasource = datasource;
        walletList = datasource.readData();
    }

    private void readUserAndWalletData() {
        userAndWalletListDatasource = new UserAndWalletListDatasource(
                "data/relations", "user-and-wallet.csv", userList, walletList);
        userAndWalletListDatasource.readData();
    }

    private void readThemeData() {
        themeDatasource = new ThemeDatasource(
                "data/relations", "user-and-theme.csv", userList);
        themeDatasource.readData();
    }

    public void writeAllData() {
        writeChatData();
        writeUser();
        writeEventData();
        writeParticipantData();
        writeStaffData();
        writeScheduleData();
        writeStaffTeamData();
        writeStaffAndUserData();
        writeParticipantAndUserData();
        writeUserAndStaffParticipatedEventData();
        writeEventAndHostUserData();
        writeWalletData();
        writeThemeData();
        writeUserAndWalletData();
    }

    public void writeChatData() {
        chatListDatasource.writeData(chatList);
    }

    public void writeEventData() {
        eventListDatasource.writeData(eventList);
        writeParticipantAndEventData();
        writeEventAndStaffTeamData();
        writeUserAndParticipatedEventData();
        writeEventAndHostUserData();
    }

    public void writeParticipantAndEventData() {
        participantAndEventDatasource.writeData();
    }

    public void writeUserAndParticipatedEventData(){
        userAndParticipantedEventListDatasource.writeData(eventList);
    }


    public void writeEventAndStaffTeamData(){
        eventAndStaffTeamDatasource.writeData(staffTeamList);
    }

    public void writeParticipantData() {
        participantListDatasource.writeData(participantList);
        writeParticipantAndEventData();
        writeUserAndParticipatedEventData();
        writeParticipantAndUserData();
    }

    public void writeScheduleAndStaffTeamDatasourceData() {
        scheduleAndStaffTeamDatasource.writeData();
    }

    public void writeScheduleAndEventData() {
        scheduleAndEventDatasource.writeData();
    }

    public void writeScheduleData() {
        scheduleListDatasource.writeData(scheduleList);
        writeScheduleAndStaffTeamDatasourceData();
        writeScheduleAndEventData();
    }
    public void writeStaffTeamData() {
        staffTeamListDatasource.writeData(staffTeamList);
        writeStaffAndStaffTeamData();
        writeEventAndStaffTeamData();
        writeTeamAndHeadTeamData();
        writeTeamAndRegistrantData();
    }

    public void writeStaffData() {
        staffListDatasource.writeData(staffList);
        writeStaffAndStaffTeamData();
        writeStaffAndUserData();
    }

    public void writeTeamAndHeadTeamData() {
        teamAndHeadTeamDatasource.writeData();
    }

    public void writeStaffAndStaffTeamData() {
        staffAndStaffTeamDatasource.writeData();
    }

    public void writeParticipantAndUserData() {
        participantAndUserDatasource.writeData();
    }

    public void writeUserAndStaffParticipatedEventData() {
        userAndStaffParticipatedEventDatasource.writeData();
    }

    public void writeStaffAndUserData() { staffAndUserDatasource.writeData(); }

    public void writeUser() {
        userListDatasource.writeData(userList);
        writeUserAndStaffParticipatedEventData();
        writeThemeData();
        writeWalletData();
    }

    public void writeThemeData() {
        themeDatasource.writeData();
    }

    public void writeTeamAndRegistrantData() {
        teamAndRegistrantDatasource.writeData();
    }

    public void writeEventAndHostUserData() {
        eventAndHostUserDatasource.writeData();
    }

    public void writeWalletData() {
        walletListDatasource.writeData(walletList);
    }

    public void writeUserAndWalletData() {
        userAndWalletListDatasource.writeData();
    }

    public ChatList getChatList() {
        return chatList;
    }

    public User getCurrentUser() {
        return currentUser;
    }


    public EventList getEventList() {
        return eventList;
    }

    public ParticipantList getParticipantList() {
        return participantList;
    }

    public ScheduleList getScheduleList() {
        return scheduleList;
    }

    public StaffList getStaffList() {
        return staffList;
    }

    public UserList getUserList() {
        return userList;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public StaffTeamList getStaffTeamList() {
        return staffTeamList;
    }

    public WalletList getWalletList() {
        return walletList;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }
}
