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
@Table(name = "schedule")
public class Schedule extends Model {

    @Required
    public String name;

    public Timestamp dc;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account account = null;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    public List<Member> members = null;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    public List<Shift> shifts = null;

    public Schedule(String name, Account account) {
        this.name = name;
        this.account = account;
        this.dc = null;
        save();
    }
}
