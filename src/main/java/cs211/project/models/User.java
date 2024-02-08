package cs211.project.models;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cs211.project.cs211661project.ThinnkApplication;
import cs211.project.models.collections.EventList;
import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;
import cs211.project.services.TimeConversion;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;

public class User {
    protected Date dateOfBirth;
    protected String email;
    protected String password;
    private final Image DEFAULT_PROFILE_IMAGE = new Image(getClass().getResourceAsStream("/cs211/project/images/user.png"));
    private String bio;
    private EventList hostedEvent;
    private EventList participatedEvent;
    private String profileImageFormat;
    private Image profileImage;
    protected String profileName;
    protected String userId;
    protected String username;
    protected ArrayList<Date> timeStamps;
    private EventList staffParticipatedEvent;
    private Wallet wallet;
    private Theme theme;

    public User() {
        theme = new Theme();
    }

    public User(Date dateOfBirth,
                String email,
                String password,
                String profileName,
                String username,
                File imageFile) {

        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.username = username;
        this.profileName = profileName;
        this.bio = "";
        setPassword(password);
        profileImage = DEFAULT_PROFILE_IMAGE;
        userId = IdGenerator.generateId("user", AllCollection.getInstance().getUserList().getUserArrayList().size());
        timeStamps = new ArrayList<>();
        hostedEvent = new EventList();
        participatedEvent = new EventList();
        staffParticipatedEvent = new EventList();
        wallet = new Wallet(userId);
        theme = new Theme();

        if (imageFile != null) {
            profileImageFormat = getFileExtension(imageFile);
            copyFile(imageFile);
        } else {
            profileImageFormat = "null";
        }
        setupProfileImage();
    }

    public User(Date dateOfBirth,
                String email,
                String password,
                String profileName,
                String profileImageFormat,
                String userId,
                String username,
                ArrayList<Date> timeStamps,
                String bio
                ) {
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
        this.profileName = profileName;
        this.profileImageFormat = profileImageFormat;
        this.userId = userId;
        this.username = username;
        this.timeStamps = timeStamps;
        this.bio = bio;
        theme = new Theme();
        setupProfileImage();

        hostedEvent = new EventList();
        participatedEvent = new EventList();
        staffParticipatedEvent = new EventList();
    }


    public void changeProfileImage(File fileImage) {
        if (fileImage == null) {
            profileImageFormat = "null";
        } else {
            profileImageFormat = getFileExtension(fileImage);
            copyFile(fileImage);
            setupProfileImage();
        }
    }

    public boolean checkPassword(String password) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), this.password);
        return result.verified;
    }

    private void copyFile(File file) {
        Path targetPath = Path.of("data/users/users-images");
        try {
            Files.createDirectories(targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.copy(file.toPath(), Path.of(targetPath + File.separator + userId + "." + profileImageFormat), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getAge() {
        int age = TimeConversion.getNowDate().getYear() - dateOfBirth.getYear();
        if (dateOfBirth.getMonth() < TimeConversion.getNowDate().getMonth()) {
            age--;
        } else if (dateOfBirth.getMonth() == TimeConversion.getNowDate().getMonth() &&
                dateOfBirth.getDate() < TimeConversion.getNowDate().getDate()) {
            age--;
        }
        return age;
    }

    private String getFileExtension(File file) {
        String path = file.getPath();
        String[] splitPath = path.split("\\.");
        String fileExtension = splitPath[splitPath.length - 1];
        return fileExtension;
    }

    public boolean isAdmin() {
        return this.username.equals("admin");

    }

    public boolean isStaff(Event event) {
        return staffParticipatedEvent.getEventArrayList().contains(event);
    }

    public boolean isUserId(String userId) {
        return this.userId.equals(userId);
    }

    public boolean isUsername(String username) {
        return this.username.equals(username);
    }

    public void removeParticipatedEvent(Event event) {
        participatedEvent.getEventArrayList().remove(event);
    }

    public void setPassword(String password) {
        this.password = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }


    private void setupProfileImage() {
        if (profileImageFormat == null || profileImageFormat.equals("null")) {
            profileImage = DEFAULT_PROFILE_IMAGE;
        } else {
            String filePath = "data/users/users-images/";
            String fileName = userId + "." + profileImageFormat;
            File file = new File(filePath + fileName);
            profileImage = new Image(file.toURI().toString());
        }
    }


    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public EventList getHostedEvent() {
        return hostedEvent;
    }


    public EventList getParticipatedEvent() {
        return participatedEvent;
    }

    public String getProfileImageFormat() {
        return profileImageFormat;
    }

    public Image getProfileImage() {
        return profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Date> getTimeStamps() {
        return timeStamps;
    }

    public EventList getStaffParticipatedEvent() {
        return staffParticipatedEvent;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImageFormat(String profileImageFormat) {
        this.profileImageFormat = profileImageFormat;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void setWallet(Wallet wallet) {this.wallet = wallet;}
}