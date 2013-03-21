package controllers;

import play.*;
import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.MinSize;
import play.data.validation.Required;

import models.User;
import notifiers.Notifier;

public class Application extends BaseController {

    public static void index() {
        render();
    }

    public static void signup() {
        System.out.println("Application.signup()");
        render();
    }

    public static void register(@Required @Email String email,
                                @Required @MinSize(6) String password,
                                @Equals("password") String password2,
                                @Required String name) {
        System.out.println("Application.register()");
        if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            flash.error("Please correct these errors !");
            signup();
        }

        User user = new User(email, password, name);
        try {
            System.out.println("Notify user to check email");
            if (Notifier.welcome(user)) {
                flash.success("Your account is created. Please check your emails ...");
                signin();
            }
        }
        catch (Exception e) {
            Logger.error(e, "Mail error");
        }

        flash.error("Oops ... (the email cannot be sent)");
        signin();
    }

    public static void signin() {
        System.out.println("Application.signin()");
        render();
    }

    public static void authenticate(String email, String password) {
        System.out.println("Application.authenticate()");
        User user = User.findByEmail(email);
        if (user == null || !user.checkPassword(password)) {
            System.out.println("Bad email or bad password");
            flash.error("Bad email or bad password");
            flash.put("email", email);
            signin();
        }
        else if (user.needConfirmation != null) {
            System.out.println("This account is not confirmed");
            flash.error("This account is not confirmed");
            flash.put("notconfirmed", user.needConfirmation);
            flash.put("email", email);
            signin();
        }

        System.out.println("Authenticated");
        connect(user);
        flash.success("Welcome back %s !", user.name);
        index();
    }

    public static void confirmRegistration(String uuid) {
        System.out.println("Application.confirmRegistration()");
        User user = User.findByRegistrationUUID(uuid);
        notFoundIfNull(user);
        user.needConfirmation = null;
        user.save();
        connect(user);
        flash.success("Welcome %s !", user.name);
        index();
    }

    public static void resendConfirmation(String uuid) {
        System.out.println("Application.resendConfirmation()");
        User user = User.findByRegistrationUUID(uuid);
        notFoundIfNull(user);

        try {
            if (Notifier.welcome(user)) {
                flash.success("Please check your emails ...");
                flash.put("email", user.email);
                signin();
            }
        }
        catch (Exception e) {
            Logger.error(e, "Mail error");
        }
        
        flash.error("Oops (the email cannot be sent)...");
        flash.put("email", user.email);
        signin();
    }

    public static void signout() {
        flash.success("You've been logged out");
        session.clear();
        index();
    }

}
