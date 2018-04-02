package edu.upenn.cis350.g8.when2meetformobile;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saniyah on 4/2/2018.
 */

public class DayData {

    private static Map<String, Integer> dayToDate;
    private static Map<Integer, String> dateToDay;

    private static void initMaps() {
        dayToDate = new HashMap<String, Integer>();
        dayToDate.put("sun", Calendar.SUNDAY); //1
        dayToDate.put("mon", Calendar.MONDAY); //2
        dayToDate.put("tue", Calendar.TUESDAY); //3
        dayToDate.put("wed", Calendar.WEDNESDAY); //4
        dayToDate.put("thu", Calendar.THURSDAY); //5
        dayToDate.put("fri", Calendar.FRIDAY); //6
        dayToDate.put("sat", Calendar.SATURDAY); //7

        dateToDay = new HashMap<Integer, String>();
        dateToDay.put(Calendar.SUNDAY, "sun");
        dateToDay.put(Calendar.MONDAY, "mon");
        dateToDay.put(Calendar.TUESDAY, "tue");
        dateToDay.put(Calendar.WEDNESDAY, "wed");
        dateToDay.put(Calendar.THURSDAY, "thu");
        dateToDay.put(Calendar.FRIDAY, "fri");
        dateToDay.put(Calendar.SATURDAY, "sat");
    }

    public static int getDateFromDay(String day) {
        if (dayToDate == null) {
            initMaps();
        }
        return dayToDate.get(day);
    }

    public static String getDayFromDate(int date) {
        if (dateToDay == null) {
            initMaps();
        }
        return dateToDay.get(date);
    }

    public static String getDayOfWeekFromDateString(String date) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        String[] parts = date.split("/");
        c.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
        String day = getDayFromDate(c.get(Calendar.DAY_OF_WEEK));
        return day;
    }


}
