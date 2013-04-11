package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "billing_type")
public class BillingType extends Model {

    @Required
    public String description;

    public Timestamp dc;

    public BillingType(String description) {
        this.description = description;
        this.dc = null;
        save();
    }

    public static BillingType findByDescription(String desc) {
        return find("description", desc).first();
    }
}
