package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarViewShift3Day {
    public long userId;
    public List<CalendarViewShift> shifts;

    public CalendarViewShift3Day(long userId) {
        this.userId = userId;
        shifts = new ArrayList<CalendarViewShift>();
    }
}