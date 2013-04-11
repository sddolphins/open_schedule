package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "self_schedule_shift")
public class SelfScheduleShift extends Model {

    @Column(name = "signup_date_start")
    public Date signupDateStart;

    @Required
    @Column(name = "num_needed")
    public int numNeeded;

    @Column(name = "num_awarded")
    public int numAwarded;

    @Column(name = "num_filled")
    public int numFilled;

    public Timestamp dc;

    @OneToOne(targetEntity = Shift.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    public Shift shift = null;

    @OneToMany(mappedBy = "sss", fetch = FetchType.LAZY)
    public List<SelfScheduleRequest> requests = null;

    public SelfScheduleShift(Shift shift, Date signupDateStart, int numNeeded) {
        this.shift = shift;
        this.signupDateStart = signupDateStart;
        this.numNeeded = numNeeded;
        this.dc = null;
        save();
    }

    public static SelfScheduleShift findByShiftId(Long shiftId) {
        return SelfScheduleShift.find("shift_id", shiftId).first();
    }
}
