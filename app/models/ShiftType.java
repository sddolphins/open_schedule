package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "shift_type")
public class ShiftType extends Model {
    
    @Required
    public String name;
    
    public Timestamp dc;
    
    public ShiftType(String name) {
        this.name = name;
        this.dc = null;
        save();
    }
}
