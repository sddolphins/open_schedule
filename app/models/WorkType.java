package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "work_type")
public class WorkType extends Model {
    
    @Required
    public String name;
    
    public Timestamp dc;
    
    public WorkType(String name) {
        this.name = name;
        this.dc = null;
        save();
    }
}
