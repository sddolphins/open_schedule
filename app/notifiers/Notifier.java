package notifiers;

import play.mvc.Mailer;

import javax.mail.internet.InternetAddress;

import models.User;

public class Notifier extends Mailer {

    public static boolean welcome(User user) {
        setSubject("Welcome %s", user.name);
        addRecipient(user.email);
        setFrom("Administrator <admin@openschedule.com>");
        setReplyTo("Help <help@openschedule.com>");
        return sendAndWait(user);
    }

    public static boolean access(User user, String scheduleName) {
        setSubject("Open Schedule Access");
        addRecipient(user.email);
        setFrom("Administrator <admin@openschedule.com>");
        setReplyTo("Help <help@openschedule.com>");
        return sendAndWait(user, scheduleName);
    }
}

