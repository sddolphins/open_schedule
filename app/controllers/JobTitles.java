package controllers;

import java.util.List;
import java.util.Map;

import helpers.StatusMessage;
import models.Account;
import models.JobTitle;

@Secure
public class JobTitles extends BaseController {

    public static void index(int accountId) {
        Account account = Account.findById(new Long(accountId));
        List<JobTitle> jobTitles = JobTitle.findByAccountId(accountId);
        render(account, jobTitles);
    }

    public static void getJobTitle(int jobTitleId) {
        System.out.println("JobTitles.getJobTitle() - id: " + jobTitleId);
        renderJSON(JobTitle.findById(new Long(jobTitleId)));
    }

    public static void update(int accountId) {
        Map<String, String[]> values = params.all();
        String id = values.get("jt_id")[0];
        String name = values.get("jt_name")[0];
        String color = values.get("jt_color")[0];
        String openColor = values.get("jt_open_color")[0];

        Account account = Account.findById(new Long(accountId));
        if (id != null && id.trim().length() > 0) {
            JobTitle jt = JobTitle.findById(Long.parseLong(id));
            jt.name = name;
            if (color != null && color.trim().length() > 0)
                jt.color = color;
            if (openColor != null && openColor.trim().length() > 0)
                jt.openShiftColor = openColor;
            jt.save();
        }
        else {
            new JobTitle(name, color, openColor, account);
        }
        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void delete(int jtId) {
        JobTitle jt = JobTitle.findById(new Long(jtId));
        jt.delete();
    }
}