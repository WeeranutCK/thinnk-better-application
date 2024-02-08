package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.services.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class TimestampsUserController {
    @FXML
    private TableColumn<Date, String> dateColumn;
    @FXML
    private Label dateOfBirthLabel;
    @FXML
    private TableColumn<Date, String> dayColumn;
    @FXML
    private Label emailLabel;
    @FXML
    private TableColumn<Date, String> fullTimestampColumn;
    @FXML
    private TableColumn<Date, String> monthColumn;
    @FXML
    private VBox navBarVBox;
    @FXML
    private Label profileNameLabel;
    @FXML
    private ImageView profilePicture;
    @FXML
    private TableColumn<Date, String> timeColumn;
    @FXML
    private TableView<Date> timestampTableView;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private TableColumn<Date, String> yearColumn;

    private User selectedUser;

    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        selectedUser = (User) FXRouter.getData();
        if (selectedUser != null) {
            profileNameLabel.setText(selectedUser.getProfileName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateOfBirthLabel.setText(dateFormat.format(selectedUser.getDateOfBirth()));
            emailLabel.setText(selectedUser.getEmail());
            userIdLabel.setText(selectedUser.getUserId());
            usernameLabel.setText(selectedUser.getUsername());
            profilePicture.setImage(selectedUser.getProfileImage());
            AdjustImageView.setViewPortImageView(profilePicture, selectedUser.getProfileImage());
            AdjustImageView.radiusImageView(profilePicture, profilePicture.getFitHeight());
            setupPage();
        }
    }

    @FXML
    private void onBackButtonClicked() {
        try {
            FXRouter.goTo(PageHistory.getInstance().popStack());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void setupPage() {
        Loader.loadNavBar(navBarVBox);
        setUpTimestampTable();
    }

    private void setUpTimestampTable() {

        timestampTableView.getColumns().forEach(column -> column.setReorderable(false));
        dateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("dd", Locale.ENGLISH).format(param.getValue())));
        monthColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("MMMM", Locale.ENGLISH).format(param.getValue())));
        yearColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(param.getValue())));
        dayColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(param.getValue())));
        timeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(param.getValue())));
        fullTimestampColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(param.getValue())));

        Collections.sort(selectedUser.getTimeStamps(), Collections.reverseOrder());
        timestampTableView.getItems().clear();
        timestampTableView.getItems().addAll(selectedUser.getTimeStamps());
    }
}
