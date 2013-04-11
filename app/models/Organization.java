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

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account account = null;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    public List<Facility> facilities = null;

    public Organization(String name, Account account) {
        this.name = name;
        this.account = account;
        this.dc = null;
        save();
    }

    public static List<Organization> findByAccountId(int accountId) {
        return Organization.find("account_id", accountId).fetch();
    }
}
