package controllers;

import play.Logger;

import helpers.StatusMessage;
import models.Account;
import models.Schedule;
import models.User;

@Secure
public class Settings extends BaseController {

    public static void index(int accountId, int scheduleId) {
        System.out.println("Settings.index(): accountId = " + accountId + ", scheduleId = " + scheduleId);
        User user = connectedUser();
        Account account = Account.findById(new Long(accountId));
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        render(user, account, schedule);
    }
}