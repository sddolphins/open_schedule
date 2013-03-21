package controllers;

import java.util.Iterator;
import java.util.List;

import models.Account;
import models.Member;
import models.Schedule;
import models.User;

@Secure
public class Schedules extends BaseController {

    public static void create(int accountId, String name) {
        Account account = Account.findById(new Long(accountId));
        Schedule schedule = new Schedule(name, account);

        // Add default member.
        User user = connectedUser();
        new Member(user, account, schedule);        
    }

    public static void delete(int scheduleId) {
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        List<Member> members = schedule.members;
        Iterator<Member> it = members.iterator();
        while (it.hasNext()) {
            Member member = it.next();
            member.delete();
        }
        schedule.delete();
    }
}