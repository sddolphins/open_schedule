package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "overtime_type")
public class OvertimeType extends Model {

    @Required
    public String name;

    @Required
    public double multiplier;

    public Timestamp dc;

    public OvertimeType(String name, double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
        this.dc = null;
        save();
    }
}
