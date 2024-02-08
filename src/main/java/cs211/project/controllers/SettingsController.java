package cs211.project.controllers;

import cs211.project.services.AllCollection;
import cs211.project.services.Loader;
import cs211.project.services.TimeConversion;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsController {
    @FXML
    private VBox navBarVBox;
    @FXML
    private Label timeZoneLabel;
    @FXML
    private ComboBox<String> themeComboBox;
    @FXML
    private ComboBox<String> fontSizeComboBox;
    @FXML
    private ComboBox<String> fontFamilyComboBox;


    @FXML
    private void initialize() {
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();

        setComboBox();

        timeZoneLabel.setText(TimeConversion.getUTCStringFormatted());

        Loader.loadNavBar(navBarVBox);
    }


    @FXML
    private void setTheme() {
        AllCollection.getInstance().getCurrentUser().getTheme().setCurrentTheme(themeComboBox.getValue());
        AllCollection.getInstance().getCurrentUser().getTheme().setCurrentFontSize(fontSizeComboBox.getValue());
        AllCollection.getInstance().getCurrentUser().getTheme().setCurrentFontFamily(fontFamilyComboBox.getValue());
        AllCollection.getInstance().getCurrentUser().getTheme().reloadTheme();
    }

    private void setComboBox() {
        String[] themes = new String[]{"Sweet Dreams", "Candy Pink"};
        String[] fontSizes = new String[]{"Small", "Large"};
        String[] fontFamily = new String[]{"Arial", "Calibri", "Open Sans"};

        themeComboBox.getItems().addAll(themes);
        fontSizeComboBox.getItems().addAll(fontSizes);
        fontFamilyComboBox.getItems().addAll(fontFamily);

        themeComboBox.getSelectionModel().select(AllCollection.getInstance().getCurrentUser().getTheme().getCurrentTheme());
        fontSizeComboBox.getSelectionModel().select(AllCollection.getInstance().getCurrentUser().getTheme().getCurrentFontSize());
        fontFamilyComboBox.getSelectionModel().select(AllCollection.getInstance().getCurrentUser().getTheme().getCurrentFontFamily());
    }
}
