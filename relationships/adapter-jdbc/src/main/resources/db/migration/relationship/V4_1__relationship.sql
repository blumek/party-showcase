create schema if not exists relationships;

create table relationships.relationship_ledger (
    owner_id uuid primary key,
    version  bigint not null
);

create table relationships.relationship (
    id         uuid primary key,
    owner_id   uuid not null references relationships.relationship_ledger (owner_id) on delete cascade,
    from_party uuid not null,
    from_role  text not null,
    to_party   uuid not null,
    to_role    text not null,
    type       text not null,
    valid_from date,
    valid_to   date
);

create index idx_relationship_from_party on relationships.relationship (from_party);
create index idx_relationship_to_party on relationships.relationship (to_party);
