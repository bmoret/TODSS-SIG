insert into person(id, branch, email, employed_since, expertise, first_name, last_name, role, supervisor_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2639', 'VIANEN', 'manager@mail.com', date('2021-02-07'), 'Expertise',
        'first', 'last', 'MANAGER', null);

insert into sig(id, subject, manager_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2631', 'Sig imusBimus', 'd4fb562d-e468-431b-ab0f-ba0ad16a2639');
