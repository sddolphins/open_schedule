package utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * Compares 2 dates.
     * @param  date1
     * @param  date2
     * @return = 0  -> date1 = date1
     *         < 0  -> date1 < date2
     *         > 0  -> date1 > date2
     */
    public static int compare(Date date1, Date date2) {
        Calendar d1 = Calendar.getInstance();
        d1.setTime(date1);
        d1.set(Calendar.HOUR, 0);
        d1.set(Calendar.MINUTE, 0);
        d1.set(Calendar.SECOND, 0);

        Calendar d2 = Calendar.getInstance();
        d2.setTime(date2);
        d2.set(Calendar.HOUR, 0);
        d2.set(Calendar.MINUTE, 0);
        d2.set(Calendar.SECOND, 0);

        return d1.compareTo(d2);
    }
}