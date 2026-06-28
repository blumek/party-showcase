create schema if not exists capabilities;

create table capabilities.capability_portfolio (
    owner_id uuid primary key,
    version  bigint not null
);

create table capabilities.capability (
    id         uuid primary key,
    owner_id   uuid not null references capabilities.capability_portfolio (owner_id) on delete cascade,
    kind       text not null,
    valid_from date,
    valid_to   date
);

create table capabilities.capability_scope (
    id            uuid primary key,
    capability_id uuid not null references capabilities.capability (id) on delete cascade,
    dimension     text not null,
    grade_label   text,
    grade_rank    int,
    volume_cap    int,
    volume_period text,
    opens_at      time,
    closes_at     time
);

create table capabilities.capability_scope_value (
    scope_id uuid not null references capabilities.capability_scope (id) on delete cascade,
    value    text not null
);

create table capabilities.capability_scope_day (
    scope_id uuid not null references capabilities.capability_scope (id) on delete cascade,
    day      text not null
);
