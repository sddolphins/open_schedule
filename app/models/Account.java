package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "account")
public class Account extends Model {
    
    @Required
    public String name;
    
    public Timestamp dc;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public User owner = null;

    @OneToOne(targetEntity = Plan.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    public Plan plan = null;

    @OneToOne(targetEntity = Billing.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id")
    public Billing billing = null;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    public List<Schedule> schedules = null;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    public List<Member> members = null;

    public Account(String name, User owner, Plan plan, Billing billing) {
        this.name = name;
        this.owner = owner;
        this.plan = plan;
        this.billing = billing;
        this.dc = null;
        save();
    }

    public Plan getPlan() {
        return this.plan;
    }

    public static List<Account> findByOwnerId(int ownerId) {
        return Account.find("owner_id", ownerId).fetch();
    }
}
