package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarViewShiftDay {
    public long userId;
    public List<CalendarViewShift> shifts;

    public CalendarViewShiftDay(long userId) {
        this.userId = userId;
        shifts = new ArrayList<CalendarViewShift>();
    }
}