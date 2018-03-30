package org.song.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class StudentTest {

    private Student student;

    @Before
    public void setUp() throws Exception {
        student = new Student(1001, "student 1001", 91);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(1001, student.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("student 1001", student.getName());
    }

    @Test
    public void testGetScore() throws Exception {
        assertEquals(91, student.getScore());
    }

}