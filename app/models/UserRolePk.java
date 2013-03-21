package models;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import play.db.jpa.GenericModel;

@Embeddable
public class UserRolePk extends GenericModel {
    
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    public User user = null;

    @ManyToOne(targetEntity = Role.class, fetch = FetchType.LAZY)
    public Role role = null;

    public UserRolePk() {
    }

    public UserRolePk(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
