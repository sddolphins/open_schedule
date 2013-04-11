package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "billing")
public class Billing extends Model {

    @Required
    @Column(name = "holder_name")
    public String holderName;

    @Required
    @Column(name = "account_number")
    public String accountNumber;

    @Required
    @Column(name = "exp_month")
    public int expMonth;

    @Required
    @Column(name = "exp_year")
    public int expYear;

    @Required
    public String country;

    public String state;
    public String zip;
    public Timestamp dc;

    @ManyToOne(targetEntity = BillingType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_type_id")
    public BillingType billingType = null;

    @ManyToOne(targetEntity = CreditCardType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_card_type_id")
    public CreditCardType creditCardType = null;

    public Billing(BillingType billingType, CreditCardType creditCardType,
                   String holderName, String accountNumber, int expMonth,
                   int expYear, String country, String state, String zip) {
        this.billingType = billingType;
        this.creditCardType = creditCardType;
        this.holderName = holderName;
        this.accountNumber = accountNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.country = country;
        this.state = state;
        this.zip = zip;
        this.dc = null;
        save();
    }
}
