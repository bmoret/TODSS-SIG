create table attendance (id uuid not null, is_absent boolean not null, is_confirmed boolean not null, is_speaker boolean not null, person_id uuid not null, session_id uuid not null, primary key (id));
create table feedback (id uuid not null, description varchar(255), person_id uuid, session_id uuid, primary key (id));
create table online_session (join_url varchar(255), platform varchar(255), session_id uuid not null, primary key (session_id));
create table person (id uuid not null, branch int4, email varchar(255), employed_since date, expertise varchar(255), first_name varchar(255), last_name varchar(255), role int4, supervisor_id uuid, primary key (id));
create table physical_session (location varchar(255), session_id uuid not null, primary key (session_id));
create table session (id uuid not null, description varchar(255), end_date timestamp, start_date timestamp, subject varchar(255), state varchar(255), sig_id uuid, primary key (id));
create table session_attendance_list (session_id uuid not null, attendance_list_id uuid not null);
create table session_feedback_list (session_id uuid not null, feedback_list_id uuid not null);
create table sig (id uuid not null, subject varchar(255), manager_id uuid, primary key (id));
create table sig_organizers (organised_special_interest_groups_id uuid not null, organizers_id uuid not null);
create table teams_online_session (online_session_id uuid not null, primary key (online_session_id));
alter table session_attendance_list add constraint UK_b9c4s26st356o6ow4tm28ce8y unique (attendance_list_id);
alter table session_feedback_list add constraint UK_sf56n9tp81jfi7w5clyrbk580 unique (feedback_list_id);
alter table attendance add constraint FKd5wmpdsbs8oyb3b2amd78lfev foreign key (person_id) references person;
alter table attendance add constraint FKras5lsk62ds9j7f8myss8nwcs foreign key (session_id) references session;
alter table feedback add constraint FK9fngeswvwow8hbw483eflv3l5 foreign key (person_id) references person;
alter table feedback add constraint FKj8gwbu1cswl3lyu4cm409gd1i foreign key (session_id) references session;
alter table online_session add constraint FKq094t6hrk0jdetvqkf8e2acor foreign key (session_id) references session;
alter table person add constraint FK3e0iqc4bmiwmp8ltecavkc7d8 foreign key (supervisor_id) references person;
alter table physical_session add constraint FKrbtbf51i6fc10be7yug8vbda1 foreign key (session_id) references session;
alter table session add constraint FKtj3nhgdoqfe0n1mmjuffeuy4i foreign key (sig_id) references sig;
alter table session_attendance_list add constraint FKbsfcbqds1b7te7bcayum06j3u foreign key (attendance_list_id) references attendance;
alter table session_attendance_list add constraint FK1pyw86lao3q63oc55vjro56e6 foreign key (session_id) references session;
alter table session_feedback_list add constraint FK5ei9d7ecew8i2eo8ls8q8eejn foreign key (feedback_list_id) references feedback;
alter table session_feedback_list add constraint FKru0qye80109el8qmknrks7nol foreign key (session_id) references session;
alter table sig add constraint FKrj0kkqd2cj3krq8wirxi369c2 foreign key (manager_id) references person;
alter table sig_organizers add constraint FKrdc7im2gqb6ss31jkmwjhg3yy foreign key (organizers_id) references person;
alter table sig_organizers add constraint FKkfc7apm0kcfcjwlqtgo53xnh foreign key (organised_special_interest_groups_id) references sig;
alter table teams_online_session add constraint FKt4rsiwfoh8pb8tigh0drtplso foreign key (online_session_id) references online_session;

    create table attendance (
       id uuid not null,
        is_absent boolean not null,
        is_confirmed boolean not null,
        is_speaker boolean not null,
        person_id uuid not null,
        session_id uuid not null,
        primary key (id)
    )

    create table feedback (
       id uuid not null,
        description varchar(255),
        person_id uuid,
        session_id uuid,
        primary key (id)
    )

    create table online_session (
       join_url varchar(255),
        platform varchar(255),
        session_id uuid not null,
        primary key (session_id)
    )

    create table person (
       id uuid not null,
        branch int4,
        email varchar(255),
        employed_since date,
        expertise varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        role int4,
        supervisor_id uuid,
        primary key (id)
    )

    create table physical_session (
       location varchar(255),
        session_id uuid not null,
        primary key (session_id)
    )

    create table session (
       id uuid not null,
        description varchar(255),
        end_date timestamp,
        start_date timestamp,
        subject varchar(255),
        state varchar(255),
        sig_id uuid,
        primary key (id)
    )

    create table session_attendance_list (
       session_id uuid not null,
        attendance_list_id uuid not null
    )

    create table session_feedback_list (
       session_id uuid not null,
        feedback_list_id uuid not null
    )

    create table sig (
       id uuid not null,
        subject varchar(255),
        manager_id uuid,
        primary key (id)
    )

    create table sig_organizers (
       organised_special_interest_groups_id uuid not null,
        organizers_id uuid not null
    )

    create table teams_online_session (
       online_session_id uuid not null,
        primary key (online_session_id)
    )

    alter table session_attendance_list 
       add constraint UK_b9c4s26st356o6ow4tm28ce8y unique (attendance_list_id)

    alter table session_feedback_list 
       add constraint UK_sf56n9tp81jfi7w5clyrbk580 unique (feedback_list_id)

    alter table attendance 
       add constraint FKd5wmpdsbs8oyb3b2amd78lfev 
       foreign key (person_id) 
       references person

    alter table attendance 
       add constraint FKras5lsk62ds9j7f8myss8nwcs 
       foreign key (session_id) 
       references session

    alter table feedback 
       add constraint FK9fngeswvwow8hbw483eflv3l5 
       foreign key (person_id) 
       references person

    alter table feedback 
       add constraint FKj8gwbu1cswl3lyu4cm409gd1i 
       foreign key (session_id) 
       references session

    alter table online_session 
       add constraint FKq094t6hrk0jdetvqkf8e2acor 
       foreign key (session_id) 
       references session

    alter table person 
       add constraint FK3e0iqc4bmiwmp8ltecavkc7d8 
       foreign key (supervisor_id) 
       references person

    alter table physical_session 
       add constraint FKrbtbf51i6fc10be7yug8vbda1 
       foreign key (session_id) 
       references session

    alter table session 
       add constraint FKtj3nhgdoqfe0n1mmjuffeuy4i 
       foreign key (sig_id) 
       references sig

    alter table session_attendance_list 
       add constraint FKbsfcbqds1b7te7bcayum06j3u 
       foreign key (attendance_list_id) 
       references attendance

    alter table session_attendance_list 
       add constraint FK1pyw86lao3q63oc55vjro56e6 
       foreign key (session_id) 
       references session

    alter table session_feedback_list 
       add constraint FK5ei9d7ecew8i2eo8ls8q8eejn 
       foreign key (feedback_list_id) 
       references feedback

    alter table session_feedback_list 
       add constraint FKru0qye80109el8qmknrks7nol 
       foreign key (session_id) 
       references session

    alter table sig 
       add constraint FKrj0kkqd2cj3krq8wirxi369c2 
       foreign key (manager_id) 
       references person

    alter table sig_organizers 
       add constraint FKrdc7im2gqb6ss31jkmwjhg3yy 
       foreign key (organizers_id) 
       references person

    alter table sig_organizers 
       add constraint FKkfc7apm0kcfcjwlqtgo53xnh 
       foreign key (organised_special_interest_groups_id) 
       references sig

    alter table teams_online_session 
       add constraint FKt4rsiwfoh8pb8tigh0drtplso 
       foreign key (online_session_id) 
       references online_session
create table attendance (id uuid not null, is_absent boolean not null, is_confirmed boolean not null, is_speaker boolean not null, person_id uuid not null, session_id uuid not null, primary key (id))
create table feedback (id uuid not null, description varchar(255), person_id uuid, session_id uuid, primary key (id))
create table online_session (join_url varchar(255), platform varchar(255), session_id uuid not null, primary key (session_id))
create table person (id uuid not null, branch int4, email varchar(255), employed_since date, expertise varchar(255), first_name varchar(255), last_name varchar(255), role int4, supervisor_id uuid, primary key (id))
create table physical_session (location varchar(255), session_id uuid not null, primary key (session_id))
create table session (id uuid not null, description varchar(255), end_date timestamp, start_date timestamp, subject varchar(255), state varchar(255), sig_id uuid, primary key (id))
create table session_attendance_list (session_id uuid not null, attendance_list_id uuid not null)
create table session_feedback_list (session_id uuid not null, feedback_list_id uuid not null)
create table sig (id uuid not null, subject varchar(255), manager_id uuid, primary key (id))
create table sig_organizers (organised_special_interest_groups_id uuid not null, organizers_id uuid not null)
create table teams_online_session (online_session_id uuid not null, primary key (online_session_id))
alter table session_attendance_list add constraint UK_b9c4s26st356o6ow4tm28ce8y unique (attendance_list_id)
alter table session_feedback_list add constraint UK_sf56n9tp81jfi7w5clyrbk580 unique (feedback_list_id)
alter table attendance add constraint FKd5wmpdsbs8oyb3b2amd78lfev foreign key (person_id) references person
alter table attendance add constraint FKras5lsk62ds9j7f8myss8nwcs foreign key (session_id) references session
alter table feedback add constraint FK9fngeswvwow8hbw483eflv3l5 foreign key (person_id) references person
alter table feedback add constraint FKj8gwbu1cswl3lyu4cm409gd1i foreign key (session_id) references session
alter table online_session add constraint FKq094t6hrk0jdetvqkf8e2acor foreign key (session_id) references session
alter table person add constraint FK3e0iqc4bmiwmp8ltecavkc7d8 foreign key (supervisor_id) references person
alter table physical_session add constraint FKrbtbf51i6fc10be7yug8vbda1 foreign key (session_id) references session
alter table session add constraint FKtj3nhgdoqfe0n1mmjuffeuy4i foreign key (sig_id) references sig
alter table session_attendance_list add constraint FKbsfcbqds1b7te7bcayum06j3u foreign key (attendance_list_id) references attendance
alter table session_attendance_list add constraint FK1pyw86lao3q63oc55vjro56e6 foreign key (session_id) references session
alter table session_feedback_list add constraint FK5ei9d7ecew8i2eo8ls8q8eejn foreign key (feedback_list_id) references feedback
alter table session_feedback_list add constraint FKru0qye80109el8qmknrks7nol foreign key (session_id) references session
alter table sig add constraint FKrj0kkqd2cj3krq8wirxi369c2 foreign key (manager_id) references person
alter table sig_organizers add constraint FKrdc7im2gqb6ss31jkmwjhg3yy foreign key (organizers_id) references person
alter table sig_organizers add constraint FKkfc7apm0kcfcjwlqtgo53xnh foreign key (organised_special_interest_groups_id) references sig
alter table teams_online_session add constraint FKt4rsiwfoh8pb8tigh0drtplso foreign key (online_session_id) references online_session

    create table attendance (
       id uuid not null,
        is_absent boolean not null,
        is_confirmed boolean not null,
        is_speaker boolean not null,
        person_id uuid not null,
        session_id uuid not null,
        primary key (id)
    )

    create table feedback (
       id uuid not null,
        description varchar(255),
        person_id uuid,
        session_id uuid,
        primary key (id)
    )

    create table online_session (
       join_url varchar(255),
        platform varchar(255),
        session_id uuid not null,
        primary key (session_id)
    )

    create table person (
       id uuid not null,
        branch int4,
        email varchar(255),
        employed_since date,
        expertise varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        role int4,
        supervisor_id uuid,
        primary key (id)
    )

    create table physical_session (
       location varchar(255),
        session_id uuid not null,
        primary key (session_id)
    )

    create table session (
       id uuid not null,
        description varchar(255),
        end_date timestamp,
        start_date timestamp,
        subject varchar(255),
        state varchar(255),
        sig_id uuid,
        primary key (id)
    )

    create table session_attendance_list (
       session_id uuid not null,
        attendance_list_id uuid not null
    )

    create table session_feedback_list (
       session_id uuid not null,
        feedback_list_id uuid not null
    )

    create table sig (
       id uuid not null,
        subject varchar(255),
        manager_id uuid,
        primary key (id)
    )

    create table sig_organizers (
       organised_special_interest_groups_id uuid not null,
        organizers_id uuid not null
    )

    create table teams_online_session (
       online_session_id uuid not null,
        primary key (online_session_id)
    )

    alter table session_attendance_list 
       add constraint UK_b9c4s26st356o6ow4tm28ce8y unique (attendance_list_id)

    alter table session_feedback_list 
       add constraint UK_sf56n9tp81jfi7w5clyrbk580 unique (feedback_list_id)

    alter table attendance 
       add constraint FKd5wmpdsbs8oyb3b2amd78lfev 
       foreign key (person_id) 
       references person

    alter table attendance 
       add constraint FKras5lsk62ds9j7f8myss8nwcs 
       foreign key (session_id) 
       references session

    alter table feedback 
       add constraint FK9fngeswvwow8hbw483eflv3l5 
       foreign key (person_id) 
       references person

    alter table feedback 
       add constraint FKj8gwbu1cswl3lyu4cm409gd1i 
       foreign key (session_id) 
       references session

    alter table online_session 
       add constraint FKq094t6hrk0jdetvqkf8e2acor 
       foreign key (session_id) 
       references session

    alter table person 
       add constraint FK3e0iqc4bmiwmp8ltecavkc7d8 
       foreign key (supervisor_id) 
       references person

    alter table physical_session 
       add constraint FKrbtbf51i6fc10be7yug8vbda1 
       foreign key (session_id) 
       references session

    alter table session 
       add constraint FKtj3nhgdoqfe0n1mmjuffeuy4i 
       foreign key (sig_id) 
       references sig

    alter table session_attendance_list 
       add constraint FKbsfcbqds1b7te7bcayum06j3u 
       foreign key (attendance_list_id) 
       references attendance

    alter table session_attendance_list 
       add constraint FK1pyw86lao3q63oc55vjro56e6 
       foreign key (session_id) 
       references session

    alter table session_feedback_list 
       add constraint FK5ei9d7ecew8i2eo8ls8q8eejn 
       foreign key (feedback_list_id) 
       references feedback

    alter table session_feedback_list 
       add constraint FKru0qye80109el8qmknrks7nol 
       foreign key (session_id) 
       references session

    alter table sig 
       add constraint FKrj0kkqd2cj3krq8wirxi369c2 
       foreign key (manager_id) 
       references person

    alter table sig_organizers 
       add constraint FKrdc7im2gqb6ss31jkmwjhg3yy 
       foreign key (organizers_id) 
       references person

    alter table sig_organizers 
       add constraint FKkfc7apm0kcfcjwlqtgo53xnh 
       foreign key (organised_special_interest_groups_id) 
       references sig

    alter table teams_online_session 
       add constraint FKt4rsiwfoh8pb8tigh0drtplso 
       foreign key (online_session_id) 
       references online_session
