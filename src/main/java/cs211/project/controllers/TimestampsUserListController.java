package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.services.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class TimestampsUserListController {
    @FXML
    private TableColumn<User, Integer> ageColumn;
    @FXML
    private VBox navBarVBox;
    @FXML
    private Label numberOfUsersLabel;
    @FXML
    private TableColumn<User, String> lastLoginColumn;
    @FXML
    private TableColumn<User, Image> profileColumn;
    @FXML
    private TableColumn<User, String> profileNameColumn;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private TableColumn<User, String> userIdColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableView<User> userTableView;

    private FilteredList<User> filteredList = null;
    private ObservableList<User> userTableDataList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        setupPage();

        userTableView.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        PageHistory.getInstance().pushStack("timestamps-user-list");
                        FXRouter.goTo("timestamps-user", row.getItem());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.getStyleClass().add("row-zoom");
                }
            });
            row.setOnMouseExited(event -> {
                row.getStyleClass().remove("row-zoom");
            });

            return row;
        });

    }

    private void searchAndUpdateTableView() {
        searchBarTextField.textProperty().addListener((observable, oldValue, newValue) ->{
            filteredList.setPredicate((Predicate<? super User>) user -> {
                String searchString = newValue.toLowerCase();

                String profileName = user.getProfileName();
                String username = user.getUsername();
                String userId = user.getUserId();
                String age = String.valueOf(user.getAge());

                int lastIndex = user.getTimeStamps().size() - 1;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                String lastLogIn = "Never logged in";
                if (lastIndex >= 0) {
                    lastLogIn = simpleDateFormat.format(user.getTimeStamps().get(lastIndex)).toLowerCase();
                }

                if (userId.toLowerCase().contains(searchString)) {
                    return true;
                } else if (username.toLowerCase().contains(searchString)) {
                    return true;
                } else if (profileName.toLowerCase().contains(searchString)) {
                    return true;
                } else if (age.toLowerCase().contains(searchString)) {
                    return true;
                } else if (lastLogIn.toLowerCase().contains(searchString)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void setupPage() {
        String numberOfUsers = "(" + AllCollection.getInstance().getUserList().getUserArrayList().size() + " in total)";
        numberOfUsersLabel.setText(numberOfUsers);
        setTableView();
        Loader.loadNavBar(navBarVBox);
    }

    private void setTableView() {
        searchAndUpdateTableView();
        userTableDataList.clear();
        userTableDataList.addAll(AllCollection.getInstance().getUserList().getUserArrayList());
        sortUserTableDataListByLastLogin();

        filteredList = new FilteredList<>(userTableDataList, p -> true);
        SortedList<User> sortedData = new SortedList<>(filteredList);
        userTableView.setItems(sortedData);
        setColumn();
        setSortLastLogin();
        sortedData.comparatorProperty().bind(userTableView.comparatorProperty());
    }

    private void sortUserTableDataListByLastLogin() {
        userTableDataList.sort((user1, user2) -> {
            Date date1 = user1.getTimeStamps().size() != 0? user1.getTimeStamps().get(user1.getTimeStamps().size() - 1) : null;
            Date date2 = user2.getTimeStamps().size() != 0? user2.getTimeStamps().get(user2.getTimeStamps().size() - 1) : null;
            int valueOfDate1;
            int valueOfDate2;

            if (date1 == null || date2 == null) {
                if (date1 == null) {
                    valueOfDate1 = -1;
                } else {
                    valueOfDate1 = 1;
                }

                if (date2 == null) {
                    valueOfDate2 = -1;
                } else {
                    valueOfDate2 = 1;
                }
                return Integer.compare(valueOfDate2, valueOfDate1);
            } else {
                return date2.after(date1)? 1: date2.before(date1)? -1 : 0;
            }
        });
    }

    private void setSortLastLogin() {
        lastLoginColumn.setComparator((dateString1, dateString2) -> {
            int valueOfDate1;
            int valueOfDate2;

            if (dateString1.equalsIgnoreCase("Never logged in") || dateString2.equalsIgnoreCase("Never logged in")) {
                if (dateString1.equalsIgnoreCase("Never logged in")) {
                    valueOfDate1 = -1;
                } else {
                    valueOfDate1 = 1;
                }

                if (dateString2.equalsIgnoreCase("Never logged in")) {
                    valueOfDate2 = -1;
                } else {
                    valueOfDate2 = 1;
                }
                return Integer.compare(valueOfDate2, valueOfDate1);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                try {
                    Date date1 = simpleDateFormat.parse(dateString1);
                    Date date2 = simpleDateFormat.parse(dateString2);
                    return date2.after(date1)? 1: date2.before(date1)? -1 : 0;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setColumn() {
        userTableView.getColumns().forEach(column -> column.setReorderable(false));
        profileColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProfileImage()));
        profileColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(42);
                imageView.setFitWidth(42);
                AdjustImageView.radiusImageView(imageView,42);
                setGraphic(imageView);
            }
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    imageView.setImage(null);
                } else {
                    imageView.setImage(item);
                    AdjustImageView.setViewPortImageView(imageView,item);
                }
            }
        });
        profileColumn.setSortable(false);

        profileNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getProfileName()));
        usernameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getUsername()));
        userIdColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getUserId()));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        lastLoginColumn.setCellValueFactory(param -> {
            int lastIndex = param.getValue().getTimeStamps().size() - 1;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            String lastLogIn = "Never logged in";
            if (lastIndex >= 0) {
                Date date = Collections.max(param.getValue().getTimeStamps());
                lastLogIn = simpleDateFormat.format(date);
            }
            return new ReadOnlyObjectWrapper<>(lastLogIn);
        });
    }

}