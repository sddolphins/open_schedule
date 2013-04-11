package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.JPA;
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

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    public List<MemberLocation> members = null;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    public List<Shift> shifts = null;

    public Location(String name, Facility facility) {
        this.name = name;
        this.facility = facility;
        this.dc = null;
        save();
    }

    public static List<Location> findByAccountId(int accountId) {
        String str = "select l.* from location l " +
                     "left join facility f on f.id = l.facility_id " +
                     "left join organization o on o.id = f.organization_id " +
                     "where o.account_id = " + accountId;
        Query query = JPA.em().createNativeQuery(str, Location.class);
        List<Location> locations = query.getResultList();
        return locations;
    }
}
