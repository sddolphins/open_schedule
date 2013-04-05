package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "shift_restriction")
public class ShiftRestriction extends Model {
    
    public boolean active;
    public Timestamp dc;

    @ManyToOne(targetEntity = Shift.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    public Shift shift = null;
    
    @ManyToOne(targetEntity = Location.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    public Location location = null;
    
    @ManyToOne(targetEntity = JobTitle.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_title_id")
    public JobTitle jobTitle = null;
    
    public ShiftRestriction(Shift shift, Location location, JobTitle jobTitle,
                            boolean active) {
        this.shift = shift;
        this.location = location;
        this.jobTitle = jobTitle;
        this.active = active;
        this.dc = null;
        save();
    }

    public static List<ShiftRestriction> findByShiftId(int shiftId) {
        return ShiftRestriction.find("shift_id", shiftId).fetch();
    }
}
