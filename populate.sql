/* Role */
insert into role (description, dc) values ('Owner', null);
insert into role (description, dc) values ('Admin', null);
insert into role (description, dc) values ('Member', null);

/* Plan */
insert into plan (description, schedules, members, cost, dc)
values ('FREE', 1, 2, 0, null);
insert into plan (description, schedules, members, cost, dc)
values ('STARTUP S', 3, 5, 10, null);
insert into plan (description, schedules, members, cost, dc)
values ('STARTUP L', 5, 10, 20, null);

/* Billing Type */
insert into billing_type (description, dc) values ('Monthly', null);
insert into billing_type (description, dc) values ('Annual', null);

/* Credit Card Type */
insert into credit_card_type (description, dc) values ('FREE', null);
insert into credit_card_type (description, dc) values ('VISA', null);
insert into credit_card_type (description, dc) values ('MASTERCARD', null);
insert into credit_card_type (description, dc) values ('AMERICAN EXPRESS', null);
insert into credit_card_type (description, dc) values ('DISCOVER', null);

/* (Free) Billing */
insert into billing (billing_type_id, credit_card_type_id, holder_name, account_number,
                     exp_month, exp_year, country, state, zip, dc)
values (1, 1, 'free', 'free', 12, 2099, 'U.S.A', 'CA', '92130', null);                     

/* Employee Status */
insert into employee_status (status, dc) values ('Full Time', null);
insert into employee_status (status, dc) values ('Part Time', null);
