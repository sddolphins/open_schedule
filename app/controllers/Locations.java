package controllers;

import java.util.Iterator;
import java.util.List;

import models.Account;
import models.Facility;
import models.MemberLocation;
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

        // Remove location from all members.
        List<MemberLocation> memberLocations = loc.members;
        if (memberLocations != null && memberLocations.size() > 0) {
            for (int i = 0; i < memberLocations.size(); i++) {
                MemberLocation ml = memberLocations.get(i);
                ml.delete();
            }
        }

        // Remove location.
        loc.delete();
    }
}