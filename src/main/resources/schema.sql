create table if not exists users
(
    id    int generated always as identity primary key,
    email varchar(300) UNIQUE,
    name  varchar(300)
);
create table if not exists items
(
    id int generated always as identity primary key,
    name varchar(300),
    description varchar(300),
    is_available boolean,
    owner_id int,
    request_id int,
    constraint fk_items_to_users foreign key (owner_id) references users (id) on delete cascade
    );
CREATE TABLE if not exists booking(
    id int generated always as identity primary key,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    booker_id int,
    item_id int,
    status varchar(20),
    constraint fk_booking_to_users foreign key (booker_id) references users(id),
    constraint fk_booking_to_items foreign key (item_id) references items (id)
    );
CREATE TABLE if not exists comments(
    id int generated always as identity primary key,
    text varchar(320),
    item_id   bigint,
    author_id bigint,
    created TIMESTAMP,
    constraint fk_comments_to_items foreign key (item_id) references items(id),
    constraint fk_comments_to_users foreign key (author_id) references users(id)
    );