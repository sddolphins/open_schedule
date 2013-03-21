package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;
import play.Play;

@Entity
@Table(name = "user")
public class User extends Model {

    @Email
    @Required
    public String email;

    @Required
    public String passwordHash;

    @Required
    public String name;

    public String needConfirmation;
    public Timestamp dc;

    @ManyToOne(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    public Role role = null;

    public User(String email, String password, String name) {
        this(email, password, name, Role.findByDescription("member"));
    }

    public User(String email, String password, String name, Role role) {
        this.email = email;
        this.passwordHash = Codec.hexMD5(password);
        this.name = name;
        this.needConfirmation = Codec.UUID();
        this.role = role;
        this.dc = null;
        save();
    }

    public boolean checkPassword(String password) {
        return passwordHash.equals(Codec.hexMD5(password));
    }

    public boolean isAdmin() {
        return email.equals(Play.configuration.getProperty("adminEmail", ""));
    }

    public static String gravatarHash(String gravatarId) {
        if (gravatarId != null)
            return Codec.hexMD5(gravatarId.toLowerCase().trim());
        return null;
    }

    public static User findByEmail(String email) {
        return find("email", email).first();
    }

    public static User findByRegistrationUUID(String uuid) {
        return find("needConfirmation", uuid).first();
    }

    public static List<User> findAll(int page, int pageSize) {
        return User.all().fetch(page, pageSize);
    }

    public static boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }
}
