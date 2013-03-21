package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "facility")
public class Facility extends Model {
    
    @Required
    public String name;
    
    public Timestamp dc;

    @ManyToOne(targetEntity = Organization.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    public Organization organization = null;

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    public List<Location> locations = null;

    public Facility(String name, Organization organization) {
        this.name = name;
        this.organization = organization;
        this.dc = null;
        save();
    }
}
