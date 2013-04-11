package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "open_shift")
public class OpenShift extends Model {

    public Timestamp dc;

    @OneToOne(targetEntity = Shift.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    public Shift shift = null;

    @Required
    @Column(name = "num_needed")
    public int numNeeded;

    public OpenShift(Shift shift, int numNeeded) {
        this.shift = shift;
        this.numNeeded = numNeeded;
        this.dc = null;
        save();
    }

    public static OpenShift findByShiftId(Long shiftId) {
        return OpenShift.find("shift_id", shiftId).first();
    }
}
