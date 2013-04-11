package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "credit_card_type")
public class CreditCardType extends Model {

    @Required
    public String description;

    public Timestamp dc;

    public CreditCardType(String description) {
        this.description = description;
        this.dc = null;
        save();
    }

    public static CreditCardType findByDescription(String desc) {
        return find("description", desc).first();
    }
}
