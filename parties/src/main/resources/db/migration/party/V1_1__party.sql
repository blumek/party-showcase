create table party (
    id            uuid primary key,
    version       bigint not null,
    type          text not null,
    given_name    text,
    family_name   text,
    date_of_birth date,
    legal_name    text
);

create table party_role (
    party_id uuid not null references party (id) on delete cascade,
    name     text not null
);

create table party_identifier (
    party_id uuid not null references party (id) on delete cascade,
    kind     text not null,
    value    text not null
);
