package models;

import java.util.List;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "plan")
public class Plan extends Model {
    
    @Required
    public String description;

    @Required
    public int schedules;

    @Required
    public int members;

    @Required
    public int cost;

    public Timestamp dc;

    public Plan(String description, int schedules, int members, int cost) {
        this.description = description;
        this.schedules = schedules;
        this.members = members;
        this.cost = cost;
        this.dc = null;
        save();
    }

    public double getAnnualCost() {
        double annualCost = (this.cost * 12 * .80);
        return annualCost;
    }

    public double getSavingAmount() {
        double savingAmt = (this.cost * 12 * .20);
        return savingAmt;
    }

    public static List<Plan> findAllReverseOrder() {
        List<Plan> plans = Plan.find("order by id desc").fetch();
        return plans;
    }

    public static Plan findByDescription(String desc) {
        return find("description", desc).first();
    }
}
