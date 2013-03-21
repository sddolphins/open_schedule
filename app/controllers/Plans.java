package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.data.validation.*;

import helpers.StatusMessage;
import models.Account;
import models.Billing;
import models.BillingType;
import models.CreditCardType;
import models.Plan;
import models.Member;
import models.User;

@Secure
public class Plans extends BaseController {

    public static void index(int accountId) {
        User user = Application.connectedUser();
        Account account = Account.findById(new Long(accountId));
        int memberCount = Member.getCountByAccountId(new Long(accountId));
        List<Plan> plans = Plan.findAllReverseOrder();
        render(user, account, memberCount, plans);
    }

    public static void update(int planId) {
        System.out.println("Plans.update() - plan id = " + planId);

        // Parse account id.
        Map<String, String[]> values = params.all();
        String accountId = values.get("account_id")[0];

        // Get previous account/plan info.
        Account account = Account.findById(Long.valueOf(accountId));
        Plan plan = Plan.findById(new Long(planId));

        // Has the "lower-plan" been selected?
        if (account.plan.id.intValue() > planId) {
            // Make sure the current number of schedules and members are within
            // limit of the new plan.
            if (account.schedules.size() > plan.schedules) {
                renderJSON(new StatusMessage(StatusMessage.ERROR,
                                             "You have exceeded the maximum number of private schedules with the new plan.",
                                             "You can either remove inactive private schedules before changing to the new plan or continue with the same plan."));
            }

            if (account.members.size() > plan.members) {
                renderJSON(new StatusMessage(StatusMessage.ERROR,
                                             "You have exceeded the maximum number of schedule members with the new plan.",
                                             "You can either remove inactive schedule members before changing to the new plan or continue with the same plan."));
            }

            // Free-plan has no billing info.
            if (planId == 1) {
                Plan freePlan = Plan.findByDescription("FREE");
                Billing freeBilling = Billing.findById(new Long(1));
                account.plan = freePlan;
                account.billing = freeBilling;
                account.save();                
                renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
            }
        }

        // Parse the rest of form data.
        String billingType = values.get("billingType")[0];
        String holderName = values.get("holderName")[0];
        String accountNumber = values.get("accountNumber")[0];
        String expMonth = values.get("expMonth")[0];
        String expYear = values.get("expYear")[0];
        String country = values.get("country")[0];
        String state = values.get("state")[0];
        String zip = values.get("zip")[0];

        // Check required billing info.
        validation.required("Name On Card", holderName);
        validation.required("Account Number", accountNumber);
        validation.required("Expiration Month", expMonth);
        validation.required("Expiration Year", expYear);
        validation.required("Country", country);

        if (validation.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            ArrayList<play.data.validation.Error> errors = (ArrayList)validation.errors();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0)
                    sb.append("~");
                sb.append(errors.get(i).message());
            }
            renderJSON(new StatusMessage(StatusMessage.ERROR,
                                         "Invalid billing info. Please correct the following error(s):",
                                         sb.toString()));
        }

        // Add new billing info.
        BillingType bt = BillingType.findById(Long.valueOf(billingType));
        CreditCardType cct = CreditCardType.findByDescription("VISA");
        Billing billing = new Billing(bt, cct, holderName, accountNumber,
                                      Integer.parseInt(expMonth),
                                      Integer.parseInt(expYear),
                                      country, state, zip);

        // Update account.
        account.plan = plan;
        account.billing = billing;
        account.save();

        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }
}