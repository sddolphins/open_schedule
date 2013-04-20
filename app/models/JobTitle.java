package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "job_title")
public class JobTitle extends Model {

    @Required
    public String name;

    @Column(name = "open_shift_color")
    public String openShiftColor;

    public String color;
    public Timestamp dc;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account account = null;

    public JobTitle(String name, String color, String openShiftColor, Account account) {
        this.name = name;
        if (color != null && color.trim().length() > 0)
            this.color = color;
        if (openShiftColor != null && openShiftColor.trim().length() > 0)
            this.openShiftColor = openShiftColor;
        this.account = account;
        this.dc = null;
        save();
    }

    public static List<JobTitle> findByAccountId(int accountId) {
        return JobTitle.find("account_id", accountId).fetch();
    }
}
