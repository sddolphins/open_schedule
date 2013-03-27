package controllers;

import java.util.Iterator;
import java.util.List;

import models.Account;
import models.Facility;
import models.Location;
import models.Organization;

@Secure
public class Locations extends BaseController {

    public static void index(int accountId) {
        Account account = Account.findById(new Long(accountId));
        List<Organization> orgs = Organization.findByAccountId(accountId);
        render(account, orgs);
    }

    public static void create(int facId, String name) {
        Facility facility = Facility.findById(new Long(facId));
        new Location(name, facility);        
    }

    public static void delete(int locationId) {
        Location loc = Location.findById(new Long(locationId));
        loc.delete();
    }
}