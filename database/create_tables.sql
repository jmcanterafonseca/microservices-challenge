BEGIN;

CREATE TABLE Building
(
    id serial NOT NULL,
    name varchar(50),
    address varchar(100),
    CONSTRAINT "pkey_Building" PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE Employee
(
    id serial NOT NULL,
    dni character varying(12) NOT NULL,
    name character varying(256) NOT NULL,
    work_building_id integer NOT NULL,
    CONSTRAINT "pkey_Employee" PRIMARY KEY (id),
    CONSTRAINT "Employee_Building" FOREIGN KEY (work_building_id) REFERENCES Building (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
CREATE INDEX ON Employee (dni);

CREATE TABLE Terminal
(
    id VARCHAR(20) NOT NULL,
    building_id integer NOT NULL,
    CONSTRAINT "pkey_Terminal" PRIMARY KEY (id),
    CONSTRAINT "Terminal_Building" FOREIGN KEY (building_id) REFERENCES Building (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE Clock_Record
(
    id varchar(50),
    employee_id integer NOT NULL,
    check_in_date timestamp with time zone NOT NULL,
    check_out_date timestamp with time zone,
    check_in_terminal varchar(20) NOT NULL,
    check_out_terminal varchar(20),
    CONSTRAINT "Clock_Record_pkey" PRIMARY KEY (id),
    CONSTRAINT "Clock_Employee" FOREIGN KEY (employee_id) REFERENCES Employee (id),
    CONSTRAINT "Clock_Terminal1" FOREIGN KEY (check_in_terminal) REFERENCES Terminal (id),
    CONSTRAINT "Clock_Terminal2" FOREIGN KEY (check_out_terminal) REFERENCES Terminal (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
CREATE INDEX ON Clock_Record (employee_id);
CREATE INDEX ON Clock_Record (check_in_date);

CREATE TYPE Status_Type AS ENUM ('in', 'out');
CREATE TABLE Presence
(
    employee_id integer NOT NULL,
    last_update timestamp with time zone NOT NULL,
    status Status_Type NOT NULL,
    clock_record_id varchar(50) NOT NULL,
    CONSTRAINT "Presence_pkey" PRIMARY KEY (employee_id),
    CONSTRAINT "Presence_Employee" FOREIGN KEY (employee_id) REFERENCES Employee (id),
    CONSTRAINT "Presence_Clock_Record" FOREIGN KEY (clock_record_id) REFERENCES Clock_Record (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
CREATE INDEX ON Presence (employee_id);

CREATE TABLE Timesheet_Record
(
    id varchar(50),
    employee_id integer NOT NULL,
    work_date date NOT NULL,
    worked_hours numeric(4,2) NOT NULL,
    last_update timestamp with time zone NOT NULL,
    CONSTRAINT "Timesheet_pkey" PRIMARY KEY (id),
    CONSTRAINT "Timesheet_Employee" FOREIGN KEY (employee_id) REFERENCES Employee (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE INDEX ON Timesheet_Record (work_date);
CREATE INDEX ON Timesheet_Record (employee_id);
CREATE INDEX ON Timesheet_Record (last_update);


CREATE TYPE Alarm_Status_Type AS ENUM ('new', 'ack', 'closed');
CREATE TYPE Alarm_Category_Type AS ENUM ('day_absence', 'temporal_absence', 'late_checkout', 'late_checkin');
CREATE TABLE Alarm
(
    id serial NOT NULL,
    employee_id integer NOT NULL,
    alarm_date date NOT NULL,
    status Alarm_Status_Type NOT NULL,
    category Alarm_Category_Type NOT NULL,
    description varchar(200),
    last_update timestamp with time zone NOT NULL,
    CONSTRAINT "Alarm_pkey" PRIMARY KEY (id),
    CONSTRAINT "Alarm_Employee" FOREIGN KEY (employee_id) REFERENCES Employee (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE INDEX ON Alarm (employee_id);
CREATE INDEX ON Alarm (alarm_date);
CREATE INDEX ON Alarm (status);


COMMIT;
