package models;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "user_role")
@AssociationOverrides ({
    @AssociationOverride(name = "pk.user", joinColumns = @JoinColumn(name = "user_id")),
    @AssociationOverride(name = "pk.role", joinColumns = @JoinColumn(name = "role_id"))
})
public class UserRole extends GenericModel {

    /**
     * @EmbeddedId represents a primary class that is embedded in a bean class.
     */
    @EmbeddedId
    public UserRolePk pk = new UserRolePk();
    
    public UserRole(UserRolePk pk) {
        this.pk = pk;
        save();
    }
}
