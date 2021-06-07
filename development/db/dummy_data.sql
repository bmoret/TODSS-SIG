insert into person(id, branch, email, employed_since, expertise, first_name, last_name, role, supervisor_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2639', 1, 'manager@mail.com', date('2021-02-07'),'Expertise', 'first', 'last', 0, null),
       ('b4ba9d87-41d5-4c56-9c20-5addcf875f86', 1, 'manager2@mail.com', date('2021-02-07'),'Expertise', 'Adam', 'Adams', 0, null),
       ('b4ba9d87-41d5-4c56-9c20-5addcf875f88', 1, 'mail@mail.com', date('2021-02-07'),'Expertise', 'Manager', 'Lastname', 0, null);

insert into users(id, password, role, username, person_id)
values ('b4ba9d87-41d5-4c56-9c20-5addcf875fcc', '$2a$10$YLpVJJXejQGnBbZdg1/lGut61ygMFzi1BMEH0FbkAfpd3tBwnjOBK',
        'ROLE_MANAGER', 'admin', 'b4ba9d87-41d5-4c56-9c20-5addcf875f88');

insert into sig(id, subject, manager_id)
values ('e4fb562d-e468-431b-ab0f-ba0ad16a2631', 'Sig imusBimus', 'd4fb562d-e468-431b-ab0f-ba0ad16a2639'),
       ('d4fb562d-e468-431b-ab0f-ba0ad16a2639', 'Sig bimus des Imus', 'b4ba9d87-41d5-4c56-9c20-5addcf875f86');

insert into sig_organizers(organised_special_interest_groups_id, organizers_id)
VALUES ('e4fb562d-e468-431b-ab0f-ba0ad16a2631', 'b4ba9d87-41d5-4c56-9c20-5addcf875f86');

insert into session(id, description, end_date, start_date, subject, state, sig_id, contact_person_id)
values ('effb562d-e468-431b-ab0f-ba0ad16a2639', 'Dit is een sessie omschrijving. bla bla bla', date('2021-02-07'), date('2021-02-06'),
        'subject', 'PLANNED', 'e4fb562d-e468-431b-ab0f-ba0ad16a2631', 'd4fb562d-e468-431b-ab0f-ba0ad16a2639');

insert into physical_session(location, session_id)
values ('CIM hoofdkantoor', 'effb562d-e468-431b-ab0f-ba0ad16a2639');

insert into attendance(id, is_speaker, state, person_id, session_id)
values ('effb562d-e468-bbbb-ab0f-ba0ad16a2639', false, 2, 'b4ba9d87-41d5-4c56-9c20-5addcf875f88', 'effb562d-e468-431b-ab0f-ba0ad16a2639');

insert into session_attendance_list (session_id, attendance_list_id)
values ('effb562d-e468-431b-ab0f-ba0ad16a2639', 'effb562d-e468-bbbb-ab0f-ba0ad16a2639');