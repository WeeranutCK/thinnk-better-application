package cs211.project.models;

import cs211.project.services.AllCollection;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Theme {
    private String currentTheme;
    private String currentFontSize;
    private String currentFontFamily;
    private Scene scene;

    private static String cssFileDirectory = "data/styles/styles.css";

    public Theme() {
        currentTheme = "Dark";
        currentFontFamily = "Arial";
        currentFontSize = "Large";
    }

    public Theme(String currentTheme, String currentFontSize, String currentFontFamily) {
        this.currentTheme = currentTheme;
        this.currentFontSize = currentFontSize;
        this.currentFontFamily = currentFontFamily;
    }

    public void reloadTheme() {
        setStyle();
        AllCollection.getInstance().writeThemeData();
        Platform.runLater(this::reloadStyles);
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public String getCurrentFontFamily() {
        return currentFontFamily;
    }

    public String getCurrentFontSize() {
        return currentFontSize;
    }

    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }

    public void setCurrentFontFamily(String currentFontFamily) {
        this.currentFontFamily = currentFontFamily;
    }

    public void setCurrentFontSize(String currentFontSize) {
        this.currentFontSize = currentFontSize;
    }

    private void writeCssFontStylesFile(ArrayList<String> fontFamilyStyles,
                                        ArrayList<ArrayList<String>> allFontSizeStyles,
                                        String[] colorPalette
                                        ) {
        String filePath = cssFileDirectory;
        File file = new File(filePath);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            buffer.write("* {\n");
            for (String eachStyle : fontFamilyStyles) {
                buffer.write("\t" + eachStyle + ";\n");
            }
            buffer.write("}\n\n");

            buffer.write("*.bold {\n\t-fx-font-weight: bold;\n}\n\n");

            buffer.write(".combo-box .list-cell {\n");
            for (String eachStyle : fontFamilyStyles) {
                buffer.write("\t" + eachStyle + ";\n");
            }
            buffer.write("}\n");

            for (int i = 0; i < allFontSizeStyles.size(); i++) {
                buffer.write("\n*.h" + (i + 1) + " {\n");
                for (String eachStyle : allFontSizeStyles.get(i)) {
                    buffer.write("\t" + eachStyle + ";\n");
                }
                buffer.write("}\n");
            }

            buffer.write("\n*.primary-button {\n\t-fx-background-color: " + colorPalette[0] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.secondary-button {\n\t-fx-background-color: " + colorPalette[2] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[0] + ";\n" +
                    "}\n\n*.primary-text-field {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "\t-fx-prompt-text-fill: " + colorPalette[3] + ";\n" +
                    "\t-fx-control-inner-background: " + colorPalette[4] + ";\n" +
                    "\t-fx-focus-color: transparent;\n" +
                    "\t-fx-border-color: transparent;\n" +
                    "}\n\n*.secondary-text-field {\n\t-fx-text-fill: " + colorPalette[1] + ";\n" +
                    "\t-fx-control-inner-background: " + colorPalette[5] + ";\n" +
                    "}\n\n*.text-area * {\n\t-fx-background-color: transparent" + ";\n" +
                    "}\n\n*.text-area {\n\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "\t-fx-caret-color: " + colorPalette[2] + ";\n" +
                    "\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\t-fx-control-inner-background: " + colorPalette[4] + ";\n" +
                    "\t-fx-prompt-text-fill: " + colorPalette[3] + ";\n" +
                    "\t-fx-faint-focus-color: transparent;\n" +
                    "\t-fx-focus-color: hidden;\n" +
                    "}\n\n*.text-area .content {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\tfx-border-color: transparent;\n" +
                    "}\n\n*.secondary-text-area {\n\t-fx-background-color: #252525;\n" +
                    "\t-fx-control-inner-background: " + colorPalette[9] + ";\n" +
                    "}\n\n*.secondary-text-area .content {\n" +
                    "\t-fx-background-color: " + colorPalette[9] + ";\n" +
                    "}\n\n*.text-area:focused .content {\n\t-fx-border-color: transparent;\n" +
                    "}\n\n*.background {\n\t-fx-background-color: " + colorPalette[6] + ";\n" +
                    "}\n\n*.box {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\t-fx-border-block: hidden;\n" +
                    "}\n\n*.header-block {\n\t-fx-background-color: " + colorPalette[8] + ";\n" +
                    "}\n\n*.inner-block {\n\t-fx-background-color: " + colorPalette[9] + ";\n" +
                    "}\n\n*.combo-box {\n\t-fx-background-color: " + colorPalette[7] + ";\n" +
                    "\t-fx-border-color: " + colorPalette[12] + ";\n" +
                    "}\n\n*.combo-box .arrow-button .arrow {\n\t-fx-background-color: " + colorPalette[10] + ";\n" +
                    "}\n\n*.combo-box .indexed-cell {\n\t-fx-background-color: " + colorPalette[12] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.combo-box .list-cell {\n\t-fx-background-color: " + colorPalette[8] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.combo-box .list-cell:hover {\n\t-fx-background-color: " + colorPalette[7] + ";\n" +
                    "}\n\n*.combo-box .list-view {\n\t-fx-background-color: " + colorPalette[7] + ";\n" +
                    "}\n\n*.date-picker * {\n\t-fx-background-color: transparent;\n" +
                    "}\n\n*.date-picker {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\t-fx-control-inner-background: " + colorPalette[4] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "\t-fx-focus-color: transparent;\n" +
                    "\t-fx-faint-focus-color: transparent;\n" +
                    "}\n\n*.date-picker .inner {\n\t-fx-border-color: transparent;\n" +
                    "}\n\n*.date-picker .text-field {\n\t-fx-prompt-text-fill: " + colorPalette[3] + ";\n" +
                    "}\n\n*.date-picker .arrow-button {\n\t-fx-background-color: " + colorPalette[11] + ";\n" +
                    "\t-fx-border-color: transparent;\n" +
                    "}\n\n*.date-picker .arrow-button .arrow {\n\t-fx-background-color: " + colorPalette[10] + ";\n" +
                    "}\n\n*.date-picker-popup > * > .date-cell {\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.date-picker-popup {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "}\n\n*.date-picker-popup .month-year-pane {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "}\n\n*.date-picker-popup .spinner .button:focused {\n\t-fx-background-color: transparent;\n" +
                    "\t-fx-color: transparent;\n" +
                    "}\n\n*.date-picker-popup .spinner .button:hover {\n\t-fx-color: transparent;\n" +
                    "\t-fx-border-color: " + colorPalette[1] + ";\n" +
                    "}\n\n*.date-picker-popup .spinner .button .left-arrow, .date-picker-popup .spinner .button .right-arrow {\n" +
                    "\t-fx-background-color: " + colorPalette[2] + ";\n" +
                    "}\n\n*.date-picker-popup > * > .spinner > .label {\n\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.date-picker-popup > .calendar-grid {\n\t-fx-background-color: " + colorPalette[11] + ";\n" +
                    "}\n\n*.date-picker-popup > * > .day-cell {\n\t-fx-background: " + colorPalette[6] + ";\n" +
                    "\t-fx-background-color: -fx-background;\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.date-picker-popup > * > .day-cell:hover {\n\t-fx-background: " + colorPalette[0] + ";\n" +
                    "}\n\n*.date-picker-popup > * > .previous-month:hover, .date-picker-popup > * > .next-month:hover {\n" +
                    "\t-fx-background: " + colorPalette[3] + ";\n" +
                    "}\n\n*.date-picker-popup > * > .previous-month, .date-picker-popup > * > .next-month {\n" +
                    "\t-fx-background: " + colorPalette[4] + ";\n" +
                    "}\n\n*.date-picker-popup .selected {\n\t-fx-background: " + colorPalette[1] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[6] + ";\n" +
                    "}\n\n*.message-block {\n\t-fx-background-color: " + colorPalette[0] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.check-box .box {\n\t-fx-background-color: " + colorPalette[2] + ";\n" +
                    "}\n\n*.table-view * {\n\t-fx-background-color: transparent" + ";\n" +
                    "}\n\n*.table-column .arrow {\n\t-fx-background-color: " + colorPalette[2] + ";\n" +
                    "}\n\n*.table-view * {\n\t-fx-background-color: transparent" + ";\n" +
                    "}\n\n*.table-view {\n\t-fx-background-color: " + colorPalette[16] + ";\n" +
                    "}\n\n*.table-view .column-header-background {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\t-fx-border-color: " + colorPalette[0] + ";\n" +
                    "}\n\n*.table-view .column-header .label {\n\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.table-view .cell {\n\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "\t-fx-background-color: " + colorPalette[16] + ";\n" +
                    "}\n\n*.table-view .scroll-bar:vertical .thumb  {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "}\n\n*.table-view .check-box .box {\n\t-fx-background-color: " + colorPalette[2] + ";\n" +
                    "\t-fx-border-color: " + colorPalette[2] + ";\n" +
                    "}\n\n*.table-view .check-box:selected .mark {\n\t-fx-background-color: " + colorPalette[0] + ";\n" +
                    "\t-fx-border-color: " + colorPalette[0] + ";\n" +
                    "}\n\n*.table-view .scroll-bar .increment-button,\n*.table-view .scroll-bar .decrement-button {\n" +
                    "\t-fx-background-color: " + colorPalette[15] + ";\n" +
                    "\t-fx-border-color: " + colorPalette[15] + ";\n" +
                    "}\n\n*.table-cell {\n\t-fx-background-color: " + colorPalette[0] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.scroll-pane {\n\t-fx-background-color: transparent;\n" +
                    "}\n\n*.schedule-background {\n\t-fx-background-color: " + colorPalette[16] + ";\n" +
                    "}\n\n*.schedule-edit-background {\n\t-fx-background-color: " + colorPalette[17] + ";\n" +
                    "}\n\n*.used-button {\n\t-fx-background-color: " + colorPalette[18] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.used-button.non-active {\n\t-fx-background-color: " + colorPalette[19] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[20] + ";\n" +
                    "}\n\n*.secondary-background {\n\t-fx-background-color: " + colorPalette[21] + ";\n" +
                    "}\n\n*.table-view .row-zoom .cell {\n\t-fx-background-color: " + colorPalette[13] + ";\n" +
                    "}\n\n*.selected-row .table-row-cell:selected .cell {\n\t-fx-background-color: " + colorPalette[24] + ";\n" +
                    "}\n\n*.box-in-event {\n\t-fx-background-color: " + colorPalette[22] + ";\n" +
                    "\t-fx-border-block: hidden;\n" +
                    "}\n\n*.more-info-button {\n\t-fx-background-color: " + colorPalette[23] + ";\n" +
                    "\t-fx-border-block: hidden;\n" +
                    "}\n\n*.card-background {\n\t-fx-background-color: " + colorPalette[7] + ";\n" +
                    "}\n\n*.tertiary-button {\n\t-fx-background-color: " + colorPalette[4] + ";\n" +
                    "\t-fx-text-fill: " + colorPalette[2] + ";\n" +
                    "}\n\n*.table-view-secondary-colour {\n\t-fx-background-color: " + colorPalette[25] + ";\n" +
                    "}\n\n*.table-view-secondary-colour .cell{\n\t-fx-background-color: " + colorPalette[25] + ";\n" +
                    "}\n\n*.table-view .radio-button .radio {\n\t-fx-background-color: #F0F0F0;\n" +
                    "\t-fx-border-color: "+ colorPalette[25] +";\n" +
                    "}\n\n*.table-view .radio-button:selected .dot{\n\t-fx-background-color: #151515;\n" +
                    "\t-fx-border-color: " + colorPalette[2] + ";\n" +
                    "}\n\n*.line {\n\t-fx-stroke: " + colorPalette[2] + ";\n" +
                    "}\n\n*.wallet-box {\n\t-fx-background-color: " + colorPalette[9] + ";\n" +
                    "}\n\n*.main-header-text {\n\t-fx-text-fill: linear-gradient(from 100.0% 0.0% to 100.0% 75.0%, " +  colorPalette[0] + " 40.0%, " +  colorPalette[5] + " 100.0%);\n" +
                    "}\n\n*.primary-text {\n\t-fx-text-fill: " + colorPalette[5] + ";\n" +
                    "}\n\n*.secondary-text {\n\t-fx-text-fill: " + colorPalette[1] + ";\n" +
                    "}\n\n*.navbar-vbox {\n\t-fx-background-color: linear-gradient(to bottom, " + colorPalette[0] + ", " + colorPalette[1] + ");\n" +
                    "}\n"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void reloadStyles() {
        File file = new File(cssFileDirectory);
        scene.getRoot().getStylesheets().clear();
        scene.getRoot().getStylesheets().add(file.toURI().toString());
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private void setStyle() {
        ArrayList<String> fontFamilyStyles = new ArrayList<>();
        fontFamilyStyles.add("-fx-font-family: \"" + currentFontFamily + "\"");

        ArrayList<ArrayList<String>> allFontSizeStyles = new ArrayList<>();

        int[] fontSizeValues = {38, // index 0 : h1
                                26, // index 1 : h2
                                24, // index 2 : h3
                                22, // index 3 : h4
                                20, // index 4 : h5
                                19, // index 5 : h6
                                18, // index 6 : h7
                                16, // index 7 : h8
                                15, // index 8 : h9
                                14, // index 9 : h10
                                12, // index 10 : h11
                                40, // index 11 : h12
                                };

        int offset;
        if (currentFontSize.equals("Large")) {
            offset = 0;
        } else {
            offset = 2;
        }

        for (int fontSizeValue : fontSizeValues) {
            ArrayList<String> fontSizeStyles = new ArrayList<>();
            fontSizeStyles.add("-fx-font-size: " + (fontSizeValue - offset) + "px");
            allFontSizeStyles.add(fontSizeStyles);
        }

        String[] colorPalette = {
                "#6528F7", // index 0 : primary main color
                "#A076F9", // index 1 : secondary main color
                "#F0F0F0", // index 2 : primary text color
                "#949494", // index 3 : prompt text color
                "#3B3B3B", // index 4 : box color
                "#E3D4FF", // index 5 : inner box color
                "#131113", // index 6 : main background color
                "#383838", // index 7 : primary background color
                "#303030", // index 8 : header block
                "#252525", // index 9 : inner block
                "#A8A8A8", // index 10 : arrow
                "#292929", // index 11 : arrow background
                "#282828", // index 12 : indexed cell
                "#474747", // index 13 : indexed cell (hover)
                "#616161", // index 14 : checkbox background
                "#575757", // index 15 : scroll bar
                "#252525", // index 16 : schedule background
                "#1E1E1E", // index 17 : schedule edit
                "#2E2E2E", // index 18 : used button
                "#434343", // index 19 : unused button
                "#909090", // index 20 : unused text
                "#2F2F2F", // index 21 : inner background
                "#212020", // index 22 : vbox in scroll pane
                "#474747", // index 23 : more info in card
                "#202020", // index 24 : selected row in table view
                "#151515", // index 25 : secondary table view color
        };

        String[] lightColorPalette = {
                "#E71178", // index 0 : primary main color
                "#F254A0", // index 1 : secondary main color
                "#F0F0F0", // index 2 : primary text color
                "#949494", // index 3 : prompt text color
                "#3B3B3B", // index 4 : box color
                "#FC88DA", // index 5 : inner box color
                "#131113", // index 6 : main background color
                "#383838", // index 7 : primary background color
                "#303030", // index 8 : header block
                "#252525", // index 9 : inner block
                "#A8A8A8", // index 10 : arrow
                "#292929", // index 11 : arrow background
                "#282828", // index 12 : indexed cell
                "#474747", // index 13 : indexed cell (hover)
                "#616161", // index 14 : checkbox background
                "#575757", // index 15 : scroll bar
                "#252525", // index 16 : schedule background
                "#1E1E1E", // index 17 : schedule edit
                "#2E2E2E", // index 18 : used button
                "#434343", // index 19 : unused button
                "#909090", // index 20 : unused text
                "#2F2F2F", // index 21 : inner background
                "#212020", // index 22 : vbox in scroll pane
                "#474747", // index 23 : more info in card
                "#202020", // index 24 : selected row in table view
                "#151515", // index 25 : secondary table view color
        };

        if (currentTheme.equals("Candy Pink")) {
            writeCssFontStylesFile(fontFamilyStyles, allFontSizeStyles, lightColorPalette);
        } else {
            writeCssFontStylesFile(fontFamilyStyles, allFontSizeStyles, colorPalette);
        }
    }
}
