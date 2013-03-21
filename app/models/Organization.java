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
@Table(name = "organization")
public class Organization extends Model {
    
    @Required
    public String name;
    
    public Timestamp dc;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public User owner = null;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    public List<Facility> facilities = null;

    public Organization(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.dc = null;
        save();
    }

    public static List<Organization> findByOwner(int ownerId) {
        return Organization.find("owner_id", ownerId).fetch();
    }
}
