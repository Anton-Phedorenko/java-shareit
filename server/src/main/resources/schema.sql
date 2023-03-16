create table if not exists users
(
    id    bigint generated always as identity primary key,
    email varchar(300) UNIQUE,
    name  varchar(300)
);
create table if not exists request
(
    id        bigint generated always as identity primary key,
    description  varchar(300) NOT NULL,
    requestor_id bigint,
    created   TIMESTAMP NOT NULL,
    constraint fk_request_to_users foreign key (requestor_id) references users (id)
);
create table if not exists items
(
    id           bigint generated always as identity primary key,
    name         varchar(300) NOT NULL,
    description  varchar(300) NOT NULL,
    is_available boolean NOT NULL,
    owner_id     bigint,
    request_id   bigint,
    constraint fk_items_to_users foreign key (owner_id) references users (id),
    constraint fk_items_to_requestor foreign key (request_id) references request (id)
);
create table if not exists booking
(
    id         bigint generated always as identity primary key,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP NOT NULL,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(20),
    constraint fk_booking_to_items foreign key (item_id) references items (id),
    constraint fk_booking_to_users foreign key (booker_id) references users (id)
);
create table if not exists comments
(
    id        bigint generated always as identity primary key,
    text      varchar(320) NOT NULL,
    item_id   bigint,
    author_id bigint,
    created   TIMESTAMP,
    constraint fk_comments_to_items foreign key (item_id) references items (id),
    constraint fk_comments_to_users foreign key (author_id) references users (id)
);