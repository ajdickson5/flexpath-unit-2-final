drop table if exists order_items, orders, products, roles, users;

create table users (
    username varchar(255) primary key,
    password varchar(255)
);

create table roles (
    username varchar(255) not null,
    role varchar(250) not null,
    primary key (username, role),
    foreign key (username) references users(username) on delete cascade
);

create table products (
    id int primary key auto_increment,
    name varchar(255),
    price decimal(10, 2)
);

create table orders (
    id int primary key auto_increment,
    username varchar(255),
    foreign key (username) references users(username) on delete cascade
);

create table order_items (
    id int primary key auto_increment,
    order_id int,
    product_id int,
    quantity int,
    foreign key (order_id) references orders(id) on delete cascade,
    foreign key (product_id) references products(id) on delete cascade
);

insert into users (username, password) values ('test-admin', 'admin');
insert into roles (username, role) values ('test-admin', 'ADMIN');

insert into products (name, price) values ('Apple', 0.99);
insert into products (name, price) values ('Banana', 0.59);
insert into products (name, price) values ('Cherry', 1.99);
insert into products (name, price) values ('Date', 2.99);
insert into products (name, price) values ('Elderberry', 3.99);

insert into orders (username) values ('test-admin');
insert into orders (username) values ('test-admin');
insert into orders (username) values ('test-admin');
insert into orders (username) values ('test-admin');
insert into orders (username) values ('test-admin');

insert into order_items (order_id, product_id, quantity) values (1, 1, 1);
insert into order_items (order_id, product_id, quantity) values (2, 2, 2);
insert into order_items (order_id, product_id, quantity) values (3, 3, 3);
insert into order_items (order_id, product_id, quantity) values (4, 4, 4);
insert into order_items (order_id, product_id, quantity) values (5, 5, 5);