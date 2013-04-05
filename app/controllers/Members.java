package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.data.validation.*;
import play.libs.Codec;
import play.Logger;

import helpers.StatusMessage;
import models.Account;
import models.Location;
import models.Member;
import models.MemberLocation;
import models.MemberLocationPk;
import models.Role;
import models.Schedule;
import models.User;
import notifiers.Notifier;

@Secure
public class Members extends BaseController {

    public static void index(int scheduleId) {
        User user = connectedUser();
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        List<Member> members = schedule.members;
        List<Role> roles = Role.findAll();
        render(user, schedule, members, roles);
    }

    public static void create() {
        // Parse member info.
        Map<String, String[]> values = params.all();
        Long accountId = new Long(values.get("account_id")[0]);
        Long scheduleId = new Long(values.get("schedule_id")[0]);
        String name = values.get("name")[0];
        String email = values.get("email")[0];
        Long roleId = new Long(values.get("role_id")[0]);
        System.out.println("Members.create(): accountId = " + accountId + ", scheduleId = " + scheduleId);
        System.out.println("Members.create(): name = " + name + ", email = " + email + ", role =  " + roleId);

        // Make sure we have not exceeded the max number of members in the plan.
        Account account = Account.findById(accountId);
        int maxMembers = account.plan.members;
        int memberCount = Member.getCountByAccountId(accountId);
        if (memberCount >= maxMembers) {
            renderJSON(new StatusMessage(StatusMessage.WARNING, "", ""));
        }

        // Validate name and email.
        validation.required("Name", name);
        validation.required("Email", email);
        validation.email("Email", email);
        if (validation.hasErrors()) {
            renderJSON(new StatusMessage(StatusMessage.ERROR,
                                         "Invalid name and/or email!", ""));
        }

        // Get schedule info.
        Schedule schedule = Schedule.findById(scheduleId);

        // Is new member already signed up?
        User user = User.findByEmail(email);
        if (user == null) {
            // Register new member.
            Role role = Role.findById(roleId);
            user = new User(email, "change_me", name, role);
            try {
                System.out.println("Notify new member to check email");
                Notifier.welcome(user);
            }
            catch (Exception e) {
                Logger.error(e, "Mail error");
            }
        }
        else {
            // Grant access and notify user.
            try {
                System.out.println("Notify new member to check email");
                Notifier.access(user, schedule.name);
            }
            catch (Exception e) {
                Logger.error(e, "Mail error");
            }
        }

        // Add new member to this account/schedule.
        try {
            new Member(user, account, schedule);
        }
        catch (Exception ex) {
            Logger.error(ex, "Add member error");
            renderJSON(new StatusMessage(StatusMessage.ERROR,
                                         "Error adding new member:  " + user.email + " already existed!", ""));
        }

        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void update(Long id) {
        System.out.println("Members.update() - id = " + id);

        Map<String, String[]> values = params.all();
        String name = values.get("name")[0];
        String email = values.get("email")[0];
        String phone = values.get("phone")[0];
        String password = values.get("password")[0];
        String password2 = values.get("password2")[0];
        String address = values.get("address")[0];
        String city = values.get("city")[0];
        String state = values.get("state")[0];
        String zip = values.get("zip")[0];
        String country = values.get("country")[0];
        String hireDate = values.get("hireDate")[0];
        String status = values.get("employeeStatus")[0];
        String jobTitle = values.get("jobTitle")[0];
        String basePay = values.get("basePay")[0];
        String[] location = values.get("location");

        validation.required("Name", name);
        validation.required("Email", email);
        validation.email("Email", email);

        boolean pwdChanged = false;
        if (password != null && password.trim().length() > 0) {
            pwdChanged = true;
            validation.minSize("Password", password, 6);
            validation.equals("Password", password, "confirmation password", password2);
        }

        if (validation.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            ArrayList<play.data.validation.Error> errors = (ArrayList)validation.errors();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0)
                    sb.append("~");
                sb.append(errors.get(i).message());
            }
            renderJSON(new StatusMessage(StatusMessage.ERROR, sb.toString(), ""));
        }

        // Find existing member.
        Member member = Member.findById(id);

        // Update user info.
        User user = member.user;
        user.name = name;
        user.email = email;
        if (pwdChanged)
            user.passwordHash = Codec.hexMD5(password);
        user.save();

        // Update member info.
        member.address = address;
        member.city = city;
        member.state = state;
        member.zip = zip;
        member.country = country;
        member.phone = phone;
        if (hireDate != null && hireDate.trim().length() > 0) {
            try {                
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                member.hireDate = sdf.parse(hireDate);
            }
            catch (ParseException e) {
                System.out.println("Error parsing hire date: " + e.getMessage());
            }
        }
        if (jobTitle != null && jobTitle.trim().length() > 0) {
            member.jobTitleId = Integer.parseInt(jobTitle);
        }
        if (status != null && status.trim().length() > 0) {
            member.employeeStatusId = Integer.parseInt(status);
        }
        if (basePay != null && basePay.trim().length() > 0) {
            member.basePay = Double.parseDouble(basePay);
        }
        member.save();

        // Remove existing locations.
        List<MemberLocation> memberLocations = member.locations;
        if (memberLocations != null && memberLocations.size() > 0) {
            for (int i = 0; i < memberLocations.size(); i++) {
                MemberLocation ml = memberLocations.get(i);
                ml.delete();
            }
        }

        // Insert new locations.
        for (int i = 0; i < location.length; i++) {
            int locationId = Integer.parseInt(location[i]);
            //System.out.println("location id: " + locationId);            
            new MemberLocation(member.id.longValue(), locationId);
        }

        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void delete(int memberId) {
        Member member = Member.findById(new Long(memberId));
        
        // Remove existing locations.
        List<MemberLocation> memberLocations = member.locations;
        if (memberLocations != null && memberLocations.size() > 0) {
            for (int i = 0; i < memberLocations.size(); i++) {
                MemberLocation ml = memberLocations.get(i);
                ml.delete();
            }
        }

        // Remove member.
        member.delete();
    }
}