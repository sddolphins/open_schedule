package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "scheduled_shift")
public class ScheduledShift extends Model {
    
    public Timestamp dc;

    @OneToOne(targetEntity = Shift.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    public Shift shift = null;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    public Member member = null;
    
    public ScheduledShift(Shift shift, Member member) {
        this.shift = shift;
        this.member = member;
        this.dc = null;
        save();
    }

    public static ScheduledShift findByShiftId(Long shiftId) {
        return ScheduledShift.find("shift_id", shiftId).first();
    }
}
