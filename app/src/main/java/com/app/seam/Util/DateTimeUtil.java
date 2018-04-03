package com.app.seam.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Colinares on 3/28/2018.
 */

public class DateTimeUtil {

    public static String currentTime(){
        Calendar calendar = Calendar.getInstance(Locale.US);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);

        return format.format(calendar.getTimeInMillis());
    }

    public static String currentDate() {
        Calendar calendar = Calendar.getInstance(Locale.US);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        return format.format(calendar.getTimeInMillis());
    }


}
