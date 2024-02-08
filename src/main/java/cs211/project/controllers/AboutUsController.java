package cs211.project.controllers;

import cs211.project.cs211661project.ThinnkApplication;
import cs211.project.services.AdjustImageView;
import cs211.project.services.AllCollection;
import cs211.project.services.Loader;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


public class AboutUsController {
    @FXML
    private ImageView jitladaImageView;
    @FXML
    private ImageView karnnitraImageView;
    @FXML
    private VBox navBarVBox;
    @FXML
    private ImageView weeranutImageView;
    @FXML
    private ImageView wanwalaiImageView;


    @FXML
    public void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        Loader.loadNavBar(navBarVBox);

        AdjustImageView.radiusImageView(jitladaImageView,15);
        AdjustImageView.radiusImageView(weeranutImageView,15);
        AdjustImageView.radiusImageView(wanwalaiImageView,15);
        AdjustImageView.radiusImageView(karnnitraImageView,15);
    }

    @FXML
    public void onManualButtonClick() {
        ThinnkApplication.hostServices.showDocument("https://drive.google.com/file/d/1PApjj6xQdJcNkLGBRBVyqDLALTwG1Stu/view?usp=sharing/");
    }
}
