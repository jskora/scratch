package org.song.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TeacherTest {

    Teacher teacher;

    @Before
    public void before() {
        teacher = new Teacher(1, "Zeus");
        teacher.addStudent(101, new Student(101, "student 101", 101));
        teacher.addStudent(102, new Student(102, "student 102", 102));
    }

    @Test
    public void testTeacher() {
        assertEquals("Zeus", teacher.getName());
        assertEquals(1, teacher.getId());

        assertEquals(2, teacher.getStudents().size());

        assertEquals(102, teacher.getStudents().get(102).getScore());
        assertEquals(101, teacher.getStudents().get(101).getId());
    }
}