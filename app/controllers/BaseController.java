package controllers;

import play.mvc.Before;
import play.mvc.Controller;

import models.User;

public class BaseController extends Controller {

    // @Before interceptors.
    @Before
    static void globals() {
        renderArgs.put("connected", connectedUser());
    }

    @Before
    static void checkSecure() {
        Secure secure = getControllerAnnotation(Secure.class);
        if (secure != null) {
            if (connectedUser() == null) {                
                flash.put("url", "GET".equals(request.method) ? request.url : "/");
                Application.signin();
                return;
            }
        }

        secure = getActionAnnotation(Secure.class);
        if (secure != null) {
            if (connectedUser() == null) {
                flash.put("url", "GET".equals(request.method) ? request.url : "/");
                Application.signin();
            }
        }
    }

    // Helper methods.
    static void connect(User user) {
        session.put("logged", user.id);
    }

    static User connectedUser() {
        String userId = session.get("logged");
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }

    static void redirectToOriginalURL() {
        String url = flash.get("url");
        if (url == null) {
            url = "/";
        }
        redirect(url);
    }
}