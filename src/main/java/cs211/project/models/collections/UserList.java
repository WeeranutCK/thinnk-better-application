package cs211.project.models.collections;

import cs211.project.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class UserList {
    private ArrayList<User> userArrayList;


    public UserList() {
        userArrayList = new ArrayList<>();
    }

    public void addNewUser(Date dateOfBirth,
                           String email,
                           String password,
                           String profileName,
                           String username,
                           File imageFile) {
        email = email.trim();
        profileName = profileName.trim();
        username = username.trim();
        if (!username.equals("")) {
            User exist = findUserByUsername(username);
            if (exist == null) {
                userArrayList.
                        add(new User(dateOfBirth,
                                email, password,
                                profileName,
                                username,
                                imageFile));
            }
        }
    }

    public void addNewUser(Date dateOfBirth,
                           String email,
                           String password,
                           String profileName,
                           String profileImageFormat,
                           String userId,
                           String username,
                           ArrayList<Date> timeStamps,
                           String bio
                           ) {
        email = email.trim();
        profileName = profileName.trim();
        profileImageFormat = profileImageFormat.trim();
        username = username.trim();
        userId = userId.trim();
        if (!userId.equals("")) {
            User exist = findUserById(userId);
            if (exist == null) {
                userArrayList.
                        add(new User(dateOfBirth,
                                email, password,
                                profileName,
                                profileImageFormat,
                                userId,
                                username,
                                timeStamps,
                                bio
                                ));
            }
        }
    }

    public User findUserById(String userId) {
        for (User user: userArrayList) {
            if (user.isUserId(userId)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByUsername(String username) {
        for (User user: userArrayList) {
            if (user.isUsername(username)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }
}
