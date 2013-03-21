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
@Table(name = "location")
public class Location extends Model {
    
    @Required
    public String name;
    
    public Timestamp dc;

    @ManyToOne(targetEntity = Facility.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    public Facility facility = null;

    public Location(String name, Facility facility) {
        this.name = name;
        this.facility = facility;
        this.dc = null;
        save();
    }
}
