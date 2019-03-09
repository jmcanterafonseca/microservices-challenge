BEGIN;

insert into building
(name, address)
values ('Atica', 'Via de las dos Castillas, Pozuelo de Alarcón');

insert into terminal
(id,building_id)
values ('A2378KJH',1);

insert into terminal
(id,building_id)
values ('B5721ABC', 1);

insert into employee
(dni,name, work_building_id)
values ('12772139D','José Manuel Cantera Fonseca', 1);

insert into employee
(dni,name, work_building_id)
values ('12345678K','Luis Alfonso Alvarez Toledo', 1);

insert into employee
(dni,name, work_building_id)
values ('12000555','Efren Luis Fernandez', 1);

insert into employee
(dni,name, work_building_id)
values ('test','Test', 1);

COMMIT;
