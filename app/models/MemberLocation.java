package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Table(name = "member_location")
@IdClass(MemberLocationPk.class)
public class MemberLocation extends GenericModel {
    
    @Id
    @Column(name = "member_id")
    public long memberId;

    @Id
    @Column(name = "location_id")
    public int locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false, insertable = false)
    public Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", updatable = false, insertable = false)
    public Location location;

    public MemberLocation(long memberId, int locationId) {
        this.memberId = memberId;
        this.locationId = locationId;
        save();
    }
}
