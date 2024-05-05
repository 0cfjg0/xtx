package com.itheima.xiaotuxian.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {


    public static String getMinusHoursDate(int hour, String format){
       return  LocalDateTime.now().minusHours(hour).format(DateTimeFormatter.ofPattern(format));
    }

    public static String getMinusWeeksDate(int week, String format){
        return  LocalDateTime.now().minusWeeks(week).format(DateTimeFormatter.ofPattern(format));
    }
}
