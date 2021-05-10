insert into person(id, branch, email, employed_since, expertise, first_name, last_name, role, supervisor_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2639', 1, 'manager@mail.com', date('2021-02-07'),
        'Expertise', 'first', 'last', 0, null);

insert into sig(id, subject, manager_id)
values ('d4fb562d-e468-431b-ab0f-ba0ad16a2631', 'Sig imusBimus', 'd4fb562d-e468-431b-ab0f-ba0ad16a2639');


insert into session(id, description, end_date, start_date, subject, state, sig_id)
values ('e4fb562d-e468-431b-ab0f-ba0ad16a2631', 'Session Description', timestamp '2021-02-07 11:30:15', timestamp '2021-02-07 13:30:15', 'Session subject', 'TO_BE_PLANNED', 'd4fb562d-e468-431b-ab0f-ba0ad16a2631');

insert into physical_session(location, session_id)
values ('Location 12', 'e4fb562d-e468-431b-ab0f-ba0ad16a2631');