package org.song.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ITTeacher {

    final static int NUM_STUDENTS = 10;

    private Teacher teacher;
    private Student[] students = new Student[NUM_STUDENTS];

    @Before
    public void before() {
        teacher = new Teacher(99,"Zeus");
        for (int i = 0; i < NUM_STUDENTS; i++) {
            students[i] = new Student((100 + i), "student " + Long.toString(100 + i), (100 + i));
        }
        for (final Student currStudent : students) {
            teacher.addStudent(currStudent);
        }
    }

    @Test
    public void testITTeacher() {
        assertEquals(students.length, teacher.getStudents().size());
        assertEquals("Zeus", teacher.getName());
        assertEquals(99, teacher.getId());

        assertEquals(students[0].getId(), teacher.getStudents().get(students[0].getId()).getId());
        assertEquals(students[0].getScore(), teacher.getStudents().get(students[0].getId()).getScore());
        assertEquals(students[0].getName(), teacher.getStudents().get(students[0].getId()).getName());
    }
}
