package controllers;

import java.util.List;

import models.Account;
import models.Billing;
import models.Plan;
import models.User;

@Secure
public class Accounts extends BaseController {

    public static void index() {
        User user = Application.connectedUser();
        List<Account> accounts = Account.findByOwnerId(user.id.intValue());
        render(user, accounts);
    }

    public static void create(String name) {
        User owner = Application.connectedUser();
        Plan plan = Plan.findByDescription("FREE");
        Billing billing = Billing.findById(new Long(1));
        Account account = new Account(name, owner, plan, billing);
    }
}