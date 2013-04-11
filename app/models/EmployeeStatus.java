package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "employee_status")
public class EmployeeStatus extends Model {

    @Required
    public String status;

    public Timestamp dc;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public User owner = null;

    public EmployeeStatus(String status, User owner) {
        this.status = status;
        this.owner = owner;
        this.dc = null;
        save();
    }
}
