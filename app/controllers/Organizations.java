package controllers;

import java.util.Iterator;
import java.util.List;

import models.Facility;
import models.Organization;
import models.User;

@Secure
public class Organizations extends BaseController {

    public static void index() {
        User user = Application.connectedUser();
        List<Organization> orgs = Organization.findByOwner(user.id.intValue());
        render(user, orgs);
    }

    public static void create(String name) {
        User owner = Application.connectedUser();
        Organization org = new Organization(name, owner);
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