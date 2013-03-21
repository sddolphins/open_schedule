package controllers;

import java.util.ArrayList;
import java.util.Map;

import play.data.validation.*;
import play.libs.Codec;

import helpers.StatusMessage;
import models.Role;
import models.User;

@Secure
public class Users extends BaseController {

    public static void show() {
        User user = Application.connectedUser();
        render(user);
    }

    public static void update(Long id) {
        System.out.println("Users.update() - user id = " + id);
        //System.out.println("Users.update() - user id: " + params.get("id"));

        Map<String, String[]> values = params.all();
        String name = values.get("name")[0];
        String email = values.get("email")[0];
        String password = values.get("password")[0];
        String password2 = values.get("password2")[0];
        String role = values.get("role")[0];

        validation.required("Name", name);
        validation.required("Email", email);
        validation.email("Email", email);

        boolean pwdChanged = false;
        if (password != null && password.trim().length() > 0) {
            pwdChanged = true;
            validation.minSize("Password", password, 6);
            validation.equals("Password", password, "confirmation password", password2);
        }

        if (validation.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            ArrayList<play.data.validation.Error> errors = (ArrayList)validation.errors();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0)
                    sb.append("~");
                sb.append(errors.get(i).message());
            }
            renderJSON(new StatusMessage(StatusMessage.ERROR, sb.toString(), ""));
        }

        User user = User.findById(id);
        user.name = name;
        user.email = email;
        if (pwdChanged)
            user.passwordHash = Codec.hexMD5(password);
        user.role = Role.findByDescription(role);
        user.save();
        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }
}