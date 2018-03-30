package org.song.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Teacher {
    private int id;
    private String name;
    private Map<Integer, Student> students;

    public Teacher(int id, String name) {
        this.id = id;
        this.name = name;
        this.students = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addStudent(final Integer id, final Student student) {
        students.put(id, student);
    }

    public void addStudent(final Student student) {
        addStudent(student.getId(), student);
    }

    public Map<Integer, Student> getStudents() {
        return Collections.unmodifiableMap(students);
    }
}
