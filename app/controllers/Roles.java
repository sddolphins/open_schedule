package controllers;

import play.mvc.Controller;

import models.Role;

public class Roles extends Controller {

    public static void getAll() {
        renderJSON(Role.findAll());
    }
}