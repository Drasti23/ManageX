package ca.gbc.managex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeAndDate {
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    public static String getCurrentDataAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd MMM yyyy",Locale.getDefault());
        return sdf.format(new Date());
    }
}
