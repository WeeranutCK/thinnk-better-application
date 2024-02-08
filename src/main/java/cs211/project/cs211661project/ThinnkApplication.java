package cs211.project.cs211661project;

import cs211.project.services.FXRouter;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

import java.io.IOException;

public class ThinnkApplication extends Application {
    public static HostServices hostServices;

    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();
        configRoute();

        FXRouter.bind(this, stage);

        FXRouter.goTo("log-in");

        stage.setTitle("Thinnk");
        stage.setResizable(false);
        stage.show();
    }

    private static void configRoute() {
        String resourcesPath = "cs211/project/views/";

        FXRouter.when("events", resourcesPath + "events.fxml");
        FXRouter.when("log-in", resourcesPath + "log-in.fxml");
        FXRouter.when("main", resourcesPath + "main.fxml");
        FXRouter.when("create-new-event", resourcesPath + "create-new-event.fxml");
        FXRouter.when("about-us", resourcesPath + "about-us.fxml");
        FXRouter.when("profile", resourcesPath + "profile.fxml");
        FXRouter.when("events-history", resourcesPath + "events-history.fxml");
        FXRouter.when("change-password", resourcesPath + "change-password.fxml");
        FXRouter.when("add-activity", resourcesPath + "add-activity.fxml");
        FXRouter.when("team-registration", resourcesPath + "team-registration.fxml");
        FXRouter.when("hosted-events", resourcesPath + "hosted-events.fxml");
        FXRouter.when("sign-up", resourcesPath + "sign-up.fxml");
        FXRouter.when("participants", resourcesPath + "participants.fxml");
        FXRouter.when("my-team", resourcesPath + "my-team.fxml");
        FXRouter.when("edit-event", resourcesPath + "edit-event.fxml");
        FXRouter.when("event-info", resourcesPath + "event-info.fxml");
        FXRouter.when("all-team", resourcesPath + "all-team.fxml");
        FXRouter.when("settings", resourcesPath + "settings.fxml");
        FXRouter.when("edit-activity", resourcesPath + "edit-activity.fxml");
        FXRouter.when("timestamps-user-list", resourcesPath + "timestamps-user-list.fxml");
        FXRouter.when("timestamps-user", resourcesPath + "timestamps-user.fxml");
        FXRouter.when("timestamps-all-user", resourcesPath + "timestamps-all-user.fxml");
        FXRouter.when("edit-team", resourcesPath + "edit-team.fxml");
        FXRouter.when("team-detail", resourcesPath + "team-detail.fxml");
    }

    public static void main(String[] args) {
        launch();
    }

}