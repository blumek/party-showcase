create table address_book (
    owner_id uuid primary key,
    version  bigint not null
);

create table address (
    id          uuid primary key,
    owner_id    uuid not null references address_book (owner_id) on delete cascade,
    kind        text not null,
    line1       text,
    line2       text,
    city        text,
    postal_code text,
    country     text,
    email       text,
    phone       text,
    website_url text,
    valid_from  date,
    valid_to    date
);

create table address_purpose (
    address_id uuid not null references address (id) on delete cascade,
    purpose    text not null
);
