
insert into clock_record
(employee_id, check_in_terminal, check_in_date)
values (1,'A2378KJH',to_timestamp('2018-12-25T12:00:00Z', 'YYYY-MM-DD"T"HH24:MI:SS"Z"'));

insert into presence
(employee_id,status,last_update,clock_record_id)
values(1,'out', to_timestamp('2018-12-25T14:00:00Z', 'YYYY-MM-DD"T"HH24:MI:SS"Z"'), 1);

update clock_record
set check_out_terminal='A2378KJH', check_out_date=to_timestamp('2018-12-25T14:00:00Z', 'YYYY-MM-DD"T"HH24:MI:SS"Z"')
where id = 1;
