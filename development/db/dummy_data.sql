insert into person(id, branch, email, employed_since, expertise, first_name, last_name, role, supervisor_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2639', 1, 'manager@mail.com', date('2021-02-07'),
        'Expertise', 'first', 'last', 0, null);

insert into person(id, branch, email, employed_since, expertise, first_name, last_name, role, supervisor_id)
values ('b4ba9d87-41d5-4c56-9c20-5addcf875f86', 1, 'manager2@mail.com', date('2021-02-07'),
        'Expertise', 'Adam', 'Adams', 0, null);

insert into sig(id, subject, manager_id)
values ('e4fb562d-e468-431b-ab0f-ba0ad16a2631', 'Sig imusBimus', 'd4fb562d-e468-431b-ab0f-ba0ad16a2639');

insert into sig(id, subject, manager_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2639', 'Sig bimusImus', 'b4ba9d87-41d5-4c56-9c20-5addcf875f86');

insert into sig_organizers(organised_special_interest_groups_id, organizers_id)
VALUES ('e4fb562d-e468-431b-ab0f-ba0ad16a2631', 'b4ba9d87-41d5-4c56-9c20-5addcf875f86')