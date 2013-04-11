package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "shift_status")
public class ShiftStatus extends Model {

    @Required
    public String name;

    @Required
    @Column(name = "sort_order")
    public int sortOrder;

    public Timestamp dc;

    public ShiftStatus(String name, int sortOrder) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.dc = null;
        save();
    }

    public static ShiftStatus findByName(String name) {
        return find("name", name).first();
    }
}
