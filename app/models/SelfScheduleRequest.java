package models;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "self_schedule_request")
public class SelfScheduleRequest extends Model {

    @Required
    @Column(name = "date_requested")
    public Date dateRequested;

    @Column(name = "date_approved_denied")
    public Date dateApprovedDenied;

    public String comment;

    @Column(name = "manager_comment")
    public String managerComment;

    public Timestamp dc;

    @ManyToOne(targetEntity = SelfScheduleShift.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "self_schedule_shift_id")
    public SelfScheduleShift sss = null;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    public Member member = null;

    @ManyToOne(targetEntity = ShiftRequestStatus.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_status_id")
    public ShiftRequestStatus status = null;

    public SelfScheduleRequest(SelfScheduleShift sss, Member member,
                               String comment, String managerComment) {
        this.sss = sss;
        this.member = member;
        this.status = ShiftRequestStatus.findById(new Long(1)); // Pending.
        this.dateRequested = new Date();
        this.comment = comment;
        this.managerComment = managerComment;
        this.dc = null;
        save();
    }
}
