create schema if not exists parties;

create table parties.party (
    id            uuid primary key,
    version       bigint not null,
    type          text not null,
    given_name    text,
    family_name   text,
    date_of_birth date,
    legal_name    text
);

create table parties.party_role (
    party_id uuid not null references parties.party (id) on delete cascade,
    name     text not null
);

create table parties.party_identifier (
    party_id uuid not null references parties.party (id) on delete cascade,
    kind     text not null,
    value    text not null
);
