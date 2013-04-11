create table role
(
    id smallint unsigned not null auto_increment primary key,
    description varchar(40) unique not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table user
(
    id bigint unsigned not null auto_increment primary key,
    email varchar(80) unique not null,
    name varchar(80) not null,
    needConfirmation varchar(80),
    passwordHash varchar(255) not null,
    role_id smallint unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index user_role_idx (role_id),
    foreign key (role_id) references role (id)
) engine = innodb;

create table plan
(
    id tinyint unsigned not null auto_increment primary key,
    description varchar(80) not null,
    schedules smallint not null,
    members smallint not null,
    cost smallint not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table billing_type
(
    id tinyint unsigned not null auto_increment primary key,
    description varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table credit_card_type
(
    id tinyint unsigned not null auto_increment primary key,
    description varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table billing
(
    id int unsigned not null auto_increment primary key,
    billing_type_id tinyint unsigned not null,
    credit_card_type_id tinyint unsigned not null,
    holder_name varchar(80) not null,
    account_number varchar(40) not null,
    exp_month tinyint not null,
    exp_year smallint not null,
    country varchar(40) not null,
    state varchar(40), /* U.S and Canada only */
    zip varchar(10),
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index billing_billing_type_idx (billing_type_id),
    foreign key (billing_type_id) references billing_type (id),
    index billing_credit_card_idx (credit_card_type_id),
    foreign key (credit_card_type_id) references credit_card_type (id)
) engine = innodb;

create table account
(
    id int unsigned not null auto_increment primary key,
    name varchar(80) not null,
    owner_id bigint unsigned not null,
    plan_id tinyint unsigned not null,
    billing_id int unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index account_owner_idx (owner_id),
    foreign key (owner_id) references user (id),
    index account_plan_idx (plan_id),
    foreign key (plan_id) references plan (id),
    index account_billing_idx (billing_id),
    foreign key (billing_id) references billing (id)
) engine = innodb;

create table schedule
(
    id int unsigned not null auto_increment primary key,
    name varchar(80) not null,
    account_id int unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index schedule_account_idx (account_id),
    foreign key (account_id) references account (id)
) engine = innodb;

create table organization
(
    id int unsigned not null auto_increment primary key,
    name varchar(80) not null,
    account_id int unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index organization_account_idx (account_id),
    foreign key (account_id) references account (id)
) engine = innodb;

create table facility
(
    id int unsigned not null auto_increment primary key,
    name varchar(80) not null,
    organization_id int unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index facility_organization_idx (organization_id),
    foreign key (organization_id) references organization (id)
) engine = innodb;

create table location
(
    id int unsigned not null auto_increment primary key,
    name varchar(80) not null,
    facility_id int unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index location_facility_idx (facility_id),
    foreign key (facility_id) references facility (id)
) engine = innodb;

create table job_title
(
    id int unsigned not null auto_increment primary key,
    name varchar(80) not null,
    account_id int unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index job_title_account_idx (account_id),
    foreign key (account_id) references account (id)
) engine = innodb;

create table employee_status
(
    id tinyint unsigned not null auto_increment primary key,
    status varchar(80) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table member
(
    id bigint unsigned not null auto_increment primary key,
    user_id bigint unsigned not null,
    account_id int unsigned not null,
    schedule_id int unsigned not null,
    job_title_id int unsigned null,
    employee_status_id tinyint unsigned null,
    address varchar(80) null,
    country varchar(40) null,
    state varchar(40) null,
    city varchar(40) null,
    zip varchar(10) null,
    phone varchar(15) null,
    hire_date datetime null,
    base_pay decimal(5,2) null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    unique key member_unique_key (user_id, account_id, schedule_id),
    index member_user_idx (user_id),
    foreign key (user_id) references user (id),
    index member_account_idx (account_id),
    foreign key (account_id) references account (id),
    index member_schedule_idx (schedule_id),
    foreign key (schedule_id) references schedule (id)
) engine = innodb;

create table member_location
(
    member_id bigint unsigned not null,
    location_id int unsigned not null,
    primary key (member_id, location_id),

    index member_location_member_idx (member_id),
    foreign key (member_id) references member (id),
    index member_location_location_idx (location_id),
    foreign key (location_id) references location (id)
) engine = innodb;

create table shift_type
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table shift_shift
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table shift_status
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    sort_order smallint not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table shift_request_status
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table work_type
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table work_subtype
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table overtime_type
(
    id tinyint unsigned not null auto_increment primary key,
    name varchar(40) not null,
    multiplier decimal(3,2) not null,
    dc timestamp default 0,
    lu timestamp default now() on update now()
) engine = innodb;

create table shift
(
    id bigint unsigned not null auto_increment primary key,
    schedule_id int unsigned not null,
    location_id int unsigned not null,
    shift_type_id tinyint unsigned not null,
    shift_shift_id tinyint unsigned not null,
    shift_status_id tinyint unsigned not null,
    work_type_id tinyint unsigned not null,
    work_subtype_id tinyint unsigned,
    date_start datetime not null,
    date_end datetime not null,
    contact varchar(80),
    comment varchar(255),
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index shift_schedule_idx (schedule_id),
    foreign key (schedule_id) references schedule (id),
    index shift_location_idx (location_id),
    foreign key (location_id) references location (id),
    index shift_shift_type_idx (shift_type_id),
    foreign key (shift_type_id) references shift_type (id),
    index shift_shift_shift_idx (shift_shift_id),
    foreign key (shift_shift_id) references shift_shift (id),
    index shift_shift_status_idx (shift_status_id),
    foreign key (shift_status_id) references shift_status (id),
    index shift_work_type_idx (work_type_id),
    foreign key (work_type_id) references work_type (id),
    foreign key (work_subtype_id) references work_subtype (id)
) engine = innodb;

create table shift_restriction
(
    id int unsigned not null auto_increment primary key,
    shift_id bigint unsigned not null,
    location_id int unsigned default null,
    job_title_id int unsigned default null,
    active bool not null default 1,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index shift_restriction_shift_idx (shift_id),
    foreign key (shift_id) references shift (id)
) engine = innodb;

create table open_shift
(
    id bigint unsigned not null auto_increment primary key,
    shift_id bigint unsigned not null,
    num_needed smallint unsigned not null default 1,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index self_schedule_shift_shift_idx (shift_id),
    foreign key (shift_id) references shift (id)
) engine = innodb;

create table scheduled_shift
(
    id bigint unsigned not null auto_increment primary key,
    shift_id bigint unsigned not null,
    member_id bigint unsigned not null,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index scheduled_shift_shift_idx (shift_id),
    foreign key (shift_id) references shift (id),
    index scheduled_shift_member_idx (member_id),
    foreign key (member_id) references member (id)
) engine = innodb;

create table self_schedule_shift
(
    id bigint unsigned not null auto_increment primary key,
    shift_id bigint unsigned not null,
    signup_date_start datetime,
    num_needed smallint unsigned not null default 1,
    num_awarded smallint unsigned not null default 0,
    num_filled smallint unsigned not null default 0,
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index self_schedule_shift_shift_idx (shift_id),
    foreign key (shift_id) references shift (id)
) engine = innodb;

create table self_schedule_request
(
    id bigint unsigned not null auto_increment primary key,
    self_schedule_shift_id bigint unsigned not null,
    member_id bigint unsigned not null,
    request_status_id tinyint unsigned not null,
    date_requested datetime not null,
    date_approved_denied datetime,
    comment varchar(255),
    manager_comment varchar(255),
    dc timestamp default 0,
    lu timestamp default now() on update now(),

    index self_schedule_request_sss_idx (self_schedule_shift_id),
    foreign key (self_schedule_shift_id) references self_schedule_shift (id),
    index self_schedule_request_member_idx (member_id),
    foreign key (member_id) references member (id),
    foreign key (request_status_id) references shift_request_status (id)
) engine = innodb;
