-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-1');
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-2');
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-3');

-- define a single user
insert into sag_user (id, first_name, last_name, email, phone) values (1, 'test', 'user', 'test@test.com', '5555555555');

-- define an org for user
insert into org (id, name, contact_user) values (1, 'testOrg', '1');

-- define a ride for the org
insert into ride (id, name, start_at, end_at, street_name, city, state, zip, latitude, longitude, hosting_org) values (1, 'test Ride', TO_TIMESTAMP('2022-01-01 01:00:00+01:00', 'YYYY-MM-DD HH:MI:SSTZH:TZM'), TO_TIMESTAMP('2023-01-01 01:00:00+01:00', 'YYYY-MM-DD HH:MI:SSTZH:TZM'), '13710 Central Avenue', 'Upper Marlboro', 'MD', 20721, 38.907231, -76.774564, 1);

-- define an admin for the org
insert into org_admin (user_org_id, sag_user_id) values (1, 1);