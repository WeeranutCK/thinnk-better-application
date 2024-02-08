package cs211.project.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeConversion {
    public static Date getDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        Date parsedDate = sdf.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);
        calendar.add(Calendar.HOUR_OF_DAY, getSystemTimeZoneOffset());
        return calendar.getTime();
    }

    public static String getFormattedDate(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, -calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        return sdf.format(calendar.getTime());
    }

    public static Date getNowDate() {
        Instant instant = Instant.now();
        return Date.from(instant);
    }

    public static int getSystemTimeZoneOffset() {
        TimeZone systemTimeZone = TimeZone.getDefault();
        return systemTimeZone.getRawOffset() / (60 * 60 * 1000);
    }

    public static String getUTCStringFormatted() {
        ZoneOffset offset = getZoneOffset();
        int totalSeconds = offset.getTotalSeconds();
        int hoursOffset = totalSeconds / 3600;
        int minutesOffset = (totalSeconds % 3600) / 60;
        return String.format("UTC%+03d:%02d", hoursOffset, minutesOffset);
    }

    public static ZoneOffset getZoneOffset() {
        return ZoneOffset.systemDefault().getRules().getOffset(java.time.Instant.now());
    }
}
