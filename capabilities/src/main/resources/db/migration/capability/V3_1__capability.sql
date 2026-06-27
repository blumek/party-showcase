create table capability_portfolio (
    owner_id uuid primary key,
    version  bigint not null
);

create table capability (
    id         uuid primary key,
    owner_id   uuid not null references capability_portfolio (owner_id) on delete cascade,
    kind       text not null,
    valid_from date,
    valid_to   date
);

create table capability_scope (
    id            uuid primary key,
    capability_id uuid not null references capability (id) on delete cascade,
    dimension     text not null,
    grade_label   text,
    grade_rank    int,
    volume_cap    int,
    volume_period text,
    opens_at      time,
    closes_at     time
);

create table capability_scope_value (
    scope_id uuid not null references capability_scope (id) on delete cascade,
    value    text not null
);

create table capability_scope_day (
    scope_id uuid not null references capability_scope (id) on delete cascade,
    day      text not null
);
