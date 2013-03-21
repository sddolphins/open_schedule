package controllers;

import java.util.List;
import java.util.Map;

import play.Logger;
import play.data.validation.*;

import helpers.StatusMessage;
import models.Account;
import models.Member;
import models.Role;
import models.Schedule;
import models.User;
import notifiers.Notifier;

@Secure
public class Members extends BaseController {

    public static void index(int accountId, int scheduleId) {
        System.out.println("Members.index(): accountId = " + accountId + ", scheduleId = " + scheduleId);
        User user = connectedUser();
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        List<Member> members = schedule.members;
        List<Role> roles = Role.findAll();
        render(user, accountId, schedule, members, roles);
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

    public static void delete(int memberId) {
        Member member = Member.findById(new Long(memberId));
        member.delete();
    }
}