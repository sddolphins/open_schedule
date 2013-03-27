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

/*
create table user_role
(
    user_id bigint unsigned not null,
    role_id smallint unsigned not null,

    index user_role_idx1 (user_id),
    foreign key (user_id) references user (id),
    index user_role_idx2 (role_id),
    foreign key (role_id) references role (id)
) engine = innodb;
*/

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
    location_id int unsigned null,
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
