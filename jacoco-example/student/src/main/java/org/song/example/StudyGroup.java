package org.song.example;

import java.util.HashMap;

public class StudyGroup {
	private HashMap<Integer, Student> students;
	
	public StudyGroup() {
		students = new HashMap<>();
	}
	
	public void addStudent(Student student) {
		int id = student.getId();
		students.put(id, student);
	}
	
	public Student getStudent(int id) {
		return students.get(id);
	}
	
	public void removeStudent(int id) {
		students.remove(id);
	}
	
	public void clear() {
		students.clear();
	}
	
	public int getGroupSize() {
		return students.size();
	}
}
