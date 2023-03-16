CREATE TABLE IF NOT EXISTS users
(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(300) UNIQUE,
    name varchar(300)
);
CREATE TABLE IF NOT EXISTS request
(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(300),
    requestor_id int,
    created TIMESTAMP,
    CONSTRAINT fk_request_to_users FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(300),
    description varchar(300),
    is_available boolean,
    owner_id int,
    request_id int,
    CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_items_to_requestor FOREIGN KEY (request_id) REFERENCES request (id)
);

CREATE TABLE IF NOT EXISTS booking
(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    booker_id int,
    item_id int,
    status varchar(20),
    CONSTRAINT fk_booking_to_users FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_booking_to_items FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(320),
    item_id bigint,
    author_id bigint,
    created TIMESTAMP,
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id)
);