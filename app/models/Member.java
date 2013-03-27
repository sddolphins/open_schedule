package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "member",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id",
                                                             "account_id",
                                                             "schedule_id"})})
public class Member extends Model {
    
    public String address;
    public String city;
    public String state;
    public String zip;
    public String country;
    public String phone;

    @Column(name = "hire_date")
    public Date hireDate;

    @Column(name = "base_pay")
    public double basePay = 0.0;

    @Column(name = "job_title_id")
    public int jobTitleId;

    @Column(name = "location_id")
    public int locationId;

    @Column(name = "employee_status_id")
    public int employeeStatusId;

    public Timestamp dc;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user = null;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account account = null;

    @ManyToOne(targetEntity = Schedule.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    public Schedule schedule = null;

    public Member(User user, Account account, Schedule schedule) {
        this.user = user;
        this.account = account;
        this.schedule = schedule;
        this.dc = null;
        save();
    }

    public static Member findByUserId(Long userId) {
        return Member.find("user_id", userId).first();
    }

    public static int getCountByAccountId(Long accountId) {
        int memberCount = (int)Member.count("account_id=?", accountId);
        return memberCount;
    }
}
