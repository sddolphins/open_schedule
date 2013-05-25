package models;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.ScheduledShift;
import utils.DateUtils;

public class PopoverShift {
    public long id;
    public String date;
    public String time;
    public String duration;
    public int type;
    public String memberImage;
    public String memberName;
    public String memberTitle;

    public PopoverShift(Shift shift) {
        SimpleDateFormat df = new SimpleDateFormat("E, MMM d, yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("HHmm");
        this.id = shift.id.longValue();
        this.date = df.format(shift.dateStart);
        this.time = tf.format(shift.dateStart) + "-" + tf.format(shift.dateEnd);
        this.duration = DateUtils.duration(shift.dateEnd, shift.dateStart) + " hours";
        this.type = shift.shiftType.id.intValue();

        // If scheduled shift, get scheduled member info.
        if (this.type == 2) {
            ScheduledShift ss = ScheduledShift.findByShiftId(shift.id);
            this.memberImage = "http://www.gravatar.com/avatar/" +
                               ss.member.user.gravatarHash(ss.member.user.email) + "?s=50";
            this.memberName = ss.member.user.name;
            this.memberTitle = ss.member.jobTitle.name;
        }
    }
}