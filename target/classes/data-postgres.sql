-- insert into operation_type (id, deleted, name, price, clinic_id) values (1120, false, 'Ime oper', 96, null);
-- update examination_type set price=35 where name='Tip1';

--CLINIC

--  insert into clinic (id, address, city, deleted, description, name, rating_avg, rating_count) values (100000, 'Blagoja Parovica',
--  'Novi Sad', false, 'Fina klinika', 'Gradska bolnica', 4.8, 5);

--   insert into clinic (id, address, city, deleted, description, name, rating_avg, rating_count) values (100013, 'Blagoja Parovica',
--   'Novi Sad', false, 'Fina klinika', 'Gradska bolnica', 4.8, 5);


--EXAMINATION TYPE
-- insert into examination_type (id, deleted, name, price, clinic_id) values (1502, false, 'Tip1', 55, null);

--    insert into clinic (id, address, city, deleted, description, name, rating_avg, rating_count) values (100000, 'Blagoja Parovica',
--                        'Novi Sad', false, 'Fina klinika', 'Gradska bolnica', 4.8, 5);
--    insert into examination_type (id, deleted, name, price, clinic_id) values (1007, false, 'Pregled prstiju', 77, 100000);
--    insert into examination_type (id, deleted, name, price, clinic_id) values (1135, false, 'Pregled creva', 55, 100000);
--OPERATION_TYPE

--    insert into operation_type (id, deleted, name, price, clinic_id) values (1151, false, 'Operacija slepog creva', 966, 100000);

--    insert into operation_type (id, deleted, name, price, clinic_id) values (1008, false, 'Ime oper 1', 96, 100000);
--    insert into operation_type (id, deleted, name, price, clinic_id) values (1009, false, 'Ime oper 2', 96, 100000);
--    insert into operation_type (id, deleted, name, price, clinic_id) values (1010, false, 'Ime oper 3', 96, 100000);
--    insert into operation_type (id, deleted, name, price, clinic_id) values (1011, false, 'Ime oper 4', 96, 100000);
--    insert into operation_type (id, deleted, name, price, clinic_id) values (1143, false, 'Operacija slepog creva', 966, 100000);

--ROOM
--    insert into room (id, deleted, name, type, clinic_id) values (984, false, 'Soba 1', 1, 100000);
--    insert into room (id, deleted, name, type, clinic_id) values (985, false, 'Soba 2', 1, 100000); 
--   insert into room (id, deleted, name, type, clinic_id) values (1005, false, 'EKG ordinacija', 0, 100000);

--EXAMINATION
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1050, 5, '2020-02-09 08:00:00', false, '2020-02-09 07:15:00',
--    3, 100000, 34, 53, 110, 67, 18);
--
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1001, 5, '2020-02-09 08:00:00', false, '2020-02-09 07:20:00',
--    1, 100000, 34, 53, 85, 67, 18);
--
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1002, 5, '2020-02-09 08:00:00', false, '2020-02-09 07:0021:00',
--    1, 100000, 34, 53, 85, 67, 18);
--
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1119, 5, '2020-05-21 10:00:00', false, '2020-05-21 09:00:00',
--    1, 100000, 9, 47, 124, 23, 5);
-- --
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1120, 5, '2020-05-21 11:00:00', false, '2020-05-21 10:15:00',
--    1, 100000, 9, 47, 109, 23, 5);

--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1113, 5, '2020-02-19 10:00:00', false, '2020-02-19 09:00:00',
--    1, 100000, 9, 47, 112, 23, 5);
--
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1114, 5, '2020-01-19 00:50:00', false, '2020-01-19 00:35:00',
--    1, 100000, 9, 47, 115, 23, 5);
--
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1115, 5, '2020-01-19 00:50:00', false, '2020-01-19 00:35:00',
--    1, 100000, 9, 47, 118, 23, 5);
--
--    insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--    nurse_id, patient_id, room_id, examination_type_id) values (1116, 5, '2020-01-19 00:50:00', false, '2020-01-19 00:35:00',
--    1, 100000, 9, 47, 118, 23, 5);
--
--     delete from examination where id=1100;

--     insert into examination (id, discount, end_date_time, predefined, start_date_time, status, clinic_id, doctor_id,
--     nurse_id, patient_id, room_id, examination_type_id, version) values (1100, 5, '2020-02-19 10:50:00', true, '2020-02-19 10:35:00',
--     0, 100000, 11, 13, null, 15, 10, 0);


--OPERATION
--
--     insert into operation (id, avg_rating, end_date_time, start_date_time, status, clinic_id, patient_id,
--     room_id, operation_type_id, record_id) values (100000, 0, '2020-07-08 11:00:00', '2020-07-08 10:00:00',
--     3, 4, 12, 24, 1118, 14);

--OPERATION DOCTORS
--     insert into operation_doctors (operation_id, doctors_id) values (100000,22);
--     insert into operation_doctors (operation_id, doctors_id) values (100000,16);

--    insert into operation (id, end_date_time, start_date_time, status, clinic_id, patient_id,
--    room_id, operation_type_id, record_id) values (1121, '2020-01-25 13:55:00', '2020-01-22 12:29:00',
--    1, 100000, 118, 25, 7, 120);
--
--    insert into operation (id, end_date_time, start_date_time, status, clinic_id, patient_id,
--    room_id, operation_type_id, record_id) values (1122, '2020-01-23 11:55:00', '2020-01-23 11:34:00',
--    1, 100000, 118, 25, 7, 120);
--
--    insert into operation (id, end_date_time, start_date_time, status, clinic_id, patient_id,
--    room_id, operation_type_id, record_id) values (1123, '2020-01-23 11:55:00', '2020-01-23 11:29:00',
--    1, 100000, 118, 25, 7, 120);
--
--      insert into operation (id, end_date_time, start_date_time, status, clinic_id, patient_id,
--    room_id, operation_type_id, record_id) values (1124, '2020-01-25 13:55:00', '2020-01-22 12:29:00',
--    1, 100000, 118, 25, 7, 120);
--
--    insert into operation (id, end_date_time, start_date_time, status, clinic_id, patient_id,
--    room_id, operation_type_id, record_id) values (1125, '2020-01-23 11:55:00', '2020-01-23 11:34:00',
--    1, 100000, 118, 25, 7, 120);
--
--    insert into operation (id, end_date_time, start_date_time, status, clinic_id, patient_id,
--    room_id, operation_type_id, record_id) values (1126, '2020-01-23 11:55:00', '2020-01-23 11:29:00',
--    1, 100000, 118, 25, 7, 120);

--OPERATION DOCTORS
--   insert into operation_doctors (operation_id, doctors_id) values (1121,9);
--   insert into operation_doctors (operation_id, doctors_id) values (1122,9);
--   insert into operation_doctors (operation_id, doctors_id) values (1123,9);
--   insert into operation_doctors (operation_id, doctors_id) values (1124,9);
--   insert into operation_doctors (operation_id, doctors_id) values (1125,9);
--   insert into operation_doctors (operation_id, doctors_id) values (1126,9);

--EXAMINATION REQUESTS
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1550, '1970-01-01 10:00:00', '2020-04-03', '1970-01-01 11:00:00', 0, 100000, 34, 85, null, 18);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1555, '1970-01-01 10:30:00', '2020-06-22', '1970-01-01 09:30:00', 0, 100000, 34, 85, null, 18);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1521, '1970-01-01 13:00:00', '2020-03-19', '1970-01-01 12:00:00', 0, 100000, 5, 12, null, 1502);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1522, '1970-01-01 13:00:00', '2020-12-31', '1970-01-01 12:00:00', 0, 100000, 5, 12, null, 1502);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1523, '1970-01-01 13:00:00', '2020-12-31', '1970-01-01 12:00:00', 0, 100000, 5, 12, null, 1502);

--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1011, '1970-01-01 10:15:00', '2020-01-28', '1970-01-01 09:15:00', 0, 100000, 9, 99, null, 5);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1012, '1970-01-01 10:30:00', '2020-05-21', '1970-01-01 09:30:00', 0, 100000, 9, 99, null, 5);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1013, '1970-01-01 11:30:00', '2020-11-30', '1970-01-01 10:30:00', 0, 100000, 9, 99, null, 5);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1014, '1970-01-01 13:00:00', '2020-02-18', '1970-01-01 12:00:00', 0, 100000, 9, 99, null, 5);
--
--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1015, '1970-01-01 10:15:00', '2020-01-28', '1970-01-01 09:15:00', 0, 100000, 9, 99, null, 5);

--       insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, examination_type_id) values (1024, '1970-01-01 13:00:00', '2020-02-18', '1970-01-01 12:00:00', 0, 100000, 9, 99, null, 5);
--
-- delete from examination_requests where id=1025;
--
-- insert into examination_requests (id, end_time, examination_date, start_time, status, clinic_id, doctor_id, patient_id,
--     room_id, examination_type_id) values (1025, '1970-01-01 10:15:00', '2020-03-28', '1970-01-01 09:15:00', 0, 100000, 11, null, null, 10);

--OPERATION REQUESTS
--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1016, '1970-01-01 15:00:00', '2020-04-20', '1970-01-01 14:00:00', 0, 100000, 9, 99, null, 7);
--
--        insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--              room_id, operation_type_id) values (1007, '1970-01-01 11:00:00', '2020-03-27', '1970-01-01 10:30:00', 0, 100000, 25, 10, null, 1124);

--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1017, '1970-01-01 10:00:00', '2020-03-27', '1970-01-01 09:00:00', 0, 100000, 9, 99, null, 7);
--
--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1018, '1970-01-01 15:00:00', '2020-04-20', '1970-01-01 14:00:00', 0, 100000, 9, 99, null, 7);
--
--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1019, '1970-01-01 10:00:00', '2020-03-27', '1970-01-01 09:00:00', 0, 100000, 9, 99, null, 7);
--
--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1020, '1970-01-01 15:00:00', '2020-04-20', '1970-01-01 14:00:00', 0, 100000, 9, 99, null, 7);
--
--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1021, '1970-01-01 10:00:00', '2020-03-27', '1970-01-01 09:00:00', 0, 100000, 9, 99, null, 7);
--
--       insert into operation_requests (id, end_time, operation_date, start_time, status, clinic_id, doctor_id, patient_id,
--             room_id, operation_type_id) values (1022, '1970-01-01 15:00:00', '2020-04-20', '1970-01-01 14:00:00', 0, 100000, 9, 99, null, 7);

update clinic set version = 0 where version is null;
update examination_type set version = 0 where version is null;
update operation_type set version = 0 where version is null;
update room set version = 0 where version is null;
update users set version = 0 where version is null;
update examination set version = 0 where version is null;