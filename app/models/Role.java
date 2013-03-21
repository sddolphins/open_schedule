package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "role")
public class Role extends Model {
    
    @Required
    public String description;
    
    public Timestamp dc;

    public Role(String description) {
        this.description = description;
        this.dc = null;
        save();
    }

    public static Role findByDescription(String desc) {
        return find("description", desc).first();
    }
}
