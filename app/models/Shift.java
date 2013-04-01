package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "shift")
public class Shift extends Model {
    
    @Required
    @Column(name = "date_start")
    public Date dateStart;
    
    @Required
    @Column(name = "date_end")
    public Date dateEnd;

    public String contact;
    public String comment;
    public Timestamp dc;

    @ManyToOne(targetEntity = Schedule.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    public Schedule schedule = null;

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

    public Shift(Date dateStart, Date dateEnd, Schedule schedule, ShiftType shiftType,
                 ShiftShift shiftShift, ShiftStatus shiftStatus, WorkType workType,
                 WorkSubtype workSubtype, String contact, String comment) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.schedule = schedule;
        this.shiftType = shiftType;
        this.shiftShift = shiftShift;
        this.shiftStatus = shiftStatus;
        this.workType = workType;
        this.workSubtype = workSubtype;
        this.contact = contact;
        this.comment = comment;
        this.dc = null;
        save();
    }

    public static List<Shift> findByScheduleId(int scheduleId) {
        return Shift.find("schedule_id", scheduleId).fetch();
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
}
