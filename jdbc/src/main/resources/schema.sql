-- For PostrgeSQL the SERIAL type could be used as well to generate primary key values
-- Sequences decouple the identifier generation from the row insert
-- Sequences offer more flexibility relative to how the next value is generated (for example, you can start at 100)
create sequence student_seq;

create table if not exists student (
--  id int auto_increment primary key,
  id bigint default student_seq.nextval primary key,
  first_name varchar(100),
  last_name varchar(100)
);

