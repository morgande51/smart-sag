-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-1');
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-2');
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-3');

-- define some users
insert into sag_user (id, first_name, last_name, email, phone) values (1, 'test', 'user', 'test@test.com', '5555555555');
insert into sag_user (id, first_name, last_name, email, phone) values (2, 'Admin', 'User', 'admin@test.com', '9999999999');
insert into sag_user (id, first_name, last_name, email, phone) values (3, 'SAG', 'Support', 'sag@test.com', '2222222222');
insert into sag_user (id, first_name, last_name, email, phone) values (4, 'Test', 'Rider', 'rider@test.com', '2222222222');

-- define an org for user
insert into org (id, name, contact_user, popup) values (0, 'TEST:USER', '1', TRUE);
insert into org (id, name, contact_user) values (1, 'testOrg', '1');
insert into org (id, name, contact_user) values (2, 'Removable Org', '1');

-- define admins for the org
insert into org_admin (user_org_id, sag_user_id) values (0, 1);
insert into org_admin (user_org_id, sag_user_id) values (1, 1);
insert into org_admin (user_org_id, sag_user_id) values (1, 2);
insert into org_admin (user_org_id, sag_user_id) values (2, 1);
insert into org_admin (user_org_id, sag_user_id) values (2, 2);

-- define ride for the org
insert into ride (id, name, start_at, end_at, street_name, city, state, zip, latitude, longitude, hosting_org) values (1, 'test Ride', TO_TIMESTAMP('2022-01-01 01:00:00+01:00', 'YYYY-MM-DD HH:MI:SSTZH:TZM'), TO_TIMESTAMP('2023-01-01 01:00:00+01:00', 'YYYY-MM-DD HH:MI:SSTZH:TZM'), '13710 Central Avenue', 'Upper Marlboro', 'MD', 20721, 38.907231, -76.774564, 1);

-- define SAG for rides
insert into ride_sag (ride_id, sag_user_id) values (1, 3);

-- define SAGRequest for rides
insert into sag_request (id, ref_id, requested, status, user_id, ride_id, latitude, longitude) values (1, 'SAMPLE_REF_ID', TO_TIMESTAMP('2022-01-01 01:00:00+01:00', 'YYYY-MM-DD HH:MI:SSTZH:TZM'), 'N', 4, 1, 38.907231, -76.774564);