package models;

import java.util.Date;

public class EditShift {
    public long id;
    public Date start;
    public Date end;
    public int locationId;
    public int workShiftId;
    public int workTypeId;
    public int workSubtypeId;
    public String contact;
    public String comment;

    public EditShift(Shift shift) {
        this.id = shift.id.longValue();
        this.start = shift.dateStart;
        this.end = shift.dateEnd;
        this.locationId = shift.location.id.intValue();
        this.workShiftId = shift.shiftShift.id.intValue();
        this.workTypeId = shift.workType.id.intValue();
        this.workSubtypeId = shift.workSubtype.id.intValue();
        this.contact = shift.contact;
        this.comment = shift.comment;
    }
}