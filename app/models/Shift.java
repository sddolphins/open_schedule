package models;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;

import utils.DateUtils;

@Entity
@Table(name = "shift")
public class Shift extends Model {

    private static final int OPEN_SHIFT = 1;
    private static final int SELF_SCHEDULE_SHIFT = 2;

    @Required
    @Column(name = "date_start")
    public Date dateStart;

    @Required
    @Column(name = "date_end")
    public Date dateEnd;

    public String contact;
    public String comment;
    public boolean active;
    public Timestamp dc;

    @ManyToOne(targetEntity = Schedule.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    public Schedule schedule = null;

    @ManyToOne(targetEntity = Location.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    public Location location = null;

    @OneToOne(targetEntity = ShiftType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id")
    public ShiftType shiftType = null;

    @OneToOne(targetEntity = ShiftShift.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_shift_id")
    public ShiftShift shiftShift = null;

    @OneToOne(targetEntity = ShiftStatus.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_status_id")
    public ShiftStatus shiftStatus = null;

    @OneToOne(targetEntity = WorkType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id")
    public WorkType workType = null;

    @OneToOne(targetEntity = WorkSubtype.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_subtype_id")
    public WorkSubtype workSubtype = null;

    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    public List<ShiftRestriction> restrictions = null;

    public Shift(Date dateStart, Date dateEnd, Schedule schedule, Location location,
                 ShiftType shiftType, ShiftShift shiftShift, ShiftStatus shiftStatus,
                 WorkType workType, WorkSubtype workSubtype, String contact, String comment) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.schedule = schedule;
        this.location = location;
        this.shiftType = shiftType;
        this.shiftShift = shiftShift;
        this.shiftStatus = shiftStatus;
        this.workType = workType;
        this.workSubtype = workSubtype;
        this.contact = contact;
        this.comment = comment;
        this.active = true; // Default to active.
        this.dc = null;
        save();
    }

    public static List<Shift> findByScheduleId(int scheduleId) {
        return Shift.find("schedule_id", scheduleId).fetch();
    }

    public static List<Shift> findByLocationId(int locationId) {
        return Shift.find("location_id", locationId).fetch();
    }

    public static List<Shift> findByShiftTypeId(int shiftTypeId) {
        return Shift.find("shift_type_id", shiftTypeId).fetch();
    }

    public static List<Shift> findByShiftShiftId(int shiftShiftId) {
        return Shift.find("shift_shift_id", shiftShiftId).fetch();
    }

    public static List<Shift> findByShiftStatusId(int shiftStatusId) {
        return Shift.find("shift_status_id", shiftStatusId).fetch();
    }

    public static List<CalendarViewShift> findCalendarViewShifts(Date startDate, int viewByDays,
                                                                 int scheduleId, int locationId,
                                                                 int jobTitleId) {
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate);
        endDate.set(Calendar.HOUR, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.add(Calendar.DATE, viewByDays);

        List<CalendarViewShift> ret = new ArrayList<CalendarViewShift>();

        // @debug.
        System.out.println("Shifts.findCalendarViewShifts - startDate: " + startDate.toString() + ", endDate: " + endDate.getTime().toString());
        System.out.println("Shifts.findCalendarViewShifts - schedule id: " + scheduleId + ", location id: " + locationId);

        List<Shift> shifts =  Shift.find("schedule_id = ? and location_id = ? and " +
                                         "date_start >= ? and date_start < ? and active = 1",
                                         scheduleId, locationId, startDate, endDate.getTime()).fetch();
        // @debug.
        //System.out.println("Shifts.findCalendarViewShifts - shifts found: " + shifts.size());

        Iterator<Shift> it = shifts.iterator();
        while (it.hasNext()) {
            Shift shift = it.next();

            // Copy info to be displayed.
            CalendarViewShift cs = new CalendarViewShift();
            cs.id = shift.id;
            cs.type = shift.shiftType.id.intValue();
            cs.start = shift.dateStart;
            cs.end = shift.dateEnd;

            // Copy specific shift info based on shift type.
            boolean addShift = false;
            boolean foundRestrictions = false;
            Iterator<ShiftRestriction> srIt = null;

            switch (shift.shiftType.id.intValue()) {
                case 1: // Postable/Open.
                    // Are there any job title restrictions?
                    srIt = shift.restrictions.iterator();
                    while (srIt.hasNext()) {
                        ShiftRestriction sr = srIt.next();
                        if (sr.jobTitle != null) {
                            //System.out.println("found matching job title restriction: " + sr.jobTitle.id);
                            foundRestrictions = true;
                            if (sr.jobTitle.id.intValue() == jobTitleId) {
                                addShift = true;
                                break;
                            }
                        }
                    }

                    // If no restrictions found, then everyone can access the shift.
                    if (!foundRestrictions)
                        addShift = true;

                    if (addShift) {
                        JobTitle jt = JobTitle.findById(new Long(jobTitleId));
                        cs.name = "Open";
                        cs.image = "/public/images/o.png";
                        cs.color = jt.openShiftColor;
                    }
                    break;
                 case 2: // Scheduled.
                    ScheduledShift ss = ScheduledShift.findByShiftId(shift.id);
                    if (ss.member.jobTitleId != jobTitleId) { // Not matching job title.
                        //System.out.println("Skip job title id: " + ss.member.jobTitleId);
                        continue;
                    }

                    addShift = true;
                    JobTitle jt = JobTitle.findById(new Long(ss.member.jobTitleId));
                    cs.name = ss.member.user.name;
                    cs.image = "http://www.gravatar.com/avatar/" +
                               ss.member.user.gravatarHash(ss.member.user.email) + "?s=22";
                    cs.color = jt.color;
                    break;
                case 3: // Selft-schedule.
                    // Are there any job title restrictions?
                    srIt = shift.restrictions.iterator();
                    while (srIt.hasNext()) {
                        ShiftRestriction sr = srIt.next();
                        if (sr.jobTitle != null) {
                            //System.out.println("found matching job title restriction: " + sr.jobTitle.id);
                            foundRestrictions = true;
                            if (sr.jobTitle.id.intValue() == jobTitleId) {
                                addShift = true;
                                break;
                            }
                        }
                    }

                     // If no restrictions found, then everyone can access the shift.
                    if (!foundRestrictions)
                        addShift = true;

                   if (addShift) {
                        cs.name = "Self-schedule";
                        cs.image = "/public/images/ss.png";
                        cs.color = "#7f7f7f";
                    }
                    break;
            }

            if (addShift) {
                ret.add(cs);
            }
        }
        return ret;
    }

    public static List<CalendarViewShiftDay> findCalendarViewShiftsDay(Date startDate, int viewByDays,
                                                                       int scheduleId, int locationId,
                                                                       int jobTitleId) {
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate);
        endDate.set(Calendar.HOUR, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.add(Calendar.DATE, viewByDays-1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<CalendarViewShiftDay> ret = new ArrayList<CalendarViewShiftDay>();

        // @debug.
        System.out.println("Shifts.findCalendarViewShiftsDay - startDate: " + startDate.toString() + ", endDate: " + endDate.getTime().toString());
        System.out.println("Shifts.findCalendarViewShiftsDay - schedule id: " + scheduleId + ", location id: " + locationId + ", job title id: " + jobTitleId);

        // Scheduled shifts.
        String queryStr = "select m.user_id, s.id, u.name, u.email, s.date_start, s.date_end, jt.color " +
                          "from shift s "+
                          "inner join scheduled_shift ss on ss.shift_id = s.id " +
                          "left join member m on ss.member_id = m.id " +
                          "left join user u on m.user_id = u.id " +
                          "left join job_title jt on jt.id = m.job_title_id " +
                          "where m.job_title_id = " + jobTitleId +
                          "  and s.schedule_id = " + scheduleId +
                          "  and s.location_id = " + locationId +
                          "  and s.date_start >= '" + sdf.format(startDate) + "'" +
                          "  and s.date_start < '" + sdf.format(endDate.getTime()) + "'" +
                          "  and s.active = 1 " +
                          "order by u.id, s.date_start";
        Query query = JPA.em().createNativeQuery(queryStr);
        List<Object[]> results = query.getResultList();

        if (results.size() > 0) {
            long prevUserId = 0;
            CalendarViewShiftDay cs3day = null;

            for (Object[] objects : results) {
                long userId = ((BigInteger)objects[0]).longValue();
                System.out.println("Shifts.findCalendarViewShiftsDay - user id: " + userId);
                if (userId != prevUserId) {
                    if (cs3day != null) {
                        ret.add(cs3day);
                    }
                    prevUserId = userId;
                    cs3day = new CalendarViewShiftDay(userId);
                }

                if (cs3day != null) {
                    CalendarViewShift cs = new CalendarViewShift();
                    cs.id = ((BigInteger)objects[1]).intValue();
                    System.out.println("Shifts.findCalendarViewShiftsDay - id: " + cs.id);
                    cs.name = (String)objects[2];
                    String email = (String)objects[3];
                    cs.image = "http://www.gravatar.com/avatar/" + User.gravatarHash(email) + "?s=22";
                    Timestamp ts = (Timestamp)objects[4];
                    cs.start = new Date(ts.getTime());
                    ts = (Timestamp)objects[5];
                    cs.end = new Date(ts.getTime());
                    System.out.println("Shifts.findCalendarViewShiftsDay - start: " + cs.start.toString() + ", end: " + cs.end.toString());
                    cs.color = (String)objects[6];

                    cs3day.shifts.add(cs);
                }
            }

            if (cs3day != null) {
                ret.add(cs3day);
            }
        }

        // Open shifts.
        List<CalendarViewShiftDay> openShifts = getShiftData(OPEN_SHIFT,
                                                             startDate, endDate.getTime(),
                                                             viewByDays, scheduleId,
                                                             locationId, jobTitleId);
        Iterator<CalendarViewShiftDay> osIt = openShifts.iterator();
        while (osIt.hasNext()) {
            CalendarViewShiftDay cs3day = osIt.next();
            ret.add(cs3day);
        }

        // Self-schedule shifts.
        List<CalendarViewShiftDay> ssShifts = getShiftData(SELF_SCHEDULE_SHIFT,
                                                           startDate, endDate.getTime(),
                                                           viewByDays, scheduleId,
                                                           locationId, jobTitleId);
        Iterator<CalendarViewShiftDay> ssIt = ssShifts.iterator();
        while (ssIt.hasNext()) {
            CalendarViewShiftDay cs3day = ssIt.next();
            ret.add(cs3day);
        }

        return ret;
    }

    private static List<CalendarViewShiftDay> getShiftData(int shiftType,
                                                           Date startDate, Date endDate,
                                                           int viewByDays, int scheduleId,
                                                           int locationId, int jobTitleId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<CalendarViewShiftDay> shifts = new ArrayList<CalendarViewShiftDay>();

        String queryStr;
        if (shiftType == OPEN_SHIFT) {
            queryStr = "select s.* from shift s " +
                       "inner join open_shift os on os.shift_id = s.id " +
                       "where s.schedule_id = " + scheduleId +
                       "  and s.location_id = " + locationId +
                       "  and s.date_start >= '" + sdf.format(startDate) + "'" +
                       "  and s.date_start < '" + sdf.format(endDate) + "'" +
                       "  and s.active = 1 " +
                       "order by s.date_start";
        }
        else if (shiftType == SELF_SCHEDULE_SHIFT) {
            queryStr = "select s.* from shift s " +
                       "inner join self_schedule_shift sss on sss.shift_id = s.id " +
                       "where s.schedule_id = " + scheduleId +
                       "  and s.location_id = " + locationId +
                       "  and s.date_start >= '" + sdf.format(startDate) + "'" +
                       "  and s.date_start < '" + sdf.format(endDate) + "'" +
                       "  and s.active = 1 " +
                       "order by s.date_start";
        }
        else {
            return shifts;
        }

        Query query = JPA.em().createNativeQuery(queryStr, Shift.class);
        List<Shift> shiftData = query.getResultList();

        if (shiftData != null && shiftData.size() > 0) {
            JobTitle jt = JobTitle.findById(new Long(jobTitleId));

            Iterator<Shift> it = shiftData.iterator();
            while (it.hasNext()) {
                Shift shift = it.next();

                // Check for any job title restrictions.
                boolean addShift = false;
                boolean foundRestrictions = false;
                Iterator<ShiftRestriction> srIt = shift.restrictions.iterator();
                while (srIt.hasNext()) {
                    ShiftRestriction sr = srIt.next();
                    if (sr.jobTitle != null) {
                        System.out.println("found job title restriction: " + sr.jobTitle.id);
                        foundRestrictions = true;
                        if (sr.jobTitle.id.intValue() == jobTitleId) {
                            addShift = true;
                            break;
                        }
                    }
                }

                // If no restrictions found, then everyone can access the shift.
                if (!foundRestrictions)
                    addShift = true;

                if (addShift) {
                    // Copy shift info.
                    CalendarViewShift cs = new CalendarViewShift();
                    cs.id = shift.id.longValue();
                    cs.start = shift.dateStart;
                    cs.end = shift.dateEnd;
                    if (shiftType == OPEN_SHIFT) {
                        cs.name = "Open";
                        cs.image = "/public/images/o.png";
                        cs.color = jt.openShiftColor;
                    }
                    else {
                        cs.name = "Self-schedule";
                        cs.image = "/public/images/ss.png";
                        cs.color = "#7f7f7f";
                    }

                    // Find correct place/row to insert shift.
                    int row = shifts.size();
                    boolean insertNewRow = true;
                    Iterator<CalendarViewShiftDay> osIt = shifts.iterator();
                    while (osIt.hasNext()) {
                        CalendarViewShiftDay cs3day = osIt.next();

                        // Skip over rows that's already full or contained
                        // shifts for each day.
                        if (cs3day.shifts.size() == viewByDays)
                            continue;

                        // Do not allow more than 1 shifts with same start date
                        // on the same row.
                        boolean sameStartDate = false;
                        Iterator<CalendarViewShift> cvsIt = cs3day.shifts.iterator();
                        while (cvsIt.hasNext()) {
                            CalendarViewShift cvs = cvsIt.next();
                            if (DateUtils.compare(cvs.start, shift.dateStart) == 0) {
                                sameStartDate = true;
                                break;
                            }
                        }

                        if (!sameStartDate) {
                            cs3day.shifts.add(cs);
                            insertNewRow = false;
                            break;
                        }
                    }

                    if (insertNewRow) {
                        row++;
                        CalendarViewShiftDay cs3day = new CalendarViewShiftDay(row);
                        cs3day.shifts.add(cs);
                        shifts.add(cs3day);
                    }
                }
            }
        }

        return shifts;
    }
}
