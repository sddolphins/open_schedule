package controllers;

import java.util.Iterator;
import java.util.List;

import models.Facility;
import models.Location;
import models.Organization;
import models.User;

@Secure
public class Locations extends BaseController {

    public static void index() {
        User user = Application.connectedUser();
        List<Organization> orgs = Organization.findByOwner(user.id.intValue());
        render(user, orgs);
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