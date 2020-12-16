-- Students
insert into student (student_id, first_name, last_name) values (1, 'John', 'Doe');
insert into student (student_id, first_name, last_name) values (2, 'Jane', 'Doe');

-- Addresses
insert into address (student_student_id, city, street) values (1, 'City', 'Street');
insert into address (student_student_id, city, street) values (2, 'City', 'Street');

-- Grades
insert into grade (grade_id, student_id, grade_value) values (1, 1, 10);
insert into grade (grade_id, student_id, grade_value) values (2, 1, 5);
insert into grade (grade_id, student_id, grade_value) values (3, 2, 10);

-- Courses
insert into course (course_id, course_name) values (1, 'First');
insert into course (course_id, course_name) values (2, 'Second');
insert into course (course_id, course_name) values (3, 'Third');

-- Student Course Mappings
insert into student_course (student_id, course_id) values (1, 1);
insert into student_course (student_id, course_id) values (1, 2);
insert into student_course (student_id, course_id) values (2, 1);
insert into student_course (student_id, course_id) values (2, 3);
