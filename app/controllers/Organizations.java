package controllers;

import java.util.Iterator;
import java.util.List;

import models.Account;
import models.Facility;
import models.Organization;

@Secure
public class Organizations extends BaseController {

    public static void index(int accountId) {
        Account account = Account.findById(new Long(accountId));
        List<Organization> orgs = Organization.findByAccountId(accountId);
        render(account, orgs);
    }

    public static void create(int accountId, String name) {
        Account account = Account.findById(new Long(accountId));
        new Organization(name, account);
    }

    public static void delete(int orgId) {
        Organization org = Organization.findById(new Long(orgId));
        List<Facility> facilities = org.facilities;
        Iterator<Facility> it = facilities.iterator();
        while (it.hasNext()) {
            Facility fac = it.next();
            fac.delete();
        }
        org.delete();
    }
}