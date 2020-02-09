
insert into authorities (id, type) values (1, 'ROLE_CLINIC_ADMIN');
insert into authorities (id, type) values (5, 'ROLE_PATIENT');
insert into authorities (id, type) values (8, 'ROLE_DOCTOR');
insert into authorities (id, type) values (9, 'ROLE_NURSE');

insert into clinic (id, address, city, deleted, description, name, rating_avg, rating_count) values (2, 'Blagoja Parovica',
   'Novi Sad', false, 'Fina klinika', 'Gradska bolnica 1', 0, 5);

insert into clinic (id, address, city, deleted, description, name, rating_avg, rating_count) values (13, 'Blagoja Parovica',
   'Novi Sad', false, 'Fina klinika', 'Gradska bolnica 2', 0, 5);

-- Examination type
insert into examination_type (id, deleted, name, price, clinic_id) values (10, false, 'Ocni pregled', 55, 2);
insert into examination_type (id, deleted, name, price, clinic_id) values (15, false, 'Tip2', 155, 2);
insert into examination_type (id, deleted, name, price, clinic_id) values (16, false, 'Tip3', 255, 2);

-- CLINIC ADMIN
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('CLINIC_ADMIN', 20, false, 'clinicAdmin1@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Pera', 1, '$2a$10$aRyKD2NZNwJL.kXsU37wO..kBhpyAZI.urCo4xOW3nXoauMaEiYcO', '12345670', 'Peric', 0, null, null, null, null, null,
    null, null, null, 2, null);

-- DOCTOR
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('DOCTOR', 6, false, 'doctor1@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Doctor', 1, '$2a$10$saH.GJtGvDpZOHqIMgKSmeT5V/Cpgng6Qz1p.mRq2GQAUdTuXqLcK', '12345670', 'Peric', 0, 0, 0, '1970-01-01 17:00:00', '1970-01-01 08:00:00', null,
    null, null, null, 2, 10);
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('DOCTOR', 13, false, 'doctor2@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Sima', 1, '$2a$10$saH.GJtGvDpZOHqIMgKSmeT5V/Cpgng6Qz1p.mRq2GQAUdTuXqLcK', '12345670', 'Simic', 0, 0, 0, '1970-01-01 17:00:00', '1970-01-01 08:00:00', null,
    null, null, null, 2, 10);
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('DOCTOR', 14, false, 'doctor3@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Mika', 1, '$2a$10$saH.GJtGvDpZOHqIMgKSmeT5V/Cpgng6Qz1p.mRq2GQAUdTuXqLcK', '12345670', 'Mikic', 0, 0, 0, '1970-01-01 17:00:00', '1970-01-01 08:00:00', null,
    null, null, null, 2, 15);
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('DOCTOR', 17, false, 'doctor4@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Sandra', 1, '$2a$10$saH.GJtGvDpZOHqIMgKSmeT5V/Cpgng6Qz1p.mRq2GQAUdTuXqLcK', '12345670', 'Sandric', 0, 0, 0, '1970-01-01 17:00:00', '1970-01-01 08:00:00', null,
    null, null, null, 13, 16);

-- PATIENT
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('PATIENT', 4, false, 'patient1@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Sima', 3, '$2a$10$Ok/Y8/DyGxWOwmBvDN/YNewyOAZy2cgOqs/Z7YVZoMpnxJtmYYpEe', '12345670', 'Simic', 0, null, null, null, null, 'Stari Most',
    'Sremska Mitrovica', 'Srbija', '12345', null, null);

-- NURSE
insert into users (type, id, deleted, email, enabled, last_password_reset_date, first_name, login_numbers, password,
    phone, last_name, version, rating_avg, rating_count, shift_end, shift_start, address, city, country, social_security_number,
    clinic_id, specialty) values ('NURSE', 7, false, 'nurse1@maildrop.cc', true, '2020-02-05 17:37:04.385',
    'Sekana', 3, '$2a$10$ZjlYSjuFbIBFX2/nUo3kJu7Q4Hu7IRdr/365nXg3DoiqndpxOA2Vu', '12345670', 'Simic', 0, null, null, null, null, 'Stari Most',
    'Sremska Mitrovica', 'Srbija', '12345', 2, null);

insert into user_authority (user_id, authority_id) values (20, 1);
insert into user_authority (user_id, authority_id) values (4, 5);
insert into user_authority (user_id, authority_id) values (6, 8);
insert into user_authority (user_id, authority_id) values (7, 9);

-- Room
insert into room (id, deleted, name, type, clinic_id) values (11, false, 'EKG ordinacija', 0, 2);

-- Examination
insert into examination (id, discount, end_date_time, predefined, start_date_time, status, version, clinic_id, doctor_id, nurse_id, patient_id, room_id, examination_type_id)
    values (12, 5, '2020-05-21 10:00:00', false, '2020-03-21 09:00:00', 0, 0, 2, 6, 7, 4, 11, 10);

insert into examination (id, discount, end_date_time, predefined, start_date_time, status, version, clinic_id, doctor_id, nurse_id, patient_id, room_id, examination_type_id)
    values (19, 0, '2020-05-21 10:00:00', false, '2020-03-21 09:00:00', 1, 0, 2, 6, 7, 4, 11, 10);

-- DOCTOR ABSENCE
insert into doctor_absence (id, end_date, reason_admin, reason_staff, start_date, status, type, doctor_id)
    values  (18, '2020-06-21', null, '', '2020-06-11', 1, 0, 17);

-- Examination requests
insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
             room_id, examination_type_id) values (14, '1970-01-01 10:00:00', '2020-03-21', '1970-01-01 09:00:00', 0, 2, 6, 4, null, 10);

