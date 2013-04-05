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

/* Shift Type */
insert into shift_type (name, dc) values ('Open-Postable', null);
insert into shift_type (name, dc) values ('Scheduled', null);
insert into shift_type (name, dc) values ('Self-Scheduled', null);

/* Shift Shift */
insert into shift_shift (name, dc) values ('Day', null);
insert into shift_shift (name, dc) values ('Evening', null);
insert into shift_shift (name, dc) values ('Night', null);

/* Shift Status */
insert into shift_status (name, sort_order, dc) values ('Open', 1, null);
insert into shift_status (name, sort_order, dc) values ('Closed', 2, null);
insert into shift_status (name, sort_order, dc) values ('Pending', 3, null);
insert into shift_status (name, sort_order, dc) values ('Awarded', 4, null);
insert into shift_status (name, sort_order, dc) values ('Unknown', 5, null);
insert into shift_status (name, sort_order, dc) values ('Inactive', 6, null);

/* Shift Request Status */
insert into shift_request_status (name, dc) values ('Pending', null);
insert into shift_request_status (name, dc) values ('Approved', null);
insert into shift_request_status (name, dc) values ('Denied', null);
insert into shift_request_status (name, dc) values ('Retracted', null);

/* Work Type */
insert into work_type (name, dc) values ('Regular', null);
insert into work_type (name, dc) values ('On-Call', null);
insert into work_type (name, dc) values ('Non-Duty', null);
insert into work_type (name, dc) values ('Personal', null);
insert into work_type (name, dc) values ('PTO', null);
insert into work_type (name, dc) values ('Sick', null);
insert into work_type (name, dc) values ('Scheduled Regular', null);
insert into work_type (name, dc) values ('Scheduled On-Call', null);
insert into work_type (name, dc) values ('Scheduled Non-Duty', null);

/* Work Subtype */
insert into work_subtype (name, dc) values ('None', null);
insert into work_subtype (name, dc) values ('PTO', null);
insert into work_subtype (name, dc) values ('Sick', null);
insert into work_subtype (name, dc) values ('Personal', null);

/* Overtime Type */
insert into overtime_type (name, multiplier, dc) values ('Regular', 1.00, null);
insert into overtime_type (name, multiplier, dc) values ('Overtime', 1.50, null);
insert into overtime_type (name, multiplier, dc) values ('Double-Time', 2.00, null);

