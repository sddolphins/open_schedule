package controllers;

import play.Logger;

import helpers.StatusMessage;
import models.Account;
import models.Schedule;
import models.User;

@Secure
public class Settings extends BaseController {

    public static void index(int scheduleId) {
        User user = connectedUser();
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        render(user, schedule);
    }
}