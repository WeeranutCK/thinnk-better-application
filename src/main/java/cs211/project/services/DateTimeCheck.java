package cs211.project.services;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DateTimeCheck {
    public static boolean checkEndDate(DatePicker endDatePicker, Label errorLabel, DatePicker startDatePicker) {
        if (endDatePicker.getValue() == null) {
            errorLabel.setText("Cannot be empty.");
        } else if (startDatePicker.getValue() == null) {
            errorLabel.setText("Please select a start date.");
        } else if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            errorLabel.setText("Cannot be before start date.");
        } else {
            return true;
        }
        return false;
    }

    public static boolean checkStartDate(DatePicker datePicker, Label errorLabel) {
        if (datePicker.getValue() == null) {
            errorLabel.setText("Cannot be empty.");
        } else {
            return true;
        }
        return false;
    }

    public static boolean checkTime(TextField startTime, TextField endTime, Label errorStartTime, Label errorEndTime, DatePicker endDatePicker, DatePicker startDatePicker) {
        String[] startStrings = startTime.getText().split(":");
        String[] endStrings = endTime.getText().split(":");

        Integer[] startTimeInteger = new Integer[2];
        Integer[] endTimeInteger = new Integer[2];

        try {
            startTimeInteger[0] = Integer.parseInt(startStrings[0]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            startTimeInteger[0] = null;
        }

        try {
            startTimeInteger[1] = Integer.parseInt(startStrings[1]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            startTimeInteger[1] = null;
        }

        try {
            endTimeInteger[0] = Integer.parseInt(endStrings[0]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            endTimeInteger[0] = null;
        }

        try {
            endTimeInteger[1] = Integer.parseInt(endStrings[1]);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            endTimeInteger[1] = null;
        }

        boolean startTimeCheck = false;
        boolean endTimeCheck = false;

        if (startTime.getText().equals("")) {
            errorStartTime.setText("Cannot be empty.");
        } else if (startStrings.length != 2 || startStrings[1].length() != 2 ||
                startTimeInteger[0] == null || startTimeInteger[1] == null ||
                startTimeInteger[0] < 0 || startTimeInteger[0] >= 24 ||
                startTimeInteger[1] < 0 || startTimeInteger[1] >= 60) {
            errorStartTime.setText("Invalid time format(HH:mm)");
        } else if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            errorStartTime.setText("Please select start date and end date.");
        } else {
            startTimeCheck = true;
        }

        if (endTime.getText().equals("")) {
            errorEndTime.setText("Cannot be empty.");
        } else if (endStrings.length != 2 || endStrings[1].length() != 2 ||
                endTimeInteger[0] == null || endTimeInteger[1] == null ||
                endTimeInteger[0] < 0 || endTimeInteger[0] > 23 ||
                endTimeInteger[1] < 0 || endTimeInteger[1] >= 60) {
            errorEndTime.setText("Invalid time format(HH:mm)");
        } else if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            errorEndTime.setText("Please select start date and end date.");
        } else if (!startTimeCheck) {
            errorEndTime.setText("Please complete start time.");
        } else if (startDatePicker.getValue().equals(endDatePicker.getValue()) &&
                (endTimeInteger[0] * 60 + endTimeInteger[1] <= startTimeInteger[0] * 60 + startTimeInteger[1])) {
            errorEndTime.setText("Cannot be before or same as start Time.");
        } else {
            endTimeCheck = true;
        }

        return endTimeCheck && startTimeCheck;
    }

    public static ArrayList<Date> createDate(DatePicker startDate, DatePicker endDate, Label errorStartDate, Label errorEndDate,
                                             TextField startTime, TextField endTime, Label errorStartTime, Label errorEndTime) {
        boolean checkStartDate = DateTimeCheck.checkStartDate(startDate, errorStartDate);
        boolean checkEndDate = DateTimeCheck.checkEndDate(endDate, errorEndDate, startDate);
        boolean checkTime = DateTimeCheck.checkTime(startTime, endTime, errorStartTime,
                errorEndTime, startDate, endDate);
        if (checkStartDate && checkEndDate && checkTime) {
            String dateStart = startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'", Locale.ENGLISH));
            String dateEnd = endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'", Locale.ENGLISH));
            String timeStart = startTime.getText();
            String timeEnd = endTime.getText();
            String prepareToParseToDateStart = dateStart + timeStart + ":00";
            String prepareToParseToDateEnd = dateEnd + timeEnd + ":00";
            Date createStartDate = null;
            Date createEndDate = null;
            ArrayList<Date> dateResult = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

            try {
                createStartDate = simpleDateFormat.parse(prepareToParseToDateStart);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            try {
                createEndDate = simpleDateFormat.parse(prepareToParseToDateEnd);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            dateResult.add(createStartDate);
            dateResult.add(createEndDate);

            return dateResult;

        }
        return null;
    }

    public static boolean isTimeInThePast(Date date, Label errorDate, Label errorTime) {
        if (date.before(TimeConversion.getNowDate())) {
            errorDate.setText("Cannot be in the past.");
            errorTime.setText("Cannot be in the past.");
            return true;
        }
        return false;
    }

    public static void disableDateBeforeNow(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
    }
}
