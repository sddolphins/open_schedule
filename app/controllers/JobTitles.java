package controllers;

import java.util.List;

import models.Account;
import models.JobTitle;

@Secure
public class JobTitles extends BaseController {

    public static void index(int accountId) {
        Account account = Account.findById(new Long(accountId));
        List<JobTitle> jobTitles = JobTitle.findByAccountId(accountId);
        render(account, jobTitles);
    }

    public static void create(int accountId, String name) {
        Account account = Account.findById(new Long(accountId));
        new JobTitle(name, account);
    }

    public static void delete(int jtId) {
        JobTitle jt = JobTitle.findById(new Long(jtId));
        jt.delete();
    }
}