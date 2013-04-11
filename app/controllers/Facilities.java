package controllers;

import java.util.Iterator;
import java.util.List;

import models.Organization;
import models.Facility;

@Secure
public class Facilities extends BaseController {

    public static void create(int orgId, String name) {
        Organization org = Organization.findById(new Long(orgId));
        new Facility(name, org);
    }

    public static void delete(int facilityId) {
        Facility facility = Facility.findById(new Long(facilityId));
        facility.delete();
    }
}